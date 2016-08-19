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

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class ChecklistItem {
  private String name;
  private String location;
  private String description;
  private String directionsUrl;
  private String email;
  private String phone;
  private Uri directions;
  private boolean isDone;

  public ChecklistItem() {
    // Default constructor required for calls to DataSnapshot.getValue(ChecklistItem.class)
  }

  private ChecklistItem(String name, String description, boolean isDone, String location, String directionsUrl, String email, String phone) {
    this.name = name;
    this.description = description;
    this.isDone = isDone;
    this.location = location;
    this.directionsUrl = directionsUrl;
    this.email = email;
    this.phone = phone;
  }

  public String getName() {
    return name;
  }

  public boolean isDone() {
    return isDone;
  }

  public void setDone(boolean done) {
    isDone = done;
  }

  public static ChecklistItem of(String name, String description, boolean isDone, String location, Uri directions, String email, String phone) {
    return new ChecklistItem(name, description, isDone, location, directions.toString(), email, phone);
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

  public String getDescription() {
    return description;
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
}
