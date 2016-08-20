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
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.g11x.checklistapp.data.ChecklistItem;
import com.g11x.checklistapp.data.Database;
import com.g11x.checklistapp.language.Language;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChecklistActivity extends NavigationActivity {

  private FirebaseRecyclerAdapter<ChecklistItem, ChecklistItemHolder> checklistAdapter;
  private Language language;

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
          final ChecklistItemHolder itemHolder, ChecklistItem model, final int position) {
        itemHolder.markDone(model.isDone(getContentResolver()));
        itemHolder.setText(model.getName(language));
        itemHolder.setDbRef(getRef(position));
        itemHolder.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Intent intent = new Intent(ChecklistActivity.this, ChecklistItemActivity.class);
            intent.putExtra("databaseRefUrl", itemHolder.databaseRefUrl);
            startActivity(intent);
            startActivity(intent);
          }
        });
      }
    };
    recyclerView.setAdapter(checklistAdapter);
  }

  @Override
  public void onLanguageChange(Language newLanguage) {
    language = newLanguage;
  }

  protected void onStart() {
    super.onStart();
    // Need to populate done status at onStart as well.
    Cursor donenessCursor = getContentResolver().query(Database.ChecklistItem.CONTENT_URI,
        ChecklistItem.PROJECTION, null, null, null);
    if (donenessCursor.moveToFirst()) {
      for (int i = 0; i < donenessCursor.getCount(); i++) {
        String itemHash = donenessCursor.getString(ChecklistItem.ITEM_HASH_COLUMN_INDEX);
        int done = donenessCursor.getInt(ChecklistItem.DONE_COLUMN_INDEX);
        donenessCursor.moveToNext();
      }
    }
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
      if (done) {
        TextView textView = (TextView) view.findViewById(R.id.info_text);
        textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
      }
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
  }
}
