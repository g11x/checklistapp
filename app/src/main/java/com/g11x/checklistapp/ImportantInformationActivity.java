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

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ImportantInformationActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_important_information);

    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.important_information_recycler_view);
    recyclerView.setHasFixedSize(true);
    LinearLayoutManager layoutManger = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(layoutManger);
    String[] data = {"1", "2", "3"};
    Adapter adapter = new Adapter(data);
    recyclerView.setAdapter(adapter);
  }

  private static class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private final String[] data;

    static class ViewHolder extends RecyclerView.ViewHolder {
      private final TextView textView;

      ViewHolder(LinearLayout layout) {
        super(layout);
        textView = (TextView) itemView.findViewById(R.id.data);
      }
    }

    Adapter(String[] data) {
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
      holder.textView.setText(data[position]);
    }

    @Override
    public int getItemCount() {
      return data.length;
    }

  }
}
