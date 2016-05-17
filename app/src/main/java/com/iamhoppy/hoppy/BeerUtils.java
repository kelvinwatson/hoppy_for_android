package com.iamhoppy.hoppy;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BeerUtils {
    private static final String TAG = "BeerUtils";
    public Activity activity;
    private String defaultEventBeerData;

    public BeerUtils(Activity activity){
        this.activity = activity;
    }

    public void getBeers(String firstName, String lastName, String facebookCredential, int eventId){
        RequestQueue queue = Volley.newRequestQueue(activity);
        String url = "http://45.58.38.34:8080/startUp/" + firstName + "/" + lastName + "/" + facebookCredential + "/?eventId=" + eventId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Response is: " + response);
                        defaultEventBeerData = response;
                        try{
                            JSONObject startUpDataJSONObj = new JSONObject(defaultEventBeerData);
                            DefaultEventAllBeers.beers = parseBeers(startUpDataJSONObj, "beers");
                            DefaultEventAllBeers.favoriteBeers = parseBeers(startUpDataJSONObj, "favorites");
                        } catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        queue.add(stringRequest);
    }

    /* Parses JSON object and saves to Beer class */
    private List<Beer> parseBeers(JSONObject startUpDataJSONObj, String param) throws JSONException {
        JSONArray beersJSONArr = startUpDataJSONObj.getJSONArray(param);
        List<Beer> tempBeers = new ArrayList<>();
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

}
