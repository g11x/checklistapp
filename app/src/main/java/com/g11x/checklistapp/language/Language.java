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

import android.support.annotation.NonNull;

/**
 * Custom content languages supported by the system.
 */
public enum Language {
  Russian("ru", "RussianTBU"),
  English("en", "English"),
  Ukrainian("uk", "UkrainianTBU");

  @NonNull
  private String code;

  @NonNull
  private String nativeDescription;

  Language(@NonNull String langCode, @NonNull String nativeDesc) {
    code = langCode;
    nativeDescription = nativeDesc;
  }

  @NonNull
  public String getCode() {
    return code;
  }

  @NonNull
  public String getNativeDescription() {
    return nativeDescription;
  }
}