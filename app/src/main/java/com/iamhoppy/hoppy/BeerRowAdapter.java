package com.iamhoppy.hoppy;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

class BeerRowAdapter extends ArrayAdapter<Beer> {
    private static final String TAG = "com.iamhoppy.hoppy";
    private Context context;
    private URL url;
    private HttpURLConnection urlConnection;
    private User user;

    public BeerRowAdapter(Context context, List beers, User user) {
        super(context, R.layout.custom_beer_row, beers);
        this.context = context;
        this.user = user;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inf = LayoutInflater.from(getContext());
        View customView = inf.inflate(R.layout.custom_beer_row, parent, false);
        final Beer singleBeerItem = getItem(position);
        //Reference all views and perform event handling for each
        ImageView breweryLogo = (ImageView) customView.findViewById(R.id.breweryLogo);
        Picasso.with(context)
                .load(singleBeerItem.getBreweryLogoURL())
                .fit()
                .centerInside()
                .into(breweryLogo);

        TextView beerName = (TextView) customView.findViewById(R.id.beerName);
        TextView breweryName = (TextView) customView.findViewById(R.id.breweryName);
        TextView beerType = (TextView) customView.findViewById(R.id.beerType);
        TextView beerABVIBU = (TextView) customView.findViewById(R.id.beerABVIBU);
        TextView averageRating = (TextView) customView.findViewById(R.id.score);

        ToggleButton favoriteToggle = (ToggleButton) customView.findViewById(R.id.favoriteToggle);
        if (singleBeerItem.isFavorited()) {
            favoriteToggle.setChecked(true);
        } else {
            favoriteToggle.setChecked(false);
        }
        favoriteToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                final boolean isCheckedFinal = isChecked;
                singleBeerItem.setFavorited(isChecked);
                if(isChecked){
                    generateRequestFavorite("addFavorites", true, singleBeerItem.getId());
                } else {
                    generateRequestFavorite("removeFavorites", false, singleBeerItem.getId());
                }

            }
        });

        breweryLogo.setImageResource(R.drawable.alameda);
        beerName.setText(singleBeerItem.getName());
        breweryName.setText(singleBeerItem.getBrewery());
        beerType.setText(singleBeerItem.getType());
        if (singleBeerItem.getAverageRating() == 0.0) {
            averageRating.setText(String.format("%.1f", 3.0));
        } else {
            averageRating.setText(String.format("%.1f", singleBeerItem.getAverageRating()));
        }
        if (singleBeerItem.getAbv() == null && singleBeerItem.getIbu() != null) {
            beerABVIBU.setText("IBU " + singleBeerItem.getIbu());
        } else if (singleBeerItem.getAbv() != null && singleBeerItem.getIbu() == null) {
            beerABVIBU.setText("ABV " + singleBeerItem.getAbv());
        } else if (singleBeerItem.getAbv() != null && singleBeerItem.getIbu() != null) {
            beerABVIBU.setText("ABV " + singleBeerItem.getAbv() + ", IBU " + singleBeerItem.getIbu());
        } else { //both null
            beerABVIBU.setText("");
        }
        return customView;
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

    private void generateRequestFavorite(String addOrRemove, final boolean isAdded, final int beerId){
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://45.58.38.34:8080/"+addOrRemove+"/" + user.getId() + "/" + beerId;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("BeerRowAdapter",response);
                    ((DefaultEventAllBeers)context).setFavoriteUI(isAdded, beerId);
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
        queue.add(stringRequest);

    }

}
