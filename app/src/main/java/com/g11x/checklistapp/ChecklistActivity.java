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
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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

  private ChecklistAdapter checklistAdapter;
  private DatabaseReference checklistRef;
  private final List<ChecklistItem> checklistItems = new ArrayList<>();
  private ChildEventListener childEventListener;

  private void addChildListeners(DatabaseReference reference) {
    childEventListener = new ChildEventListener() {
      @Override
      public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        // Parse all current children and store locally, for now.
        ChecklistItem item = dataSnapshot.getValue(ChecklistItem.class);
        int originalSize = checklistItems.size();
        checklistItems.add(item);
        checklistAdapter.notifyDataSetChanged();
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

    checklistRef = FirebaseDatabase.getInstance().getReference()
        .child("checklists")
        .child("basic");
    addChildListeners(checklistRef);
    Checklist basicChecklist = Checklist.of(checklistItems);

    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview_checklist);
    recyclerView.setHasFixedSize(true);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(linearLayoutManager);
    checklistAdapter = new ChecklistAdapter(basicChecklist);
    recyclerView.setAdapter(checklistAdapter);
  }

  @Override
  protected void onStart() {
    super.onStart();
    checklistAdapter.notifyDataSetChanged();
  }

  @Override
  protected void onStop() {
    super.onStop();
    if (checklistRef != null && childEventListener != null) {
      checklistRef.removeEventListener(childEventListener);
    }
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

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater menuInflater = getMenuInflater();
    menuInflater.inflate(R.menu.main, menu);
    return super.onCreateOptionsMenu(menu);
  }

  private class ChecklistAdapter extends RecyclerView.Adapter<ViewHolder> {
    final Checklist dataset;

    ChecklistAdapter(Checklist checklist) {
      this.dataset = checklist;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.view_checklist_item, parent, false);
      return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
      SpannableString spannableString = new SpannableString(dataset.getItems().get(position).getName());
      int typeface = dataset.getItems().get(position).isDone()? Typeface.NORMAL : Typeface.BOLD;
      spannableString.setSpan(new StyleSpan(typeface), 0, dataset.getItems().get(position).getName().length(), 0);
      holder.textView.setText(spannableString);
      holder.textView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          int position = holder.getAdapterPosition();
          Intent intent = new Intent(ChecklistActivity.this, ChecklistItemActivity.class);
          intent.putExtra("index", position);
          startActivity(intent);
        }
      });
    }

    @Override
    public int getItemCount() {
      return dataset.getItems().size();
    }
  }

  private class ViewHolder extends RecyclerView.ViewHolder {
    final TextView textView;

    ViewHolder(View view) {
      super(view);
      this.textView = (TextView) view.findViewById(R.id.info_text);
    }
  }
}
