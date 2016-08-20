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

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Stored representation of a Firebase notification.
 */
public class Notification {
  @NonNull
  private final Long id;

  @NonNull
  private final Long sentTime;

  private final boolean read;

  @Nullable
  private final String title;

  @NonNull
  private final String message;

  public Notification(
      @NonNull Long id,
      @NonNull Long sentTime,
      boolean read,
      @Nullable String title,
      @NonNull String message) {
    this.id = id;
    this.sentTime = sentTime;
    this.read = read;
    this.message = message;
    this.title = title;
  }

  /**
   * Convenience method to convert a {@code Cursor} to {@code Notification}. Field order found in
   * {@code com.g11x.checklistapp.data.Database.Notification.PROJECTION}.
   */
  public static
  @NonNull
  Notification fromCursor(Cursor cursor) {
    return new Notification(
        cursor.getLong(0),
        cursor.getLong(1),
        cursor.getShort(2) > 0,
        cursor.getString(3),
        cursor.getString(4));
  }

  @NonNull
  public Long getId() {
    return id;
  }

  @NonNull
  public Long getSentTime() {
    return sentTime;
  }

  public boolean isRead() {
    return read;
  }

  @Nullable
  public String getTitle() {
    return title;
  }

  @NonNull
  public String getMessage() {
    return message;
  }
}
