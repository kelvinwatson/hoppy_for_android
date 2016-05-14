package com.iamhoppy.hoppy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.facebook.appevents.AppEventsLogger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* Displays Beer List for Default Event */
public class DefaultEventAllBeers extends AppCompatActivity {
    private static final String TAG = "DEAB";

    private List<Beer> beers = new ArrayList<>();
    private List<Beer> favoriteBeers = new ArrayList<>();
    private List<Event> events = new ArrayList<>();
    private User user = new User();

    private BeerDataReceiver beerDataReceiver;
    private FavoriteReceiver favoriteReceiver;
    private ReviewReceiver reviewReceiver;

    private String defaultEventBeerData;

    private boolean newlyCreated = true;
    //private Parcelable state;
    private ListAdapter beerAdapter;
    private ListView beerList;

    public boolean onKeyDown(int keycode, KeyEvent event) {
        if (keycode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
        }
        return super.onKeyDown(keycode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");
        setContentView(R.layout.activity_default_event_all_beer);
        /* Set Buttons */
        setButtons();
    }

    private void setButtons(){
        /* Button to view all beers */
        final Button allBeersButton = (Button) findViewById(R.id.allBeersButton);
        final Button bucketListButton = (Button) findViewById(R.id.favoriteBeersButton);
        //allBeersButton.setPaintFlags(allBeersButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        allBeersButton.setTypeface(null, Typeface.BOLD);
        allBeersButton.setClickable(false);
        allBeersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bucketListButton.setClickable(true);
                allBeersButton.setClickable(false);
                bucketListButton.setPaintFlags(0);
                allBeersButton.setTypeface(null, Typeface.BOLD);
                bucketListButton.setTypeface(null, Typeface.NORMAL);
                beerAdapter = new BeerRowAdapter(getApplicationContext(), beers, user);
                beerList = (ListView) findViewById(R.id.beerList); //get reference to listview
                beerList.setAdapter(beerAdapter);
            }
        });

        /* Button to view favorite beers */
        bucketListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allBeersButton.setClickable(true);
                bucketListButton.setClickable(false);
                allBeersButton.setPaintFlags(0);
                bucketListButton.setTypeface(null, Typeface.BOLD);
                allBeersButton.setTypeface(null, Typeface.NORMAL);
                beerAdapter = new BeerRowAdapter(getApplicationContext(), favoriteBeers, user);
                final ListView beerList = (ListView) findViewById(R.id.beerList); //get reference to listview
                beerList.setAdapter(beerAdapter);
            }
        });
    }
    @Override
    protected void onRestoreInstanceState(Bundle inState) {
        super.onRestoreInstanceState(inState);
        beers = (List<Beer>) inState.getSerializable("beers");
        favoriteBeers = (List<Beer>) inState.getSerializable("favoriteBeers");
        events = (List<Event>) inState.getSerializable("events");
        user = (User)inState.getSerializable("user");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("beers", (Serializable) beers);
        outState.putSerializable("favoriteBeers", (Serializable) favoriteBeers);
        outState.putSerializable("events", (Serializable) events);
        outState.putSerializable("user", user);
    }

    /* Parses JSON object and saves to User class */
    private User parseUser(JSONObject startUpDataJSONObj, String param) throws JSONException {
        JSONObject userJSONObj = startUpDataJSONObj.getJSONObject(param);
        User tempUser = new User();
        tempUser.setFacebookCredential(userJSONObj.getString("facebook_credential"));
        tempUser.setFirstName(userJSONObj.getString("first_name"));
        tempUser.setLastName(userJSONObj.getString("last_name"));
        tempUser.setId(userJSONObj.getInt("id"));
        return tempUser;
    }

    /* Parses JSON object and saves to Event class */
    private List<Event> parseEvents(JSONObject startUpDataJSONObj, String param) throws JSONException {
        JSONArray eventsJSONArr = startUpDataJSONObj.getJSONArray(param);
        List<Event> tempEvents = new ArrayList<Event>();
        for (int i = 0, len = eventsJSONArr.length(); i < len; i++) {
            JSONObject eventObj = eventsJSONArr.getJSONObject(i);
            Event event = new Event();
            if (eventObj.has("id") && !eventObj.isNull("id")) event.setId(eventObj.getInt("id"));
            if (eventObj.has("eventName") && !eventObj.isNull("eventName"))
                event.setName(eventObj.getString("eventName"));
            if (eventObj.has("eventDate") && !eventObj.isNull("eventDate"))
                event.setDate(eventObj.getString("eventDate"));
            if (eventObj.has("logoUrl") && !eventObj.isNull("logoUrl"))
                event.setLogoURL(eventObj.getString("logoUrl"));
            if (eventObj.has("beerCount") && !eventObj.isNull("beerCount"))
                event.setBeerCount(eventObj.getInt("beerCount"));
            tempEvents.add(event);
        }
        return tempEvents;
    }

    /* Parses JSON object and saves to Beer class */
    private List<Beer> parseBeers(JSONObject startUpDataJSONObj, String param) throws JSONException {
        JSONArray beersJSONArr = startUpDataJSONObj.getJSONArray(param);
        List<Beer> tempBeers = new ArrayList<Beer>();
        for (int i = 0, len = beersJSONArr.length(); i < len; i++) {
            JSONObject beerObj = beersJSONArr.getJSONObject(i);
            Beer beer = new Beer();
            if (beerObj.has("beerID") && !beerObj.isNull("beerID")) {
                beer.setId(beerObj.getInt("beerID"));
            }
            if (beerObj.has("beerName") && !beerObj.isNull("beerName")) {
                beer.setName(beerObj.getString("beerName"));
            }
            if (beerObj.has("beerType") && !beerObj.isNull("beerType")) {
                beer.setType(beerObj.getString("beerType"));
            }
            if (beerObj.has("averageRating") && !beerObj.isNull("averageRating")) {
                beer.setAverageRating(beerObj.getDouble("averageRating"));
            }
            if (beerObj.has("beerIBU") && !beerObj.isNull("beerIBU")) {
                beer.setIbu(beerObj.getString("beerIBU"));
            }
            if (beerObj.has("beerABV") && !beerObj.isNull("beerABV")) {
                beer.setAbv(beerObj.getString("beerABV"));
            }
            if (beerObj.has("myRating")) {
                beer.setRating(beerObj.getDouble("myRating"));
            }
            if (beerObj.has("breweryName") && !beerObj.isNull("breweryName")) {
                beer.setBrewery(beerObj.getString("breweryName"));
            }
            if (beerObj.has("logoUrl") && !beerObj.isNull("logoUrl")) {
                beer.setBreweryLogoURL(beerObj.getString("logoUrl"));
            }
            if (beerObj.has("beerDescription") && !beerObj.isNull("beerDescription")) {
                beer.setDescription(beerObj.getString("beerDescription"));
            }
            if (beerObj.has("myComment") && !beerObj.getString("myComment").equals("NULL")) {
                beer.setMyComment(beerObj.getString("myComment"));
            } //else leave as default null value
            if (beerObj.has("favorited") && !beerObj.isNull("favorited")) {
                beer.setFavorited(beerObj.getBoolean("favorited"));
            } //else leave as default false
            if (beerObj.has("comments") && !beerObj.isNull("comments")) {
                JSONArray arr = beerObj.getJSONArray("comments"); //This line causes exception
                List<String> tempComments = new ArrayList<String>();
                for (int j = 0, arrLen = arr.length(); j < arrLen; j++) {
                    tempComments.add(arr.getString(j));
                }
                beer.setComments(tempComments);
            }
            tempBeers.add(beer);
        }
        return tempBeers;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_default_event_all_beer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* Fetch beer data */
    private void getBeerData() {
        /* Start the service to get all beers */
        Intent fetchIntent = new Intent(this, FetchDefaultEventAllBeers.class);
        try {
            if (fetchIntent != null) {
                fetchIntent.putExtra("firstName", user.getFirstName());
                fetchIntent.putExtra("lastName", user.getLastName());
                fetchIntent.putExtra("facebookCredential", user.getFacebookCredential());
                fetchIntent.putExtra("isRefresh", true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        startService(fetchIntent);
    }

    @Override
    protected void onRestart() { //called when: user switches back from recent apps or home screen launger icon; on back button from second activity

        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        beerDataReceiver = new BeerDataReceiver();
        registerReceiver(beerDataReceiver, new IntentFilter("com.iamhoppy.hoppy.beers"));
        /* Receiver for UpdateFavorites Service */
        favoriteReceiver = new FavoriteReceiver();
        registerReceiver(favoriteReceiver, new IntentFilter("com.iamhoppy.hoppy.favoriteDone"));
        /* Receiver for UpdateReview Service */
        reviewReceiver = new ReviewReceiver();
        registerReceiver(reviewReceiver, new IntentFilter("com.iamhoppy.hoppy.reviewDone"));
        /* Get default event & beer data from MainActivity, parse, and save data */
        final Bundle bundle = getIntent().getExtras();
        String defaultEventBeerData = bundle.getString("DefaultEventBeerData");
        try {
            JSONObject startUpDataJSONObj = new JSONObject(defaultEventBeerData);
            beers = parseBeers(startUpDataJSONObj, "beers");
            favoriteBeers = parseBeers(startUpDataJSONObj, "favorites");
            events = parseEvents(startUpDataJSONObj, "events");
            user = parseUser(startUpDataJSONObj, "user");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (bundle == null) return;

        /* Create event spinner */
        SpinnerAdapter eventAdapter = new EventSpinnerAdapter(this, events);
        final Spinner eventSpinner = (Spinner) findViewById(R.id.eventSpinner);
        eventSpinner.setAdapter(eventAdapter);
        eventSpinner.setOnItemSelectedListener(
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(DefaultEventAllBeers.this, "Loading...", Toast.LENGTH_SHORT).show();
                    Event selectedEvent = events.get(position);
                    //call API to fetch all beers
                    Intent fetchIntent = new Intent(DefaultEventAllBeers.this, FetchDefaultEventAllBeers.class);
                    try {
                        if (fetchIntent != null) {
                            fetchIntent.putExtra("firstName", user.getFirstName());
                            fetchIntent.putExtra("lastName", user.getLastName());
                            fetchIntent.putExtra("facebookCredential", user.getFacebookCredential());
                            fetchIntent.putExtra("eventId", selectedEvent.getId());
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    startService(fetchIntent);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            }
        );

        /* Create list of beers */
        ListAdapter beerAdapter = new BeerRowAdapter(this, beers, user);
        beerList = (ListView) findViewById(R.id.beerList); //get reference to listview
        beerList.setAdapter(beerAdapter);
        beerList.setOnItemClickListener(
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    view.setEnabled(false);
                    Beer selectedBeer = (Beer) (beerList.getItemAtPosition(position));
                    Toast.makeText(DefaultEventAllBeers.this, "Loading...", Toast.LENGTH_SHORT).show();
                    Intent viewBeerProfile = new Intent(DefaultEventAllBeers.this, BeerProfile.class);
                    viewBeerProfile.putExtra("beer", selectedBeer);
                    viewBeerProfile.putExtra("user", user);
                    startActivity(viewBeerProfile);
                    view.setEnabled(true);
                }
            }
        );

        if (!newlyCreated) {
            /* Call the fetch service */
            getBeerData();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        newlyCreated = false;
        // Logs 'app deactivate' App Event.
        //Log.d(TAG, "saving listview state @ onPause");
        //state = ((ListView) (findViewById(R.id.beerList))).onSaveInstanceState();
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        try {
            unregisterReceiver(favoriteReceiver);
        } catch (Exception e) {
            e.printStackTrace(); //ignore exception
        }
        try {
            unregisterReceiver(reviewReceiver);
        } catch (Exception e) {
            e.printStackTrace(); //ignore exception
        }
    }

    /* Broadcast receiver for reviews */
    public class ReviewReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int userId = intent.getIntExtra("userID", 0);
            int beerId = intent.getIntExtra("beerID", 0);
            double rating = intent.getDoubleExtra("rating", 0.0);
            String comment = intent.getStringExtra("comment");
            boolean success = intent.getBooleanExtra("success", false);
            if (success) {
                for (Beer beer : beers) {
                    if (beer.getId() == beerId) {
                        beer.setRating(rating);
                        beer.setMyComment(comment);
                    }
                }
                for (Beer beer : favoriteBeers) {
                    if (beer.getId() == beerId) {
                        beer.setRating(rating);
                        beer.setMyComment(comment);
                    }
                }
            }
        }
    }

    /* Broadcast receiver for favorites */
    public class FavoriteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ((Button) findViewById(R.id.favoriteBeersButton)).setClickable(false);
            ((Button) findViewById(R.id.allBeersButton)).setClickable(false);
            int userId = intent.getIntExtra("userID", 0);
            int beerId = intent.getIntExtra("beerID", 0);
            boolean success = intent.getBooleanExtra("success", false);
            boolean added = intent.getBooleanExtra("added", false);
            boolean alreadyInFavorites = false;
            boolean inFavorites = false;
            if (success) {
                if (added) {
                    int i;
                    for (i = 0; i < beers.size(); i++) {
                        if (beers.get(i).getId() == beerId) {
                            beers.get(i).setFavorited(true);
                            break;
                        }
                    }
                    alreadyInFavorites = false;
                    for (int j = 0; j < favoriteBeers.size(); j++) {
                        if (favoriteBeers.get(j).getId() == beerId) {
                            alreadyInFavorites = true;
                            break;
                        }
                    }
                    if (!alreadyInFavorites) favoriteBeers.add(beers.get(i));
                } else {
                    int i;
                    for (i = 0; i < beers.size(); i++) {
                        if (beers.get(i).getId() == beerId) {
                            beers.get(i).setFavorited(false);
                            break;
                        }
                    }
                    inFavorites = false;
                    int j;
                    for (j = 0; j < favoriteBeers.size(); j++) {
                        if (favoriteBeers.get(j).getId() == beerId) {
                            inFavorites = true;
                            break;
                        }
                    }
                    if (inFavorites) favoriteBeers.remove(j);
                    /*for(Beer b : beers) {
                        if(b.getId() == beerId) {
                            b.setFavorited(false);
                            favoriteBeers.removeAll(Collections.singleton(b));
                            break;
                        }
                    }*/
                }
                Collections.sort(favoriteBeers);
                Collections.sort(beers);
            }
            ((Button) findViewById(R.id.favoriteBeersButton)).setClickable(true);
            ((Button) findViewById(R.id.allBeersButton)).setClickable(true);
        }
    }

    /* Broadcast receiver for beer data */
    public class BeerDataReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // broadcast is detected from FetchDefaultEventAllBeers class
            defaultEventBeerData = intent.getStringExtra("DefaultEventBeerData");
             /* Parse data */
            if (defaultEventBeerData != null && !defaultEventBeerData.equals("NULL")) {
                try {
                    JSONObject startUpDataJSONObj = new JSONObject(defaultEventBeerData);
                    beers = parseBeers(startUpDataJSONObj, "beers");
                    favoriteBeers = parseBeers(startUpDataJSONObj, "favorites");
                    events = parseEvents(startUpDataJSONObj, "events");
                    user = parseUser(startUpDataJSONObj, "user");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (defaultEventBeerData == null) return;

            /* ReCreate list of beers */
            beerAdapter = new BeerRowAdapter(context, beers, user);
            beerList = (ListView) findViewById(R.id.beerList); //get reference to listview
            beerList.setAdapter(beerAdapter);
            //if (state != null) {
            //    Log.d(TAG, "trying to restore listview state..");
            //    beerList.onRestoreInstanceState(state);
            //}
        }
    }
}