package com.iamhoppy.hoppy;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

//Service runs in bg, no UI, user unaware

public class FetchDefaultEventAllBeers extends Service {
    private static final String TAG = "com.iamhoppy.hoppy";
    private HttpURLConnection urlConnection;
    private String firstName;
    private String lastName;
    private String facebookCredential;
    private boolean isRefresh;
    private int eventId;
    private boolean hasEventId = false;
    private URL url;

    public FetchDefaultEventAllBeers() {
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        try {
            if (intent != null) {
                firstName = intent.getStringExtra("firstName");
                lastName = intent.getStringExtra("lastName");
                facebookCredential = intent.getStringExtra("facebookCredential");
                if (intent.hasExtra("eventId")) {
                    eventId = intent.getIntExtra("eventId", -1);
                    hasEventId = true;
                }
                isRefresh = intent.getBooleanExtra("isRefresh", false);
            }
            Runnable r = new Runnable() {   //MUST place service code in thread(req'd for Service class)
                @Override
                public void run() {
                    //TODO: retrieve all beers for default event from database
                    //http://developer.android.com/reference/java/net/HttpURLConnection.html
                    //http://stackoverflow.com/questions/8376072/whats-the-readstream-method-i-just-can-not-find-it-anywhere
                    try {
                        if (hasEventId) {
                            url = new URL("http://45.58.38.34:8080/startUp/" + firstName + "/" + lastName + "/" + facebookCredential + "/?eventId=" + eventId);
                        } else {
                            url = new URL("http://45.58.38.34:8080/startUp/" + firstName + "/" + lastName + "/" + facebookCredential);
                        }
                        System.out.println("Calling: http://45.58.38.34:8080/startUp/" + firstName + "/" + lastName + "/" + facebookCredential);
                        urlConnection = (HttpURLConnection) url.openConnection();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                        String beerData = readStream(in);
                        Intent intent = new Intent();
                        intent.setAction("com.iamhoppy.hoppy.beers");
                        intent.putExtra("DefaultEventBeerData", beerData);
                        intent.putExtra("isRefresh", isRefresh);
                        sendBroadcast(intent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        urlConnection.disconnect();
                    }
                }
            };
            Thread getDefaultEventAllBeersThread = new Thread(r);
            getDefaultEventAllBeersThread.start();
            return Service.START_STICKY;
        } catch (Exception e) {
            e.printStackTrace();
            return Service.START_NOT_STICKY;
        }
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

    @Override
    public void onDestroy() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
