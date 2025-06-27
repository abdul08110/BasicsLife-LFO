package com.hasbro.basicslife_lfo;

import static com.hasbro.basicslife_lfo.geturl.retrofit;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import java.util.ArrayList;

public class NotificationHelper extends Service {

    private static final String CHANNEL_ID = "NOTIFICATION_CHANNEL";
    private static RequestQueue mRequestQueue;
    private static StringRequest mStringRequest;
    static JSONArray storeNotiData =new JSONArray();
    static String messagestring="";
    static String urlstring="";
    public static void fetchNotification(Context context) {

        String apiUrl = retrofit.baseUrl()+"getnotidata";
        mRequestQueue = Volley.newRequestQueue(context);
        //String Request initialized
        mStringRequest = new StringRequest(Request.Method.GET, apiUrl, response -> {
            try {
                JSONObject obj = new JSONObject(response);
                System.out.println("welcome "+obj.length());
                if(obj.length() > 0){
                    storeNotiData = obj.getJSONArray("strnotidata");

                    if (storeNotiData.length()>0) {
                        JSONArray message = storeNotiData.getJSONArray(0);

                        messagestring = (String) message.get(0);
                        urlstring = (String) message.get(1);

                        //System.out.println("Abdul "+messagestring);
                        fetchImage(context, messagestring, urlstring);

                    }
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }


        }, error -> System.out.println("Error :" + error.toString()));

        mRequestQueue.add(mStringRequest);

    }

    private static void fetchImage(Context context, String message, String imageUrl)  {
        RequestQueue queue = Volley.newRequestQueue(context);
        System.out.println("message" +message);
        ImageRequest imageRequest = new ImageRequest(imageUrl,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        showNotification(context, message, bitmap);
                    }
                },
                0, 0, null, Bitmap.Config.RGB_565,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("NotificationError", "Image Fetch Error", error);
                    }
                });

        queue.add(imageRequest);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Execute your background task here
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @SuppressLint("MissingPermission")
    private static void showNotification(Context context, String message, Bitmap image) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Alert!!!")
                .setContentText(message)
                .setLargeIcon(image)
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(image).bigLargeIcon((Bitmap) null))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder.build());
    }
}