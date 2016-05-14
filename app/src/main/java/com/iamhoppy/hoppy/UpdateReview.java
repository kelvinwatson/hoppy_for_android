package com.iamhoppy.hoppy;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class UpdateReview extends Service {
    private static final String TAG = "com.iamhoppy.hoppy";
    URL url;
    HttpURLConnection urlConnection;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            final int userID = intent.getIntExtra("userID", 0);
            final int beerID = intent.getIntExtra("beerID", 0);
            final double rating = intent.getDoubleExtra("rating", -1.0);
            final String comment = intent.getStringExtra("comment");
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        url = new URL("http://45.58.38.34:8080/addReview/" + userID + "/" + beerID + "/" + rating + "/" + URLEncoder.encode(comment, "UTF-8").replace("+", "%20"));
                        urlConnection = (HttpURLConnection) url.openConnection();
                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                        String response = readStream(in);
                        JSONObject respObj = new JSONObject(response);
                        if (respObj.getBoolean("success")) {
                            Intent intent = new Intent();
                            intent.setAction("com.iamhoppy.hoppy.reviewDone");
                            intent.putExtra("beerID", beerID);
                            intent.putExtra("userID", userID);
                            intent.putExtra("rating", rating);
                            intent.putExtra("comment", comment);
                            intent.putExtra("success", true);
                            sendBroadcast(intent);
                        }
                    } catch (Exception e) {
                    }
                }
            });
            thread.start();
            return Service.START_STICKY;
        } catch (Exception e) {
            e.printStackTrace();
            return Service.START_NOT_STICKY;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while (i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }
}
