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
import android.content.UriMatcher;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/** Important domain concepts for manipulating the ImportantInformation model. */
public class Database {
  public static final String NAME = "g11x_checklistapp";

  public static Uri insert(SQLiteDatabase db, Uri uri, ContentValues contentValues) {
    return Database.getTableHandler(db, uri).insert(contentValues);
  }

  public interface TableHandler {
    Uri insert(ContentValues contentValues);
  }

  /** Information representing the ImportantInformation content..*/
  public static class ImportantInformation {
    /** Important information table content URI. */
    public static final Uri CONTENT_URI = createContentUri();

    /** Info column name. */
    public static final String INFO_COLUMN = "info";

    /** A string that defines the SQL statement for creating a table. */
    public static final String CREATE_TABLE_SQL = createCreateTableSql();

    private static final String TABLE_NAME = "important_information";

    private static class ImportantInformationTable implements TableHandler {
      private final SQLiteDatabase db;
      private final Uri contentUri;

      public ImportantInformationTable(SQLiteDatabase db, Uri contentUri) {
        this.db = db;
        this.contentUri = contentUri;
      }
      
      @Override
      public Uri insert(ContentValues contentValues) {
        long id = db.insert(Database.ImportantInformation.TABLE_NAME, null, contentValues);
        return ContentUris.withAppendedId(contentUri, id);
      }
    }

    private static Uri createContentUri() {
      // TODO: Figure out how to use getString(R.string.content_provider_authority) here.
      return Uri.parse("content://com.g11x.checklistapp.provider/" + Database.NAME + "/" + TABLE_NAME);
    }

    private static String createCreateTableSql() {
      return "create table " + TABLE_NAME + " (_ID integer primary key, " + INFO_COLUMN + " text);";
    }
  }

  private static final UriMatcher URI_MATCHER = createContentRouter();

  private static UriMatcher createContentRouter() {
    UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

    matcher.addURI("com.g11x.checklistapp.provider", Database.NAME + "/" + ImportantInformation.TABLE_NAME, 1);

    return matcher;
  }

  private static TableHandler getTableHandler(SQLiteDatabase db, Uri contentUri) {
    switch (URI_MATCHER.match(contentUri)) {
      case 1:
        return new ImportantInformation.ImportantInformationTable(db, contentUri);
      default:
        throw new RuntimeException(String.format("No handler registered for %s", contentUri));
    }
  }
}
