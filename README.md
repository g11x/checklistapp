# RST Checklist App

![RST Checklist app logo][logo]

This app helps refugees in central Texas by providing them a checklist of resettlement TODO's and notifications from [Refugee Services of Texas (RST)][RST].

## Try it out

[Download RST Checklist on the Play Store][app].

![Screenshots of the app's welcome, checklist, and checklist item activities][screenshots]

We've been using the latest 2.2 version of the canary channel of Android Studio to develop, so if you want to hack on it, it's probably easiest to [download that][as], clone the project, and run the app. If you're on a stable channel, you may need to tweak the `com.android.tools.build:gradle` dependency version in [the top level `build.gradle`][buildgradle].

## Translations process

We need translations in two areas of this app:

1. The Android-standard [`strings.xml` resource files][strings]
2. The Firebase database used to store the checklist items. This file is mirrored [in the source][db]

To streamline the process, we've got [a little script][csvtool] to get a CSV of the translations:

    stringtool.py tocsv > strings.csv

We import that to a Google Sheet where missing translations are highlighted in red:

> https://docs.google.com/spreadsheets/d/1w2vv6Tt5uvjKLLHX4M6kv8yFDN-sSvwuMUz2MbeFNjY/edit#gid=0

And that can be exported to CSV again, run through our tool, and easily imported back into the app with a clean diff to track changes:

    cat strings-from-google-sheet.csv | stringtool.py tostrings

See some red on that sheet under a language you speak? Let us know!t

## Why?

Maybe you've seen some recent news that says we're facing the largest refugee crisis since World War II. Maybe you've thought that you'd really like to help the victims of this crisis, but you've got no idea where to start.
 
When it comes to refugees, they're literally showing up on the doorstep of your community, so helping out is as easy as showing some hospitality! This app takes a small step in that direction. We're building something to ease the transition, and hopefully they'll find our home a bit better as a result.

## Want to help?

Thanks!

- Show some hospitality! Visit some refugees in their home. Invite them to yours. [Volunteer to take them to appointments around town][RST volunteer]. [Teach them some of your job skills][MRC volunteer].
- Help translate the app

<h3><small>You're still here! Have some legalese:</small></h3>

<small>The original authors work for Google, so the code is all &copy; Google Inc., but this is *not* an official Google project.</small>

[RST]: http://www.rstx.org
[RST volunteer]: http://www.rstx.org/volunteer.html
[MRC volunteer]: http://www.mrcaustin.org/community-services/
[strings]: https://github.com/g11x/checklistapp/blob/master/app/src/main/res/values/strings.xml
[db]: https://github.com/g11x/checklistapp/blob/master/app/testdata/checklistappdev-export.json
[csvtool]: https://github.com/g11x/checklistapp/blob/master/bin/stringtool.py
[app]: https://play.google.com/store/apps/details?id=com.g11x.checklistapp.rst
[buildgradle]: https://github.com/g11x/checklistapp/blob/master/build.gradle
[as]: http://tools.android.com/download/studio/canary
[logo]: http://i.imgur.com/RDpLZmI.png
[screenshots]: http://i.imgur.com/Ajg5ZFM.jpg