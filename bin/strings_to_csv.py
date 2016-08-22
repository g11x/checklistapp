#!/usr/bin/env python

import collections, csv, itertools, json, os, re, sys
import xml.etree.ElementTree

TRANSLATABLE_FIELDS = ('name', 'description')
DEFAULT_LANGUAGE = 'en'
LOCALES = ('uk', 'ru')
STRINGS_FILE_RE = re.compile(r'^strings\.xml$')


def encode_tuple((k, v), encoding='utf-8'):
  return (k.encode(encoding), v.encode(encoding) if v != None else v)


def encode_dict(d, encoding='utf-8'):
  return dict(map(encode_tuple, d.iteritems()))


def db_translations():
  db = json.load(open('app/testdata/checklistappdev-export.json'))
  for index, item in enumerate(db['checklists']['basic']):
    for field in TRANSLATABLE_FIELDS:
      record = {
        'location': 'db[{}]'.format(index),
        'field': field,
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
  res_dir = os.path.split(os.path.dirname(filename))[0]
  return os.path.join(res_dir, 'values', os.path.basename(filename))


def language_translation_from_filename(filename):
  language = language_from_filename(filename)
  for event, element in xml.etree.ElementTree.iterparse(filename):
    if element.tag == 'string':
      yield {
        'location': filename_without_locale(filename),
        'field': element.attrib['name'],
        language: element.text,
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


if __name__ == '__main__':
  translations_to_csv(itertools.chain(code_translations(), db_translations()),
      outfile=sys.stdout)
