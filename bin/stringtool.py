#!/usr/bin/env python
# -*- coding: utf-8 -*-

"""\
Tool for converting to/from string resources. This app has strings that need to
be translated in 2 locations:

- The Firebase database (mirrored in app/testdata/checklistappdev-export.json)
- The strings.xml resource files

As a general process for soliciting translations and updating the app, we would
like to be able to copy all of these to a Google sheet, where translators can
input translations, and then back to the source code. This tool handles both
with a minimal diff footprint to make reviewing easier.

Examples:

  # Output all of the strings to a CSV for pasting into Google Sheets
  $ stringtool.py tocsv > strings.csv

  # Given a CSV from Google Sheets, update this repository with missing strings
  $ cat strings.csv | stringtool.py tostrings
"""

import argparse, collections, copy, csv, HTMLParser, itertools, json, os, re, sys
import xml.dom.minidom
import xml.etree.ElementTree

JSON_DB_FILENAME = 'app/testdata/checklistappdev-export.json'
TRANSLATABLE_FIELDS = ('name', 'description')
DEFAULT_LANGUAGE = 'en'
LOCALES = ('uk', 'ru', 'ar', 'es')
STRINGS_FILE_RE = re.compile(r'^strings\.xml$')


def encode_tuple((k, v), encoding='utf-8'):
  return (k.encode(encoding), v.encode(encoding) if v != None else v)


def encode_dict(d, encoding='utf-8'):
  return dict(map(lambda t: encode_tuple(t, encoding=encoding), d.iteritems()))


def decode_tuple((k, v), encoding='utf-8'):
  return (k.decode(encoding), v.decode(encoding) if v != None else v)


def decode_dict(d, encoding='utf-8'):
  return dict(map(lambda t: decode_tuple(t, encoding=encoding), d.iteritems()))


def db_translations():
  db = None
  with open(JSON_DB_FILENAME) as infile:
    db = json.load(open(JSON_DB_FILENAME))
  for index, item in enumerate(db['checklists']['basic']):
    for field in TRANSLATABLE_FIELDS:
      record = {
        'location': 'db'.format(index),
        'field': '[{}]{}'.format(index, field),
        DEFAULT_LANGUAGE: item[field]
      }
      for locale in LOCALES:
        record[locale] = item.get('alt', {}).get(locale, {}).get(field, None)
      yield record


def strings_xml_filenames():
  for (directory, _, files) in os.walk('app/src/main/res'):
    for file_ in files:
      if STRINGS_FILE_RE.match(file_):
        yield os.path.join(directory, file_)


def language_from_filename(filename):
  values_dir = os.path.split(os.path.dirname(filename))[1]
  try:
    return values_dir.rsplit('-')[1]
  except IndexError:
    return DEFAULT_LANGUAGE


def filename_without_locale(filename):
  """Something like 'res/values-ru/strings.xml' → 'res/values/strings.xml'"""
  res_dir = os.path.split(os.path.dirname(filename))[0]
  return os.path.join(res_dir, 'values', os.path.basename(filename))


def translation_to_filename_with_locale(translation, locale):
  """Given a translation object and a locale, return the locale specific path,
  i.e. res/values-{LOCALE}/strings.xml"""
  location = translation['location']
  res_dir = os.path.split(os.path.dirname(location))[0]
  values_dir = 'values' if locale == DEFAULT_LANGUAGE else 'values-' + locale
  return os.path.join(res_dir, values_dir, os.path.basename(location))


def language_translation_from_filename(filename):
  language = language_from_filename(filename)
  for event, element in xml.etree.ElementTree.iterparse(filename):
    if element.tag == 'string':
      # a <string> element may contain markup elements, so we can't just take
      # the 'text'. beyond that, ~awesome~ etree will html escape non-ascii
      # stuff, so we've got to decode the result.
      text = u'' + (element.text or u'')
      text += u''.join(xml.etree.ElementTree.tostring(e) for e in element)
      html_parser = HTMLParser.HTMLParser()
      text = html_parser.unescape(text)
      yield {
        'location': filename_without_locale(filename),
        'field': element.attrib['name'],
        language: text,
      }


