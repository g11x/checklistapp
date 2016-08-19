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

package com.g11x.checklistapp.data;

import android.net.Uri;
import android.support.annotation.Nullable;

import com.g11x.checklistapp.language.Language;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Map;

@SuppressWarnings("unused")
@IgnoreExtraProperties
public class ChecklistItem {
  private String name;
  private String location;
  private String description;
  private String directionsUrl;
  private String email;
  private String phone;
  private Uri directions;
  private Map<String, Map<String, String>> alt;
  private boolean isDone;

  public ChecklistItem() {
    // Default constructor required for calls to DataSnapshot.getValue(ChecklistItem.class)
  }

  @SuppressWarnings("WeakerAccess")
  public String getName() {
    return name;
  }

  public String getName(@Nullable Language language) {
    if (language == null) {
      return getName();
    }

    if (alt == null) {
      return name;
    }

    Map<String, String> languageStrings = alt.get(language.getCode());

    if (languageStrings == null) {
      return name;
    }

    String string = languageStrings.get("name");

    if (string == null) {
      return name;
    } else {
      return string;
    }
  }

  public boolean isDone() {
    return isDone;
  }

  public void setDone(boolean done) {
    isDone = done;
  }

  @SuppressWarnings("unused")
  public String getEmail() {
    return email;
  }

  @SuppressWarnings("unused")
  public String getPhone() {
    return phone;
  }

  @SuppressWarnings("unused")
  public String getLocation() {
    return location;
  }

  @SuppressWarnings("WeakerAccess")
  public String getDescription() {
    return description;
  }

  public String getDescription(@Nullable Language language) {
    if (language == null) {
      return getDescription();
    }

    if (alt == null) {
      return description;
    }

    Map<String, String> languageStrings = alt.get(language.getCode());

    if (languageStrings == null) {
      return description;
    }

    String string = languageStrings.get("description");

    if (string == null) {
      return description;
    } else {
      return string;
    }
  }

  @SuppressWarnings("unused")
  public String getDirectionsUrl() {
    return directionsUrl;
  }

  public Uri getDirections() {
    if (directions == null) {
      directions = Uri.parse(directionsUrl);
    }
    return directions;
  }

  @SuppressWarnings("unused")
  public Map<String, Map<String, String>> getAlt() {
    return alt;
  }
}
