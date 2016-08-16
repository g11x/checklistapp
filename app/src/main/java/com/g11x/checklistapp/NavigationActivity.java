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

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class NavigationActivity extends AppCompatActivity {
  private DrawerLayout drawerLayout;
  private ListView drawerList;
  private ActionBarDrawerToggle drawerToggle;

  private CharSequence title;
  private String[] menuItemTitles;
  private Class<? extends Fragment>[] menuItemClasses;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_navigation);

    title = getTitle();
    menuItemTitles = getResources().getStringArray(R.array.menu_items_array);
    menuItemClasses = new Class[]{
        ChecklistFragment.class,
        ImportantInformationFragment.class,
        LanguagesFragment.class,
        AboutFragment.class
    };
    drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    drawerList = (ListView) findViewById(R.id.left_drawer);

    // set a custom shadow that overlays the main content when the drawer opens
    drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
    // set up the drawer's list view with items and click listener
    drawerList.setAdapter(new ArrayAdapter<String>(this,
        R.layout.drawer_list_item, menuItemTitles));
    drawerList.setOnItemClickListener(new DrawerItemClickListener());

    // enable ActionBar app icon to behave as action to toggle nav drawer
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setIcon(R.color.transparent);

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

    if (savedInstanceState == null) {
      selectItem(0);
    }
  }

  @Override
  protected void onDestroy() {
    drawerLayout.removeDrawerListener(drawerToggle);
    super.onDestroy();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.main, menu);
    return super.onCreateOptionsMenu(menu);
  }

  /* The click listner for ListView in the navigation drawer */
  private class DrawerItemClickListener implements ListView.OnItemClickListener {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      selectItem(position);
    }
  }

  private void selectItem(int index) {
    // update the main content by replacing fragments
    Class<? extends Fragment> type = menuItemClasses[index];

    try {

      Fragment fragment = type.getConstructor().newInstance();
      FragmentManager fragmentManager = getSupportFragmentManager();
      fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

      // update selected item and title, then close the drawer
      drawerList.setItemChecked(index, true);
      setTitle(menuItemTitles[index]);
      drawerLayout.closeDrawer(drawerList);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void setTitle(CharSequence title) {
    this.title = title;
    getSupportActionBar().setTitle(this.title);
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
    // Pass any configuration change to the drawer toggls
    drawerToggle.onConfigurationChanged(newConfig);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // The action bar home/up action should open or close the drawer.
    // ActionBarDrawerToggle will take care of this.
    if (drawerToggle.onOptionsItemSelected(item)) {
      return true;
    }

    return super.onOptionsItemSelected(item);
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
