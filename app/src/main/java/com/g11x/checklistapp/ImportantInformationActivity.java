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
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Display a list of important information items and provide the ability to create a new one.
 */
public class ImportantInformationActivity extends NavigationActivity {
  private static ArrayList<String> data = null;
  private Adapter adapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_important_information);

    // TODO: fetch from content provider
    if (data == null) {
      data = new ArrayList<>();
      data.add("one");
      data.add("two");
      data.add("three");
    }

    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.important_information_recycler_view);
    recyclerView.setHasFixedSize(true);
    LinearLayoutManager layoutManger = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(layoutManger);
    adapter = new Adapter(data);
    recyclerView.setAdapter(adapter);

    FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
    floatingActionButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        ImportantInformationActivity.this.onFloatingActionButtonClick();
      }
    });

    Intent intent = getIntent();

    if (intent.getExtras() != null && intent.getExtras().get("title") != null) {
      View view = findViewById(R.id.activity_important_information);
      String message = String.format(getString(R.string.created_important_information_item),
          intent.getExtras().get("title"));
      Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }
  }

  @Override
  protected int getNavDrawerItemIndex() {
    return NavigationActivity.NAVDRAWER_ITEM_IMPORTANT_INFO;
  }

  @Override
  protected void onStart() {
    super.onStart();
    adapter.notifyDataSetChanged();
  }

  private void onFloatingActionButtonClick() {
    startActivity(new Intent(this, ImportantInformationItemActivity.class));
  }

  private static class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private final ArrayList<String> data;

    static class ViewHolder extends RecyclerView.ViewHolder {
      private final TextView textView;

      ViewHolder(LinearLayout layout) {
        super(layout);
        textView = (TextView) itemView.findViewById(R.id.data);
      }
    }

    Adapter(ArrayList<String> data) {
      this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      LinearLayout layout = (LinearLayout) LayoutInflater.from(parent.getContext())
          .inflate(R.layout.view_important_information_item, parent, false);
      return new ViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
      holder.textView.setText(data.get(position));
    }

    @Override
    public int getItemCount() {
      return data.size();
    }
  }

  static ArrayList<String> getData() {
    return data;
  }
}
