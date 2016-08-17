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

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Important domain concepts for manipulating the ImportantInformation model.
 */
public class Database {
  public static final String NAME = "g11x_checklistapp";

  public static TableHandler getTableHandler(SQLiteDatabase db, Uri contentUri) {
    return new ImportantInformation.ImportantInformationTable(db, contentUri);
  }

  public interface TableHandler {
    Uri insert(ContentValues contentValues);
  }

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

    private static class ImportantInformationTable implements TableHandler {
      public ImportantInformationTable(SQLiteDatabase db, Uri contentUri) {}
      
      @Override
      public Uri insert(ContentValues contentValues) {
        long id = db.insert(Database.ImportantInformation.INFO_COLUMN, null, contentValues);
        return ContentUris.withAppendedId(uri, id);
      }
    }
  }
}
