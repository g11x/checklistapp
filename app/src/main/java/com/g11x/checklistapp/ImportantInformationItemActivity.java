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

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.g11x.checklistapp.data.Database;

public class ImportantInformationItemActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_important_information_item);

    Button create = (Button) findViewById(R.id.create);
    create.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        ImportantInformationItemActivity.this.onClickCreate();
      }
    });
  }

  @Override
  protected void onStart() {
    super.onStart();

    EditText editText = (EditText)findViewById(R.id.title);
    editText.requestFocus();
    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
  }

  private void onClickCreate() {
    EditText title = (EditText) findViewById(R.id.title);
    String titleText = title.getText().toString();
    Intent intent = new Intent(this, ImportantInformationActivity.class);
    intent.putExtra("title", titleText);

    ContentValues newValues = new ContentValues();
    newValues.put(Database.ImportantInformation.INFO_COLUMN, titleText);
    Uri titleUri = getContentResolver().insert(
        Database.ImportantInformation.CONTENT_URI,
        newValues
    );

    startActivity(intent);
  }
}
