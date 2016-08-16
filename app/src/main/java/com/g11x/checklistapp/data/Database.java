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

/**
 * Important domain concepts for manipulating the ImportantInformation model.
 */
public class Database {
  public static final String NAME = "g11x_checklistapp";

  public static class ImportantInformation {
    // Important information table content URI.
    // TODO: Figure out how to use getString(R.string.content_provider_authority) here.
    public static final Uri CONTENT_URI;

    // Important information table info column.
    public static final String INFO_COLUMN = "info";

    private static final String TABLE_NAME = "important_information";

    static {
      CONTENT_URI = Uri.parse("content://com.g11x.checklistapp/" + Database.NAME + "/" + TABLE_NAME);
    }
  }
}
