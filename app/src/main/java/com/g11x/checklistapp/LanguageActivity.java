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

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Displays a language selection screen to allow the user to override the system defaults.
 */
public class LanguageActivity extends NavigationActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_language);

    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.main_content);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setAdapter(new LanguageSelectionAdapter());
  }

  @Override
  protected int getNavDrawerItemIndex() {
    return NavigationActivity.NAVDRAWER_ITEM_LANGUAGE;
  }

  static class LanguageSelectionAdapter extends RecyclerView.Adapter<LanguageSelectionAdapter.ViewHolder> {
    private String[] dataSet;

    LanguageSelectionAdapter() {
      dataSet = new String[] { "Device default (English)", "English", "Espanol", "Russian" };
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View v = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.activity_language_item, parent, false);
      return new ViewHolder((CardView)v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
      holder.textView.setText(dataSet[position]);
    }

    @Override
    public int getItemCount() {
      return dataSet.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
      private TextView textView;
      ViewHolder(final CardView view) {
        super(view);
        textView = (TextView)view.findViewById(R.id.checkbox);
        view.setOnClickListener(this);
      }

      @Override
      public void onClick(View view) {

      }
    }
  }
}
