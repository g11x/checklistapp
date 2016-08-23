/*
 * Copyright © 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.g11x.checklistapp.language;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;

import com.g11x.checklistapp.AppPreferences;

import java.util.Locale;

/**
 * Delegate methods that can be used to apply the user overriden preferred language to a
 * {@link Context}.
 */
public class PreferredLanguageSupport {
  private static final Locale DEFAULT_LOCALE = Locale.getDefault();

  public static void applyPreferredLanguage(@NonNull Context context) {
    Language language = AppPreferences.getLanguageOverride(context);
    if (language != null && language != Language.SystemDefault) {
      applyPreferredDefaultLanguage(context, language);
    } else {
      unsetPreferredDefaultLanguage(context);
    }
  }

  private static void applyPreferredDefaultLanguage(@NonNull Context context, @NonNull Language language) {
    Resources resources = context.getResources();
    DisplayMetrics dm = resources.getDisplayMetrics();
    Configuration conf = resources.getConfiguration();
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
      //noinspection deprecation Can't remove until minSdk > 17
      conf.locale = new Locale(language.getCode());
    } else {
      conf.setLocale(new Locale(language.getCode()));
    }
    resources.updateConfiguration(conf, dm);
  }

  private static void unsetPreferredDefaultLanguage(@NonNull Context context) {
    Resources resources = context.getResources();
    DisplayMetrics dm = resources.getDisplayMetrics();
    Configuration conf = resources.getConfiguration();
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
      //noinspection deprecation Can't remove until minSdk > 17
      conf.locale = DEFAULT_LOCALE;
    } else {
      conf.setLocale(DEFAULT_LOCALE);
    }
    resources.updateConfiguration(conf, dm);
  }
}
