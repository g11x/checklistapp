package com.g11x.checklistapp;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.g11x.checklistapp.data.Database;

/**
 * Application data repository.
 */
public class LocalRepository extends ContentProvider {
  // Defines a handle to the database helper object. The MainDatabaseHelper class is defined in a
  // following snippet.
  private MainDatabaseHelper openHelper;

  @Override
  public boolean onCreate() {
    // Creates a new helper object. This method always returns quickly. Notice that the
    // database itself isn't created or opened until SQLiteOpenHelper.getWritableDatabase
    // is called.
    openHelper = new MainDatabaseHelper(getContext());
    return true;
  }

  @Nullable
  @Override
  public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs,
                      String sortOrder) {
    return Database.query(openHelper.getWritableDatabase(), uri, projection, selection,
        selectionArgs, sortOrder);
  }

  @Nullable
  @Override
  public String getType(@NonNull Uri uri) {
    return null;
  }

  @Nullable
  @Override
  public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
    return Database.insert(openHelper.getWritableDatabase(), uri, contentValues);
  }

  @Override
  public int delete(@NonNull Uri uri, String s, String[] strings) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int update(@NonNull Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
    return Database.update(openHelper.getWritableDatabase(), uri, contentValues, selection, selectionArgs);
  }

  // Helper class that actually creates and manages the provider's underlying data repository.
  static final class MainDatabaseHelper extends SQLiteOpenHelper {
    // Instantiates an open helper for the provider's SQLite data repository. Do not do database
    // creation and upgrade here.
    MainDatabaseHelper(Context context) {
      super(context, Database.NAME, null, 1);
    }

    // Creates the data repository. This is called when the provider attempts to open the
    // repository and SQLite reports that it doesn't exist.
    public void onCreate(SQLiteDatabase db) {
      // Creates the main table
      db.execSQL(Database.ImportantInformation.CREATE_TABLE_SQL);
      db.execSQL(Database.ChecklistItem.CREATE_TABLE_SQL);
      db.execSQL(Database.Notification.CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
      // TODO: This seems useful. Learn how to use it.
    }
  }
}
