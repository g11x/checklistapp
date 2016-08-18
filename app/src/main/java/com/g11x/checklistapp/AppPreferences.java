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
import android.text.TextUtils;

/**
 * Simplified class to access the app's {@link android.content.SharedPreferences}.
 */

public class AppPreferences {

  private static final String PREF_PREFERRED_LANGUAGE = "PREF_PREFERRED_LANGUAGE";

  /**
   * Returns whether the user has chosen to override the system's default language.
   *
   * @param context component context used to retrieve shared preferences
   */
  public static boolean isLanguageOverriden(@NonNull Context context) {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
    String value = sp.getString(PREF_PREFERRED_LANGUAGE, null);
    return TextUtils.isEmpty(value);
  }

  /**
   * Saves the user's language preference which overrides the system default.
   *
   * @param context            component context used to retrieve shared preferences
   * @param languageIdentifier the language identifier or null to unset
   */
  public static void setLanguageOverride(@NonNull Context context,
                                         @Nullable String languageIdentifier) {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
    SharedPreferences.Editor editor = sp.edit();
    if (languageIdentifier == null) {
      editor.remove(PREF_PREFERRED_LANGUAGE);
    } else {
      editor.putString(PREF_PREFERRED_LANGUAGE, languageIdentifier);
    }
    editor.apply();
  }

  /**
   * Custom shared preference listener for the preferred language property.
   */
  public static abstract class LanguageChangeListener {
    private final SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
      @Override
      public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(PREF_PREFERRED_LANGUAGE)) {
          onChanged(sharedPreferences.getString(s, null));
        }
      }
    };

    LanguageChangeListener(@NonNull Context context) {
      SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
      sp.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unregister(@NonNull Context context) {
      SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
      sp.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public abstract void onChanged(String newValue);
  }

}
