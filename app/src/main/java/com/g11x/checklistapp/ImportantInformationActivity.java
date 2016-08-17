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

import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.content.Intent;
import android.support.v4.content.Loader;
import android.database.Cursor;
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

import com.g11x.checklistapp.data.Database;

import java.util.ArrayList;

/**
 * Display a list of important information items and provide the ability to create a new one.
 */
public class ImportantInformationActivity extends NavigationActivity {
  private static final String[] PROJECTION = {
      Database.ID_COLUMN,
      Database.ImportantInformation.INFO_COLUMN
  };
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

    final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.important_information_recycler_view);
    recyclerView.setHasFixedSize(true);
    LinearLayoutManager layoutManger = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(layoutManger);

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

    getSupportLoaderManager().initLoader(0, null, new LoaderManager.LoaderCallbacks<Cursor>() {
      @Override
      public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(ImportantInformationActivity.this,
            Database.ImportantInformation.CONTENT_URI,
            PROJECTION, null, null, null);
      }

      @Override
      public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (adapter == null) {
          if (cursor != null) {
            adapter = new Adapter(cursor);
            recyclerView.setAdapter(adapter);
          }
        } else {
          adapter.swapCursor(cursor);
        }
      }

      @Override
      public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
      }
    });
  }

  @Override
  protected int getNavDrawerItemIndex() {
    return NavigationActivity.NAVDRAWER_ITEM_IMPORTANT_INFO;
  }

  @Override
  protected void onStart() {
    super.onStart();
    if (adapter != null) {
      adapter.notifyDataSetChanged();
    }
  }

  private void onFloatingActionButtonClick() {
    startActivity(new Intent(this, ImportantInformationItemActivity.class));
  }

  private static class Adapter extends RecyclerViewCursorAdapter<Adapter.ViewHolder> {
    static class ViewHolder extends RecyclerView.ViewHolder {
      private final TextView textView;

      ViewHolder(LinearLayout layout) {
        super(layout);
        textView = (TextView) itemView.findViewById(R.id.data);
      }
    }

    Adapter(Cursor cursor) {
      super(cursor);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      LinearLayout layout = (LinearLayout) LayoutInflater.from(parent.getContext())
          .inflate(R.layout.view_important_information_item, parent, false);
      return new ViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, Cursor cursor) {
      holder.textView.setText(cursor.getString(1));
    }
  }
}
