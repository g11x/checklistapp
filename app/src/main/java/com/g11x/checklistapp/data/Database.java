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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Important domain concepts for manipulating the ImportantInformation model.
 */
public class Database {
  public static final String NAME = "g11x_checklistapp";
  public static final String COM_G11X_CHECKLISTAPP_PROVIDER = "com.g11x.checklistapp.provider";
  public static final String ID_COLUMN = "_ID";

  public static Cursor query(SQLiteDatabase db, Uri uri, String[] projection, String selection,
                             String[] selectionArgs, String sortOrder) {
    return Database.getTableHandler(db, uri).query(projection, selection, selectionArgs, sortOrder);
  }

  /**
   * Insert a set of content values into the database and table supplied. Handles all the
   * mechanics of content routing for you.
   */
  public static Uri insert(SQLiteDatabase db, Uri uri, ContentValues contentValues) {
    return Database.getTableHandler(db, uri).insert(contentValues);
  }

  /**
   * Information representing the ImportantInformation content..
   */
  public static class ImportantInformation {
    /**
     * Important information table content URI.
     */
    public static final Uri CONTENT_URI = createContentUri();

    /**
     * Info column name.
     */
    public static final String INFO_COLUMN = "info";

    /**
     * SQL statement for creating this table.
     */
    public static final String CREATE_TABLE_SQL = createCreateTableSql();

    private static final String TABLE_NAME = "important_information";

    private static class ImportantInformationTableHandler implements TableHandler {
      private final SQLiteDatabase db;
      private final Uri contentUri;

      ImportantInformationTableHandler(SQLiteDatabase db, Uri contentUri) {
        this.db = db;
        this.contentUri = contentUri;
      }

      @Override
      public Uri insert(ContentValues contentValues) {
        long id = db.insert(Database.ImportantInformation.TABLE_NAME, null, contentValues);
        return ContentUris.withAppendedId(contentUri, id);
      }

      @Override
      public Cursor query(String[] projection, String selection, String[] selectionArgs,
                          String sortOrder) {
        return db.query(Database.ImportantInformation.TABLE_NAME, projection, selection,
            selectionArgs, null, null, sortOrder);
      }
    }

    private static Uri createContentUri() {
      // TODO: Figure out how to use getString(R.string.content_provider_authority) here.
      return Uri.parse("content://" + COM_G11X_CHECKLISTAPP_PROVIDER + "/" + Database.NAME + "/" + TABLE_NAME);
    }

    private static String createCreateTableSql() {
      return "create table " + TABLE_NAME + " (" + ID_COLUMN + " integer primary key, " +
          INFO_COLUMN + " text);";
    }
  }

  public static class ChecklistItem {
    /**
     * Important information table content URI.
     */
    public static final Uri CONTENT_URI = createContentUri();

    /**
     * Done column name.
     */
    public static final String DONE_COLUMN = "done";

    /**
     * Item hash column name.
     */
    public static final String ITEM_HASH_COLUMN = "item_hash";

    /**
     * SQL statement for creating this table.
     */
    public static final String CREATE_TABLE_SQL = createCreateTableSql();

    private static final String TABLE_NAME = "checklist_item";

    private static class ChecklistItemTableHandler implements TableHandler {
      private final SQLiteDatabase db;
      private final Uri contentUri;

      ChecklistItemTableHandler(SQLiteDatabase db, Uri contentUri) {
        this.db = db;
        this.contentUri = contentUri;
      }

      @Override
      public Uri insert(ContentValues contentValues) {
        long id = db.insert(Database.ChecklistItem.TABLE_NAME, null, contentValues);
        return ContentUris.withAppendedId(contentUri, id);
      }

      @Override
      public Cursor query(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
      }
    }

    private static Uri createContentUri() {
      // TODO: Figure out how to use getString(R.string.content_provider_authority) here.
      return Uri.parse("content://" + COM_G11X_CHECKLISTAPP_PROVIDER + "/" + Database.NAME + "/" + TABLE_NAME);
    }

