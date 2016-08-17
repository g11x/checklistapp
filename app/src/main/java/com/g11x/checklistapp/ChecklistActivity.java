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
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.g11x.checklistapp.data.ChecklistItem;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChecklistActivity extends AppCompatActivity {

  private FirebaseRecyclerAdapter<ChecklistItem, ChecklistItemHolder> checklistAdapter;
  private DatabaseReference databaseRef;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_checklist);

    databaseRef = FirebaseDatabase.getInstance().getReference()
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
        itemHolder.setText(model.getName());
        itemHolder.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Intent intent = new Intent(ChecklistActivity.this, ChecklistItemActivity.class);
            intent.putExtra("index", position);
            Log.w("zhi", "clicked on: " + getRef(position).toString());
            intent.putExtra("databaseRefUrl", getRef(position).toString());
            startActivity(intent);
          }
        });
      }
    };
    recyclerView.setAdapter(checklistAdapter);
  }

  @Override
  protected void onStart() {
    super.onStart();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    checklistAdapter.cleanup();
  }

  public static class ChecklistItemHolder extends RecyclerView.ViewHolder {
    View view;

    public ChecklistItemHolder(View itemView) {
      super(itemView);
      view = itemView;
    }

    public void setText(String title) {
      TextView textView = (TextView) view.findViewById(R.id.info_text);
      textView.setText(title);
    }

    public void setOnClickListener(View.OnClickListener listener) {
      TextView textView = (TextView) view.findViewById(R.id.info_text);
      textView.setOnClickListener(listener);
    }
  }
}
