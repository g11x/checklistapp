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

package com.g11x.checklistapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.g11x.checklistapp.language.Language;

/**
 * Simplified class to access the app's {@link android.content.SharedPreferences}.
 */

public class AppPreferences {

  private static final String PREF_PREFERRED_LANGUAGE = "PREF_PREFERRED_LANGUAGE";
  private static final String HAS_SEEN_WELCOME_SCREEN = "HAS_SEEN_WELCOME_SCREEN";

  /**
   * Saves the user's language preference which overrides the system default.
   *
   * @param context  component context used to retrieve shared preferences
   * @param language the language identifier or null to unset
   */
  static void setLanguageOverride(@NonNull Context context,
                                  @Nullable Language language) {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
    SharedPreferences.Editor editor = sp.edit();
    if (language == null || language.equals(Language.SystemDefault)) {
      editor.remove(PREF_PREFERRED_LANGUAGE);
    } else {
      editor.putString(PREF_PREFERRED_LANGUAGE, language.name());
    }
    editor.apply();
  }

  /**
   * Returns the language override setting.
   *
   * @param context component context used to retrieve shared preferences
   */
  @NonNull
  public static Language getLanguageOverride(@NonNull Context context) {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
    String value = sp.getString(PREF_PREFERRED_LANGUAGE, null);
    if (value == null) {
      return Language.SystemDefault;
    }
    return Language.valueOf(value);
  }

  /**
   * Custom shared preference listener for the preferred language property.
   */
  static abstract class LanguageChangeListener {
    private final SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
      @Override
      public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(PREF_PREFERRED_LANGUAGE)) {
          String value = sharedPreferences.getString(s, null);
          if (value == null) {
            onChanged(Language.SystemDefault);
          } else {
            onChanged(Language.valueOf(value));
          }
        }
      }
    };

    LanguageChangeListener(@NonNull Context context) {
      SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
      sp.registerOnSharedPreferenceChangeListener(listener);
    }

    void unregister(@NonNull Context context) {
      SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
      sp.unregisterOnSharedPreferenceChangeListener(listener);
    }

    abstract void onChanged(@NonNull Language newLanguage);
  }

  static boolean hasSeenWelcomeScreen(Context context) {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
    String value = sp.getString(HAS_SEEN_WELCOME_SCREEN, "false");
    return value.equals("true");
  }

  static void setHasSeenWelcomeScreen(Context context) {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
    SharedPreferences.Editor editor = sp.edit();
    editor.putString(HAS_SEEN_WELCOME_SCREEN, "true");
    editor.apply();
  }
}
