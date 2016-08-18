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

import android.support.annotation.Nullable;
import android.support.annotation.NonNull;

/**
 * Stored representation of a Firebase notification.
 */

public class Notification {

  @Nullable
  private final String title;

  @NonNull
  private final String message;

  public Notification(@NonNull String message, @Nullable String title) {
    this.message = message;
    this.title = title;
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
