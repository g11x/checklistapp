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
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (AppPreferences.hasSeenWelcomeScreen(this)) {
      goToChecklist();
      return;
    }

    setContentView(R.layout.activity_main);

    Button button = (Button) findViewById(R.id.letsgo);
    button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        AppPreferences.setHasSeenWelcomeScreen(MainActivity.this);
        goToChecklist();
      }
    });
  }

  private void goToChecklist() {
    startActivity(new Intent(MainActivity.this, ChecklistActivity.class));
  }
}
