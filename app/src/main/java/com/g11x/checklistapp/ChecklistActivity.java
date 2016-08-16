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
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.g11x.checklistapp.data.Checklist;
import com.g11x.checklistapp.data.ChecklistItem;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ChecklistActivity extends AppCompatActivity {

  private FirebaseRecyclerAdapter<ChecklistItem, ChecklistItemHolder> checklistAdapter;
  private DatabaseReference databaseRef;
  private final List<ChecklistItem> checklistItems = new ArrayList<>();

  private void addChildListeners(DatabaseReference reference) {
    ChildEventListener childEventListener = new ChildEventListener() {
      @Override
      public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        // Parse all current children and store locally, for now.
        ChecklistItem item = dataSnapshot.getValue(ChecklistItem.class);
        int originalSize = checklistItems.size();
        checklistItems.add(item);
      }

      @Override
      public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        // Do nothing, for now.  Should change list of items.
      }

      @Override
      public void onChildRemoved(DataSnapshot dataSnapshot) {
        // Do nothing, for now.  Should change list of items.
      }

      @Override
      public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        // Do nothing, for now.  Should re-order list of items.
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        Log.e("Firebase", "Could not read data from remote database.");
        View view = findViewById(R.id.recyclerview_checklist);
        Snackbar.make(view, "Failed database read", Snackbar.LENGTH_SHORT).show();
      }
    };
    reference.addChildEventListener(childEventListener);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_checklist);

    databaseRef = FirebaseDatabase.getInstance().getReference()
        .child("checklists")
        .child("basic");
    addChildListeners(databaseRef);
    Checklist basicChecklist = Checklist.of(checklistItems);

    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview_checklist);
    recyclerView.setHasFixedSize(true);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(linearLayoutManager);
    checklistAdapter = new FirebaseRecyclerAdapter<ChecklistItem, ChecklistItemHolder>(
        ChecklistItem.class, R.layout.view_checklist_item, ChecklistItemHolder.class,
        databaseRef) {
      @Override
      protected void populateViewHolder(
          ChecklistItemHolder itemHolder, ChecklistItem model, int position) {
        itemHolder.setText(model.getName());
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

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.about:
        startActivity(new Intent(this, AboutActivity.class));
        break;
    }
    return super.onOptionsItemSelected(item);
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
  }
}
