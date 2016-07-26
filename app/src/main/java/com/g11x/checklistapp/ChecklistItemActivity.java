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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.g11x.checklistapp.data.ChecklistItem;
import com.g11x.checklistapp.data.ChecklistManager;

public class ChecklistItemActivity extends AppCompatActivity {

  private ChecklistItem checklistItem;
  private ToggleButton isDone;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_checklist_item);

    int index = this.getIntent().getIntExtra("index", 0);
    checklistItem = ChecklistManager.get(getApplicationContext()).getItems().get(index);

    TextView name = (TextView) findViewById(R.id.name);
    name.setText(checklistItem.getName());

    Button getDirections = (Button) findViewById(R.id.directions);
    getDirections.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        ChecklistItemActivity.this.onClickGetDirections();
      }
    });

    isDone = (ToggleButton) findViewById(R.id.doneness);
    isDone.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        ChecklistItemActivity.this.onClickIsDone();
      }
    });

    TextView description = (TextView) findViewById(R.id.description);
    description.setText(checklistItem.getDescription());
  }

  @Override
  protected void onStart() {
    super.onStart();
    isDone.setChecked(checklistItem.isDone());
  }

  private void onClickIsDone() {
    checklistItem.setDone(!checklistItem.isDone());
    ChecklistManager.save(getApplicationContext(), ChecklistManager.get(getApplicationContext()));
  }

  private void onClickGetDirections() {
    startActivity(new Intent(Intent.ACTION_VIEW, checklistItem.getDirections()));
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
}
