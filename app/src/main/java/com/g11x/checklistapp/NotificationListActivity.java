package com.g11x.checklistapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.g11x.checklistapp.data.Database;
import com.g11x.checklistapp.data.Notification;

public class NotificationListActivity extends NavigationActivity {

  private NotificationAdapter adapter;
  private RecyclerView mRecyclerView;

  @Override
  protected int getNavDrawerItemIndex() {
    return NAVDRAWER_ITEM_NOTIFICATIONS;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_notification_list);
    mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

    // use this setting to improve performance if you know that changes
    // in content do not change the layout size of the RecyclerView
    mRecyclerView.setHasFixedSize(true);

    // use a linear layout manager
    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
    mRecyclerView.setLayoutManager(mLayoutManager);

    ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
      @Override
      public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
      }

      @Override
      public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        long id = ((NotificationAdapter.ViewHolder) viewHolder).id;

        ContentValues newValues = new ContentValues();
        newValues.put(Database.Notification.READ_COLUMN, true);

        getContentResolver().update(
            Database.Notification.CONTENT_URI,
            newValues,
            Database.ID_COLUMN + " = " + id,
            null
        );
      }
    });
    helper.attachToRecyclerView(mRecyclerView);


    getSupportLoaderManager().initLoader(0, null, new LoaderManager.LoaderCallbacks<Cursor>() {
      @Override
      public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Loader<Cursor> cursor = new CursorLoader(NotificationListActivity.this,
            Database.Notification.CONTENT_URI,
            Database.Notification.PROJECTION, "NOT " + Database.Notification.READ_COLUMN, null, Database.Notification.SENT_TIME);
        return cursor;
      }

      @Override
      public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (adapter == null) {
          if (cursor != null) {
            adapter = new NotificationAdapter(cursor);
            mRecyclerView.setAdapter(adapter);
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
  protected void onStart() {
    super.onStart();
    if (adapter != null) {
      adapter.notifyDataSetChanged();
    }
  }

  private static class NotificationAdapter extends RecyclerViewCursorAdapter<NotificationAdapter.ViewHolder> {

    NotificationAdapter(Cursor cursor) {
      super(cursor);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View layout = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.view_notification_item, parent, false);
      return new ViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, Cursor cursor) {
      Notification n = Notification.fromCursor(cursor);
      holder.id = n.getId();
      holder.mBody.setText(n.getMessage());
      String title = n.getTitle();
      if (title != null && !title.isEmpty()) {
        holder.mTitle.setVisibility(View.VISIBLE);
        holder.mTitle.setText(n.getTitle());
      } else {
        holder.mTitle.setVisibility(View.GONE);
      }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
      public final TextView mTitle;
      public final TextView mBody;
      public long id;

      public ViewHolder(View v) {
        super(v);
        mTitle = (TextView) v.findViewById(R.id.notification_title);
        mBody = (TextView) v.findViewById(R.id.notification_body);
      }
    }
  }
}
