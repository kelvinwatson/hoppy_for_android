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

/*
 * DEPRECATED
 */

public class UpdateFavorites extends Service {
    private static final String TAG = "com.iamhoppy.hoppy";
    URL url;
    HttpURLConnection urlConnection;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            final boolean isCheckedFinal = intent.getBooleanExtra("checkedFinal", true);
            final int userID = intent.getIntExtra("userID", 0);
            final int beerID = intent.getIntExtra("beerID", 0);
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (isCheckedFinal) {  // Toggle clicked
                            url = new URL("http://45.58.38.34:8080/addFavorites/" + userID + "/" + beerID);
                        } else {
                            url = new URL("http://45.58.38.34:8080/removeFavorites/" + userID + "/" + beerID);
                        }
                        urlConnection = (HttpURLConnection) url.openConnection();
                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                        String response = readStream(in);
                        JSONObject respObj = new JSONObject(response);
                        if (respObj.getBoolean("success")) {
                            Intent intent = new Intent();
                            intent.setAction("com.iamhoppy.hoppy.favoriteDone");
                            intent.putExtra("userID", userID);
                            intent.putExtra("beerID", beerID);
                            intent.putExtra("success", true);
                            intent.putExtra("added", isCheckedFinal);
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
