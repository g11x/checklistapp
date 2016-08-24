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

import android.content.ContentResolver;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.g11x.checklistapp.data.ChecklistItem;
import com.g11x.checklistapp.data.Database;
import com.g11x.checklistapp.language.Language;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChecklistItemActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

  private static final int LOADER_ID = 9002;
  private ChecklistItem checklistItem;
  private Language language;
  private ContentObserver checklistContentObserver = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    setContentView(R.layout.activity_checklist_item);

    language = AppPreferences.getLanguageOverride(this);

    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReferenceFromUrl(
        this.getIntent().getStringExtra("databaseRefUrl"));

    databaseRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        checklistItem = dataSnapshot.getValue(ChecklistItem.class);
        createUI();
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        Log.e("", "Unable to fetch database item.");
      }
    });

    getSupportLoaderManager().initLoader(LOADER_ID, null, this);

    ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
    scrollView.setFadingEdgeLength(150);
  }

  private void createUI() {
    TextView name = (TextView) findViewById(R.id.name);
    name.setText(checklistItem.getName(language));

    TextView description = (TextView) findViewById(R.id.description);
    description.setText(checklistItem.getDescription(language));

    Button getDirections = (Button) findViewById(R.id.directions);
    getDirections.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        ChecklistItemActivity.this.onClickGetDirections();
      }
    });

    Button doneness = (Button) findViewById(R.id.doneness);
    if (checklistItem.isDone()) {
      doneness.setTextColor(ContextCompat.getColor(ChecklistItemActivity.this, R.color.primary));
      doneness.setPaintFlags(doneness.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
      doneness.setText(getResources().getString(R.string.complete));
      doneness.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_box_accent_24dp, 0, 0, 0);
    } else {
      doneness.setTextColor(Color.BLACK);
      doneness.setPaintFlags(doneness.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
      doneness.setText(getResources().getString(R.string.mark_as_done));
      doneness.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_box_outline_blank_black_24dp, 0, 0, 0);
    }
    doneness.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        ChecklistItemActivity.this.onClickIsDone();
      }
    });

    Button sendEmail = (Button) findViewById(R.id.send_email);
    if (checklistItem.getEmail() != null && !checklistItem.getEmail().equals("")) {
      sendEmail.setVisibility(View.VISIBLE);
      sendEmail.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Intent intent = new Intent(Intent.ACTION_SENDTO);
          intent.setData(Uri.parse("mailto:"));
          intent.putExtra(Intent.EXTRA_EMAIL, checklistItem.getEmail());
          intent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.sent_from_rst_checklist_app));
          if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(intent, "Send e-mail"));
          }
        }
      });
    } else {
      sendEmail.setVisibility(View.GONE);
    }

    Button call = (Button) findViewById(R.id.call);
    if (checklistItem.getPhone() != null && !checklistItem.getPhone().equals("")) {
      call.setVisibility(View.VISIBLE);
      call.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Intent intent = new Intent(Intent.ACTION_DIAL);
          intent.setData(Uri.parse("tel:" + checklistItem.getPhone()));
          if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
          }
        }
      });
    } else {
      call.setVisibility(View.GONE);
    }
  }

  private void onClickIsDone() {
    ContentResolver contentResolver = getContentResolver();
    checklistItem.setDone(contentResolver, !checklistItem.isDone());
  }

  private void onClickGetDirections() {
    startActivity(new Intent(Intent.ACTION_VIEW, checklistItem.getDirections()));
  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    if (checklistContentObserver != null) {
      getContentResolver().unregisterContentObserver(checklistContentObserver);
    }
    checklistContentObserver = new ContentObserver(new Handler()) {
      @Override
      public void onChange(boolean selfChange) {
        getSupportLoaderManager().restartLoader(LOADER_ID, null, ChecklistItemActivity.this);
        super.onChange(selfChange);
      }

      @Override
      public void onChange(boolean selfChange, Uri uri) {
        getSupportLoaderManager().restartLoader(LOADER_ID, null, ChecklistItemActivity.this);
        super.onChange(selfChange, uri);
      }
    };
    getContentResolver().registerContentObserver(Database.ChecklistItem.CONTENT_URI, true,
        checklistContentObserver);

    return new CursorLoader(ChecklistItemActivity.this,
        Database.ChecklistItem.CONTENT_URI,
        ChecklistItem.PROJECTION,
        null,
        null,
        null);
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
    checklistItem.setDoneWithoutUpdatingContent(false);
    while (cursor.moveToNext()) {
      if (checklistItem.getHash().equals(cursor.getString(ChecklistItem.ITEM_HASH_COLUMN_INDEX))) {
        boolean isDone = cursor.getInt(ChecklistItem.DONE_COLUMN_INDEX) != 0;
        checklistItem.setDoneWithoutUpdatingContent(isDone);
      }
    }
    createUI();
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {
    // don't bother telling the adapter that data has changed, just leave it be with the stale
    // data.
  }

}