def code_translations():
  translation_map = collections.defaultdict(lambda: {})
  for filename in strings_xml_filenames():
    for language_translation in language_translation_from_filename(filename):
      key = language_translation['location'] + language_translation['field']
      translation_map[key].update(language_translation)
  return translation_map.values()


def translations_to_csv(translations, outfile):
  fieldnames = ('location', 'field', DEFAULT_LANGUAGE) + LOCALES
  writer = csv.DictWriter(outfile, fieldnames=fieldnames)
  writer.writeheader()
  for translation in translations:
    writer.writerow(encode_dict(translation))


def csv_to_translations(infile):
  return (decode_dict(translation, encoding=infile.encoding or 'utf-8')
      for translation in csv.DictReader(infile))


def group_translations_by_file(translations):
  file_map = collections.defaultdict(lambda: [])
  for translation in translations:
    if translation['location'] == 'db':
      file_map[translation['location']].append(translation)
    else:
      for locale in LOCALES + (DEFAULT_LANGUAGE,):
        if locale in translation:
          filename = translation_to_filename_with_locale(translation, locale)
          file_map[filename].append(translation)
  return dict(file_map.iteritems())


def update_db_object_with_translations(db, translations):
  db = copy.deepcopy(db)
  checklist = db['checklists']['basic']
  for translation in translations:
    raw_field = translation['field']
    index = int(raw_field.lstrip('[').split(']')[0])
    field = raw_field.split(']')[1]
    checklist[index][field] = translation[DEFAULT_LANGUAGE]
    for locale in LOCALES:
      if translation.get(locale, '') != '':
        if locale not in checklist[index]['alt']:
          checklist[index]['alt'][locale] = {}
        checklist[index]['alt'][locale][field] = translation[locale]
  return db


def update_db_with_translations(translations):
  db = None
  with open(JSON_DB_FILENAME) as infile:
    db = json.load(infile, object_pairs_hook=collections.OrderedDict)
  db = update_db_object_with_translations(db, translations)
  with open(JSON_DB_FILENAME, 'w') as outfile:
    json.dump(db, outfile, separators=(',', ': '), indent=2, encoding='utf-8',
              ensure_ascii=False)


# a better programmer would break this up into logical pieces of work, but xml
# is such a bummer, so this is a big hack.
def update_xml_with_translations(filename, translations):
  locale = language_from_filename(filename)
  translation_map = collections.OrderedDict()
  copyright_notice = None
  with open(filename) as infile:
    for event, element in xml.etree.ElementTree.iterparse(infile):
      if element.tag == 'string':
        text = element.text or ''
        text += ''.join(xml.etree.ElementTree.tostring(e) for e in element)
        translation_map[element.attrib['name']] = text
      infile.seek(0)
      copyright_notice = xml.dom.minidom.parse(infile).childNodes[0].toxml()
  for translation in translations:
    if translation.get(locale, '') != '':
      translation_map[translation['field']] = translation[locale]
  with open(filename, 'w') as outfile:
    outfile.write(copyright_notice.encode('utf-8') + '\n\n<resources>\n')
    for key, value in translation_map.iteritems():

      value = value.encode('utf-8')
      outfile.write('  <string name="{}">{}</string>\n'.format(key, value))
    outfile.write('</resources>\n')


def strings_to_csv(outfile=sys.stdout):
  translations_to_csv(itertools.chain(code_translations(), db_translations()),
      outfile=outfile)


def csv_to_strings(infile=sys.stdin):
  translations_by_file = group_translations_by_file(csv_to_translations(infile))
  for filename, translations in translations_by_file.iteritems():
    if filename == 'db':
      update_db_with_translations(map(encode_dict, translations_by_file['db']))
    else:
      update_xml_with_translations(filename, translations)


if __name__ == '__main__':
  parser = argparse.ArgumentParser(description=__doc__,
      formatter_class=argparse.RawDescriptionHelpFormatter)
  parser.add_argument('action', choices=('tostrings', 'tocsv'))
  if parser.parse_args().action == 'tostrings':
    csv_to_strings()
  else:
    strings_to_csv()

