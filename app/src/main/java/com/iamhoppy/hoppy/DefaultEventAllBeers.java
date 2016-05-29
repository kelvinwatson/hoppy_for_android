package com.iamhoppy.hoppy;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* Displays Beer List for Default Event */
public class DefaultEventAllBeers extends AppCompatActivity {
    private static final String TAG = "com.iamhoppy.hoppy";
    private boolean initialLoad = true;

    public static List<Beer> beers = new ArrayList<>();
    public static List<Beer> favoriteBeers = new ArrayList<>();
    private List<Event> events = new ArrayList<>();
    private Event selectedEvent;
    private User user = new User();

    private String defaultEventBeerData;

    private boolean viewingAllBeers = true;
    private boolean viewingBucketList = false;

    //private Parcelable state;
    private BeerRowAdapter beerAdapter;
    private BeerRowAdapter favoriteAdapter;
    private ListView beerList;
    private SpinnerAdapter eventAdapter;
    private Spinner eventSpinner;

    public boolean onKeyDown(int keycode, KeyEvent event) {
        if (keycode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
        }
        return super.onKeyDown(keycode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_event_all_beer);

        final Bundle bundle = getIntent().getExtras();
        defaultEventBeerData = bundle.getString("DefaultEventBeerData");
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

        setButtons();
        setBeerAdapter();
        setEventSpinner();
    }

    private void setBeerAdapter() {
        beerAdapter = new BeerRowAdapter(DefaultEventAllBeers.this, beers, user);
        beerList = (ListView) findViewById(R.id.beerList); //get reference to listview
        beerList.setAdapter(beerAdapter);
        beerList.setOnItemClickListener(
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(DefaultEventAllBeers.this, "Loading...", Toast.LENGTH_SHORT).show();
                    Beer selectedBeer = viewingAllBeers? beers.get(position) : favoriteBeers.get(position);
                    Intent viewBeerProfile = new Intent(DefaultEventAllBeers.this, BeerProfile.class);
                    viewBeerProfile.putExtra("beer", selectedBeer);
                    viewBeerProfile.putExtra("user", user);
                    viewBeerProfile.putExtra("event", selectedEvent);
                    startActivity(viewBeerProfile);
                }
            }
        );
    }

    private void repopulateBeerLists(JSONObject resp) throws JSONException{
        //beerAdapter.clear(); //NEW

        beers.clear(); //required for notifyDataSetChanged();
        List<Beer> nBrs = parseBeers(resp, "beers"); //required for notifyDataSetChanged();
        beers.addAll(nBrs); //required for notifyDataSetChanged();
        //beerAdapter.addAll(beers);

        favoriteBeers.clear();
        List<Beer> nFBrs = parseBeers(resp, "favorites");
        favoriteBeers.addAll(nFBrs);

        //beerAdapter.notifyDataSetChanged();
        if(viewingAllBeers){
        //    beerAdapter = new BeerRowAdapter(DefaultEventAllBeers.this, beers, user);
             beerAdapter.update(beers);
        } else {
        //    beerAdapter = new BeerRowAdapter(DefaultEventAllBeers.this, favoriteBeers, user);
            beerAdapter.update(favoriteBeers);
        }
        //beerList.setAdapter(beerAdapter);
    }

    /* Create event spinner */
    private void setEventSpinner(){
        eventAdapter = new EventSpinnerAdapter(this, events);
        eventSpinner = (Spinner) findViewById(R.id.eventSpinner);
        eventSpinner.setAdapter(eventAdapter);
        eventSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Toast.makeText(DefaultEventAllBeers.this, "Loading...", Toast.LENGTH_SHORT).show();
                        selectedEvent = events.get(position);
                        //call API using Volley to fetch beers for event ID
                        if(!initialLoad){
                            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                            String url = "http://45.58.38.34:8080/startUp/" + user.getFirstName() + "/" + user.getLastName() + "/" + user.getFacebookCredential() + "/?eventId=" + selectedEvent.getId();
                            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            try {
                                                repopulateBeerLists(response);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    error.printStackTrace();
                                }
                            });
                            queue.add(jsonRequest);
                        } else {
                            initialLoad=false;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                }
        );
    }

    private void setButtons(){
        /* Button to view all beers */
        final Button allBeersButton = (Button) findViewById(R.id.allBeersButton);
        final Button bucketListButton = (Button) findViewById(R.id.favoriteBeersButton);
        allBeersButton.setTypeface(null, Typeface.BOLD);
        allBeersButton.setClickable(false);
        allBeersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewingAllBeers = true;
                viewingBucketList = false;
                bucketListButton.setClickable(true);
                allBeersButton.setClickable(false);
                bucketListButton.setPaintFlags(0);
                allBeersButton.setTypeface(null, Typeface.BOLD);
                bucketListButton.setTypeface(null, Typeface.NORMAL);
                //beerAdapter = new BeerRowAdapter(DefaultEventAllBeers.this, beers, user);
                //beerList = (ListView) findViewById(R.id.beerList); //get reference to listview
                //beerList.setAdapter(beerAdapter);
                beerAdapter.update(beers);
            }
        });

        /* Button to view favorite beers */
        bucketListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewingAllBeers = false;
                viewingBucketList = true;
                allBeersButton.setClickable(true);
                bucketListButton.setClickable(false);
                allBeersButton.setPaintFlags(0);
                bucketListButton.setTypeface(null, Typeface.BOLD);
                allBeersButton.setTypeface(null, Typeface.NORMAL);
                //beerAdapter = new BeerRowAdapter(DefaultEventAllBeers.this, favoriteBeers, user);
                //beerList = (ListView) findViewById(R.id.beerList); //get reference to listview
                //beerList.setAdapter(beerAdapter);
                beerAdapter.update(favoriteBeers);
            }
        });
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
                List<String> tempComments = new ArrayList<>();
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

    @Override
    protected void onRestart() { //called when: user switches back from recent apps or home screen launger icon; on back button from second activity
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
        ((BaseAdapter)beerAdapter).notifyDataSetChanged();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }



    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void updateBeerLists(){

    }


    public void setFavoriteUI(boolean isFavorited, int beerId){
        boolean alreadyInFavorites = false;
        boolean inFavorites = false;
        int i;
        if (isFavorited) {
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
        }
        Collections.sort(favoriteBeers);
        Collections.sort(beers);
        ((BaseAdapter)beerAdapter).notifyDataSetChanged();
    }

}