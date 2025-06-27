package com.hasbro.basicslife_lfo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Call the fetchNotification method to trigger the notification
        NotificationHelper.fetchNotification(context);


    }
}