    private static String createCreateTableSql() {
      return "create table " + TABLE_NAME + " (_ID integer primary key, " + DONE_COLUMN + " boolean, " + ITEM_HASH_COLUMN + " text);";
    }
  }

  public static class Notification {
    /**
     * Important information table content URI.
     */
    public static final Uri CONTENT_URI = createContentUri();

    public static final String TITLE_COLUMN = "title";
    public static final String MESSAGE_COLUMN = "message";
    public static final String READ_COLUMN = "item_hash";
    public static final String SENT_TIME = "sent_time";

    /**
     * SQL statement for creating this table.
     */
    public static final String CREATE_TABLE_SQL = createCreateTableSql();

    private static final String TABLE_NAME = "notification";

    private static class NotificationTableHandler implements TableHandler {
      private final SQLiteDatabase db;
      private final Uri contentUri;

      NotificationTableHandler(SQLiteDatabase db, Uri contentUri) {
        this.db = db;
        this.contentUri = contentUri;
      }

      @Override
      public Uri insert(ContentValues contentValues) {
        long id = db.insert(Database.ChecklistItem.TABLE_NAME, null, contentValues);
        return ContentUris.withAppendedId(contentUri, id);
      }

      @Override
      public Cursor query(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
      }
    }

    private static Uri createContentUri() {
      // TODO: Figure out how to use getString(R.string.content_provider_authority) here.
      return Uri.parse("content://" + COM_G11X_CHECKLISTAPP_PROVIDER + "/" + Database.NAME + "/" + TABLE_NAME);
    }

    private static String createCreateTableSql() {
      return "create table " + TABLE_NAME + " (_ID integer primary key, " + TITLE_COLUMN + " text, " + MESSAGE_COLUMN + " text, " + READ_COLUMN + " boolean, " + SENT_TIME + " integer);";
    }
  }

  private static final int IMPORTANT_INFORMATION_TABLE_URI_MATCHER_INDEX = 1;
  private static final int CHECKLIST_ITEM_TABLE_URI_MATCHER_INDEX = 2;
  private static final int NOTIFICATION_TABLE_URI_MATCHER_INDEX = 3;

  private static final UriMatcher URI_MATCHER = createContentRouter();

  private static UriMatcher createContentRouter() {
    UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

    matcher.addURI(COM_G11X_CHECKLISTAPP_PROVIDER, Database.NAME + "/" + ImportantInformation.TABLE_NAME, IMPORTANT_INFORMATION_TABLE_URI_MATCHER_INDEX);
    matcher.addURI(COM_G11X_CHECKLISTAPP_PROVIDER, Database.NAME + "/" + ChecklistItem.TABLE_NAME, CHECKLIST_ITEM_TABLE_URI_MATCHER_INDEX);
    matcher.addURI(COM_G11X_CHECKLISTAPP_PROVIDER, Database.NAME + "/" + Notification.TABLE_NAME, NOTIFICATION_TABLE_URI_MATCHER_INDEX);

    return matcher;
  }

  private interface TableHandler {
    Uri insert(ContentValues contentValues);

    Cursor query(String[] projection, String selection, String[] selectionArgs, String sortOrder);
  }

  private static TableHandler getTableHandler(SQLiteDatabase db, Uri contentUri) {
    switch (URI_MATCHER.match(contentUri)) {
      case IMPORTANT_INFORMATION_TABLE_URI_MATCHER_INDEX:
        return new ImportantInformation.ImportantInformationTableHandler(db, contentUri);
      case CHECKLIST_ITEM_TABLE_URI_MATCHER_INDEX:
        return new ChecklistItem.ChecklistItemTableHandler(db, contentUri);
      case NOTIFICATION_TABLE_URI_MATCHER_INDEX:
        return new Notification.NotificationTableHandler(db, contentUri);
      default:
        throw new RuntimeException(String.format("No handler registered for %s", contentUri));
    }
  }
}
