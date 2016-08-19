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

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.g11x.checklistapp.language.PreferredLanguageSupport;

import java.util.ArrayList;


public abstract class NavigationActivity extends AppCompatActivity {

  private DrawerLayout drawerLayout;
  private ListView drawerList;
  private ActionBarDrawerToggle drawerToggle;
  private AppPreferences.LanguageChangeListener baseLanguageChangeListener;

  private CharSequence title;

  static final int NAVDRAWER_ITEM_CHECKLIST = 0;
  protected static final int NAVDRAWER_ITEM_IMPORTANT_INFO = 1;
  protected static final int NAVDRAWER_ITEM_NOTIFICATIONS = 2;
  protected static final int NAVDRAWER_ITEM_LANGUAGE = 3;
  protected static final int NAVDRAWER_ITEM_ABOUT = 4;

  private static final int[] NAVDRAWER_TITLE_RES_IDS = new int[]{
      R.string.navdrawer_item_checklist,
      R.string.navdrawer_item_important_info,
      R.string.navdrawer_item_notifications,
      R.string.navdrawer_item_language,
      R.string.navdrawer_item_about
  };

  abstract protected int getNavDrawerItemIndex();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    PreferredLanguageSupport.applyPreferredLanguage(this);
    final Activity thisActivity = NavigationActivity.this;
    baseLanguageChangeListener = new AppPreferences.LanguageChangeListener(NavigationActivity.this) {
      @Override
      public void onChanged(String newValue) {
        thisActivity.recreate();
      }
    };
  }

  @Override
  public void setContentView(@LayoutRes int layoutResID) {
    super.setContentView(layoutResID);
    setUpDrawer();
  }

  private void setUpDrawer() {

    title = getTitle();
    drawerLayout = (DrawerLayout) findViewById(R.id.main_layout);
    drawerList = (ListView) findViewById(R.id.left_drawer);

    ArrayList<String> navDrawerTitles = new ArrayList<>();
    for (int NAVDRAWER_TITLE_RES_ID : NAVDRAWER_TITLE_RES_IDS) {
      navDrawerTitles.add(getResources().getString(NAVDRAWER_TITLE_RES_ID));
    }

    // set a custom shadow that overlays the main content when the drawer opens
    drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
    // set up the drawer's list view with items and click listener
    drawerList.setAdapter(new ArrayAdapter<>(
        this, R.layout.drawer_list_item, navDrawerTitles));
    drawerList.setOnItemClickListener(new DrawerItemClickListener());

    // enable ActionBar app icon to behave as action to toggle nav drawer
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setHomeButtonEnabled(true);
      getSupportActionBar().setIcon(R.color.transparent);
    }

    // ActionBarDrawerToggle ties together the the proper interactions
    // between the sliding drawer and the action bar app icon
    drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, 0, 0) {
      public void onDrawerClosed(View view) {
        getSupportActionBar().setTitle(title);
        invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
      }

      public void onDrawerOpened(View drawerView) {
        invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
      }
    };
    drawerLayout.addDrawerListener(drawerToggle);

    // update selected item and title, then close the drawer
    drawerList.setItemChecked(getNavDrawerItemIndex(), true);
    setTitle(getResources().getString(NAVDRAWER_TITLE_RES_IDS[getNavDrawerItemIndex()]));
    drawerLayout.closeDrawer(drawerList);
  }

  @Override
  protected void onDestroy() {
    drawerLayout.removeDrawerListener(drawerToggle);
    baseLanguageChangeListener.unregister(this);
    super.onDestroy();
  }

  /* The click listener for ListView in the navigation drawer */
  private class DrawerItemClickListener implements ListView.OnItemClickListener {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      selectItem(position);
    }
  }

  private void selectItem(int index) {

    if (getNavDrawerItemIndex() != index) {

      switch (index) {
        case NAVDRAWER_ITEM_CHECKLIST:
          createBackStack(new Intent(this, ChecklistActivity.class));
          break;
        case NAVDRAWER_ITEM_IMPORTANT_INFO:
          createBackStack(new Intent(this, ImportantInformationActivity.class));
          break;
        case NAVDRAWER_ITEM_LANGUAGE:
          createBackStack(new Intent(this, LanguageActivity.class));
          break;
        case NAVDRAWER_ITEM_ABOUT:
          createBackStack(new Intent(this, AboutActivity.class));
          break;
        case NAVDRAWER_ITEM_NOTIFICATIONS:
          startActivity(new Intent(this, NotificationListActivity.class));
          break;
      }

      // switch back to current selected item
      drawerList.setItemChecked(getNavDrawerItemIndex(), true);
    }

    drawerLayout.closeDrawer(drawerList);
  }

  private void createBackStack(Intent intent) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
      TaskStackBuilder builder = TaskStackBuilder.create(this);
      builder.addNextIntentWithParentStack(intent);
      builder.startActivities();
    } else {
      startActivity(intent);
      finish();
    }
  }

  @Override
  public void setTitle(CharSequence title) {
    this.title = title;
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setTitle(this.title);
    }
  }

  /**
   * When using the ActionBarDrawerToggle, you must call it during
   * onPostCreate() and onConfigurationChanged()...
   */
  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    // Sync the toggle state after onRestoreInstanceState has occurred.
    drawerToggle.syncState();
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    // Pass any configuration change to the drawer toggles
    drawerToggle.onConfigurationChanged(newConfig);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // The action bar home/up action should open or close the drawer.
    // ActionBarDrawerToggle will take care of this.
    return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
  }

  @Override
  public void onBackPressed() {

    // Close drawer by back button
    if (drawerLayout.isDrawerOpen(drawerList)) {
      drawerLayout.closeDrawer(drawerList);
    } else {
      super.onBackPressed();
    }
  }
}
