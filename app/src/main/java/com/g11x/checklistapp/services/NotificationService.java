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

package com.g11x.checklistapp.services;


import android.content.ContentValues;
import android.util.Log;

import com.g11x.checklistapp.data.Database;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Handles incoming messages from Firebase.
 */
public class NotificationService extends FirebaseMessagingService {

  private static final String TAG = "NotificationService";

  public void onMessageReceived(RemoteMessage message) {
    super.onMessageReceived(message);

    RemoteMessage.Notification notification = message.getNotification();

    Log.d(TAG, "Received notification: " + message.getNotification().getBody());

    // NOTE: Message expiration date not present in message payload, appears to only be server-side.
    ContentValues newValues = new ContentValues();
    newValues.put(Database.Notification.MESSAGE_COLUMN, notification.getBody());
    newValues.put(Database.Notification.READ_COLUMN, false);
    newValues.put(Database.Notification.SENT_TIME, message.getSentTime());
    String title = notification.getTitle();
    if (title != null && !title.isEmpty()) {
      newValues.put(Database.Notification.TITLE_COLUMN, title);
    }
    getContentResolver().insert(
        Database.Notification.CONTENT_URI,
        newValues
    );

    // TODO: Provide some affordance to display the message when the app is in the foreground.
    // See https://firebase.google.com/docs/cloud-messaging/android/receive for more details.
  }

}
