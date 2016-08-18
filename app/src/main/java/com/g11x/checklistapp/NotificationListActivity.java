package com.g11x.checklistapp;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.g11x.checklistapp.data.Notification;

public class NotificationListActivity extends NavigationActivity {

  @Override
  protected int getNavDrawerItemIndex() {
    return NAVDRAWER_ITEM_NOTIFICATIONS;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_notification_list);
    RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

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
        // TODO: Remove item from DB (when db exists).
      }
    });
    helper.attachToRecyclerView(mRecyclerView);

    // TODO: Replace dataset with db cursor.
    Notification[] dataset = new Notification[]{
        new Notification("message 1", null),
        new Notification("message 2", "I'm a title!"),
        new Notification("message C", "I can't count :("),
        new Notification("I like fish, fishy fishy fish.", null)
    };
    RecyclerView.Adapter mAdapter = new NotificationAdapter(dataset);
    mRecyclerView.setAdapter(mAdapter);
  }

  public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private final Notification[] mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
      // each data item is just a string in this case
      public final TextView mTitle;
      public final TextView mBody;

      public ViewHolder(View v) {
        super(v);
        mTitle = (TextView) v.findViewById(R.id.notification_title);
        mBody = (TextView) v.findViewById(R.id.notification_body);
      }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public NotificationAdapter(Notification[] myDataset) {
      mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      // create a new view
      View v = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.view_notification_item, parent, false);
      return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
      Notification n = mDataset[position];
      holder.mBody.setText(n.getMessage());
      String title = n.getTitle();
      if (title != null && !title.isEmpty()) {
        holder.mTitle.setVisibility(View.VISIBLE);
        holder.mTitle.setText(n.getTitle());
      } else {
        holder.mTitle.setVisibility(View.GONE);
      }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
      return mDataset.length;
    }
  }
}
