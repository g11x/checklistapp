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

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.g11x.checklistapp.data.Database;
import com.g11x.checklistapp.data.Notification;
import com.google.android.gms.gcm.GcmReceiver;

import java.util.Date;
import java.util.List;

public class ChecklistAppReceiver extends GcmReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
//    super.onReceive(context, intent);
//    for (String key : intent.getExtras().keySet()) {
//      Log.d("BLERG", key + ": " + intent.getExtras().get(key).toString() + "(" + intent.getExtras().get(key).getClass().toString());
//    }

    Object titleObject = intent.getExtras().get("gcm.notification.title");
    String title = titleObject != null? titleObject.toString() : "RST Announcement";
    Object bodyObject = intent.getExtras().get("gcm.notification.body");
    String body;
    if (bodyObject == null) {
      return;
    } else {
      body = bodyObject.toString();
    }
    Object sentTimeObject = intent.getExtras().get("google.sent_time");
    Long sentTime = sentTimeObject != null? (Long) sentTimeObject : System.currentTimeMillis();

    ContentValues newValues = new ContentValues();
    newValues.put(Database.Notification.MESSAGE_COLUMN, body);
    newValues.put(Database.Notification.READ_COLUMN, false);
    newValues.put(Database.Notification.SENT_TIME, sentTime);
    if (title != null && !title.isEmpty()) {
      newValues.put(Database.Notification.TITLE_COLUMN, title);
    }
    context.getContentResolver().insert(Database.Notification.CONTENT_URI, newValues);

    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle(title)
        .setContentText(body)
        .setAutoCancel(true);

    Intent resultIntent = new Intent(context, NotificationListActivity.class);
    TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
    taskStackBuilder.addParentStack(NotificationListActivity.class);
    taskStackBuilder.addNextIntent(resultIntent);
    PendingIntent resultPendingIntent = taskStackBuilder
        .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    notificationBuilder.setContentIntent(resultPendingIntent);
    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
    notificationManager.notify(0, notificationBuilder.build());
  }

}
