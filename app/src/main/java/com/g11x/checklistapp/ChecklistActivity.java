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

package com.g11x.checklistapp;

import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.g11x.checklistapp.data.ChecklistItem;
import com.g11x.checklistapp.data.Database;
import com.g11x.checklistapp.language.Language;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChecklistActivity extends NavigationActivity implements LoaderManager.LoaderCallbacks<Cursor> {

  private static final int LOADER_ID = 9001;
  private FirebaseRecyclerAdapter<ChecklistItem, ChecklistItemHolder> checklistAdapter;
  private Language language;
  private final ArrayMap<String, Boolean> isDoneMapping = new ArrayMap<>();
  private ContentObserver checklistContentObserver = null;

  @Override
  protected int getNavDrawerItemIndex() {
    return NavigationActivity.NAVDRAWER_ITEM_CHECKLIST;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_checklist);

    language = AppPreferences.getLanguageOverride(this);

    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference()
        .child("checklists")
        .child("basic");

    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview_checklist);
    recyclerView.setHasFixedSize(true);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(linearLayoutManager);
    checklistAdapter = new FirebaseRecyclerAdapter<ChecklistItem, ChecklistItemHolder>(
        ChecklistItem.class, R.layout.view_checklist_item, ChecklistItemHolder.class,
        databaseRef) {

      @Override
      protected void populateViewHolder(
          final ChecklistItemHolder itemHolder, final ChecklistItem model, final int position) {
        Boolean isDone = isDoneMapping.get(model.getHash());
        if (isDoneMapping.get(model.getHash()) == null) {
          isDone = false;
        }
        itemHolder.markDone(isDone);
        itemHolder.setText(model.getName(language));
        itemHolder.setDbRef(getRef(position));
        itemHolder.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Intent intent = new Intent(ChecklistActivity.this, ChecklistItemActivity.class);
            intent.putExtra("databaseRefUrl", itemHolder.databaseRefUrl);
            startActivity(intent);
          }
        });
        final Boolean finalIsDone = isDone;
        itemHolder.setCheckBoxOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            model.setDone(getContentResolver(), !finalIsDone);
          }
        });
      }
    };
    recyclerView.setAdapter(checklistAdapter);

    getSupportLoaderManager().initLoader(LOADER_ID, null, this);
  }

  @Override
  public void onLanguageChange(Language newLanguage) {
    language = newLanguage;
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    checklistAdapter.cleanup();
  }

  public static class ChecklistItemHolder extends RecyclerView.ViewHolder {
    final View view;
    String databaseRefUrl;

    public ChecklistItemHolder(View itemView) {
      super(itemView);
      view = itemView;
    }

    public void markDone(boolean done) {
      TextView textView = (TextView) view.findViewById(R.id.info_text);
      if (done) {
        textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
      } else {
        textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
      }
      CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
      checkBox.setChecked(done);
    }

    public void setText(String title) {
      TextView textView = (TextView) view.findViewById(R.id.info_text);
      textView.setText(title);
    }

    public void setDbRef(DatabaseReference reference) {
      databaseRefUrl = reference.toString();
    }

    public void setOnClickListener(View.OnClickListener listener) {
      TextView textView = (TextView) view.findViewById(R.id.info_text);
      textView.setOnClickListener(listener);
    }

    void setCheckBoxOnClickListener(View.OnClickListener listener) {
      CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
      checkBox.setOnClickListener(listener);
    }
  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    if (checklistContentObserver != null) {
      getContentResolver().unregisterContentObserver(checklistContentObserver);
    }
    checklistContentObserver = new ContentObserver(new Handler()) {
      @Override
      public void onChange(boolean selfChange) {
        getSupportLoaderManager().restartLoader(LOADER_ID, null, ChecklistActivity.this);
        super.onChange(selfChange);
      }

      @Override
      public void onChange(boolean selfChange, Uri uri) {
        getSupportLoaderManager().restartLoader(LOADER_ID, null, ChecklistActivity.this);
        super.onChange(selfChange, uri);
      }
    };
    getContentResolver().registerContentObserver(Database.ChecklistItem.CONTENT_URI, true,
        checklistContentObserver);

    return new CursorLoader(ChecklistActivity.this,
        Database.ChecklistItem.CONTENT_URI,
        ChecklistItem.PROJECTION,
        null,
        null,
        null);
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
    while (cursor.moveToNext()) {
      isDoneMapping.put(cursor.getString(ChecklistItem.ITEM_HASH_COLUMN_INDEX),
          cursor.getInt(ChecklistItem.DONE_COLUMN_INDEX) != 0);
    }
    checklistAdapter.notifyDataSetChanged();
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {
    // don't bother telling the adapter that data has changed, just leave it be with the stale
    // data.
  }

}
