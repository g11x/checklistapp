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

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.g11x.checklistapp.data.Database;

/**
 * Adapter for displaying content from a CursorLoader in a RecyclerView
 *
 * This seems to be a common way of handling this. I could not find any specific
 * platform/official recommendation for accomplishing this, but there are a number of
 * implementations that all look very similar. In chronological order:
 *
 * - https://gist.github.com/Shywim/127f207e7248fe48400b
 * - https://gist.github.com/skyfishjy/443b7448f59be978bc59
 * - http://quanturium.github.io/2015/04/19/using-cursors-with-the-new-recyclerview/
 * - https://forums.bignerdranch.com/t/using-a-recyclerview-with-a-loader-cursorloader/8286
 */
abstract class RecyclerViewCursorAdapter<VH extends RecyclerView.ViewHolder>
    extends RecyclerView.Adapter<VH> {
  private Cursor cursor;
  private boolean dataValid;
  private int rowIDColumn;

  RecyclerViewCursorAdapter(Cursor cursor) {
    setCursor(cursor);
		setHasStableIds(true);
  }

  @Override
  public int getItemCount() {
    if (dataValid && cursor != null) {
      return cursor.getCount();
    } else {
      return 0;
    }
  }

  @Override
  public long getItemId(int position) {
    if (hasStableIds() && dataValid && cursor != null) {
      if (cursor.moveToPosition(position)) {
        return cursor.getLong(rowIDColumn);
      } else {
        return RecyclerView.NO_ID;
      }
    } else {
      return RecyclerView.NO_ID;
    }
  }

  @Override
  public void onBindViewHolder(VH holder, int position) {
    if (!dataValid) {
      throw new IllegalStateException("onBindViewHolder called without a valid cursor");
    }

    if (!cursor.moveToPosition(position)) {
      @SuppressLint("DefaultLocale") String message =
          String.format("could not move cursor to position %d", position);
      throw new IllegalStateException(message);
    }

    onBindViewHolder(holder, cursor);
  }

  abstract void onBindViewHolder(VH holder, Cursor cursor);

  private void setCursor(Cursor cursor) {
    this.cursor = cursor;
    dataValid = cursor != null;
    rowIDColumn = cursor != null? cursor.getColumnIndexOrThrow(Database.ID_COLUMN) : -1;
  }

  @SuppressWarnings("UnusedReturnValue")
  @Nullable
  public Cursor swapCursor(Cursor newCursor) {
    if (newCursor == cursor) {
      return null;
    }

    Cursor oldCursor = cursor;
    setCursor(newCursor);

    if (newCursor != null) {
      notifyDataSetChanged();
    } else {
      notifyItemRangeRemoved(0, oldCursor.getCount());
    }

    return oldCursor;
  }
}
