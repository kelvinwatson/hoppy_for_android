package com.iamhoppy.hoppy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/* Beer Profile Activity */
public class BeerProfile extends AppCompatActivity {
    private static final String TAG = "BeerProfile";
    private static final String baseUrl = "http://45.58.38.34:8080/addReview/";
    public LinearLayout postInitialCommentRow;
    private Beer beer = new Beer();
    private User user = new User();
    private List<ImageView> ratingImages;
    private EditText commentTextBox;
    private Button postCommentButton;
    private String userComment;
    private TextView editCommentClickableView;
    private TextView editableCommentView;
    private TextView myNameView;
    private TextView timeView;
    private LinearLayout myCommentRow;
    private ImageView breweryLogo;

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beer_profile);

        if (savedInstanceState != null) {
            beer = (Beer) savedInstanceState.getSerializable("beer");
            user = (User) savedInstanceState.getSerializable("user");
            userComment = savedInstanceState.getString("userComment");
        } else {
            final Bundle bundle = getIntent().getExtras();
            beer = (Beer) bundle.getSerializable("beer");
            user = (User) bundle.getSerializable("user");
        }

        populateBeer();
        setRatingImages();
        setWidgets();

        /* Set visibility on whether or not user has already posted comment */
        if (beer.getMyComment() != null && !beer.getMyComment().equals("NULL") && beer.getMyComment().length() > 0) {  //user has previously recorded a comment
            postInitialCommentRow.setVisibility(View.GONE);
            commentTextBox.setVisibility(View.GONE); //gone means removed from layout and won't occupy space
            postCommentButton.setVisibility(View.GONE); //invisible means removed form layout but still occupies space
            displayMyComment();
        } else {                        //make commentTextBox and postCommentButton visible as this is the user's initial post
            postInitialCommentRow.setVisibility(View.VISIBLE);
            commentTextBox.setVisibility(View.VISIBLE);
            postCommentButton.setVisibility(View.VISIBLE);
        }
        if (beer.getComments() != null) {
            displayOtherComments();
        }

        /* Set favorite toggle */
        ToggleButton favoriteToggle = (ToggleButton) findViewById(R.id.favoriteToggle);
        if (beer.isFavorited()) {
            favoriteToggle.setChecked(true);
        } else {
            favoriteToggle.setChecked(false);
        }
        favoriteToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                final boolean isCheckedFinal = isChecked;
                beer.setFavorited(isChecked);
                Intent updateIntent = new Intent(getApplicationContext(), UpdateFavorites.class);
                try {
                    updateIntent.putExtra("userID", user.getId());
                    updateIntent.putExtra("beerID", beer.getId());
                    updateIntent.putExtra("checkedFinal", isCheckedFinal);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                getApplicationContext().startService(updateIntent);
            }
        });
    }

    private void setWidgets(){
        postInitialCommentRow = (LinearLayout) findViewById(R.id.postInitialCommentRow);
        commentTextBox = (EditText) findViewById(R.id.commentTextBox);
        postCommentButton = (Button) findViewById(R.id.postCommentButton);

        /* Listener for postCommentButton */
        postCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                postComment();
            }
        });
    }

    private void hideSoftKeyboard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void setRatingImages() {
                /* Reference rating images and add to arrays */
        ratingImages = new ArrayList<ImageView>();
        ratingImages.add((ImageView) findViewById(R.id.ratingImg1));
        ratingImages.add((ImageView) findViewById(R.id.ratingImg2));
        ratingImages.add((ImageView) findViewById(R.id.ratingImg3));
        ratingImages.add((ImageView) findViewById(R.id.ratingImg4));
        ratingImages.add((ImageView) findViewById(R.id.ratingImg5));

        if (beer.getRating() >= 1) {
            ratingClicked(beer.getRating(), false);
        } else {
            ratingClicked(3, false);
        }
        ratingImages.get(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ratingClicked(1, true);
            }
        });
        ratingImages.get(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ratingClicked(2, true);
            }
        });
        ratingImages.get(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ratingClicked(3, true);
            }
        });
        ratingImages.get(3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ratingClicked(4, true);
            }
        });
        ratingImages.get(4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ratingClicked(5, true);
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("beer", beer);
        outState.putSerializable("user", user);
        outState.putString("userComment", commentTextBox.getText().toString());
    }

    /* Display comments by other users, i.e. other than the current user's comment*/
    private void displayOtherComments() {
        /* Get reference to row */
        LinearLayout othersCommentsRow = (LinearLayout) findViewById(R.id.othersCommentsRow);
        /* Define layout params */
        LinearLayout.LayoutParams fullWidthText = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        fullWidthText.setMargins(10, 5, 10, 0);
        LinearLayout.LayoutParams halfWidth = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f);
        halfWidth.setMargins(10, 5, 10, 0);
        LinearLayout.LayoutParams horizontalRuleParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
        horizontalRuleParam.setMargins(10, 5, 10, 0);
        /* Loop through display other users' comments array and user firstName and lastInitial */
        List<String> comments = beer.getComments();
        if (comments != null && comments.size() > 0 && !comments.get(0).equals("NULL")) {
            int i = 1;
            for (String c : comments) {
                /* Parse out the name*/
                String[] commentLines = c.split(System.getProperty("line.separator"));
                String pUserName = "";
                String pTime = "";
                if (commentLines.length > 0) {
                    pUserName = commentLines[0];
                }
                if (commentLines.length > 1) {
                    pTime = commentLines[1];
                }
                /* Reconstruct comment from lines array */
                StringBuilder sBuilder = new StringBuilder();
                for (int k = 2, len = commentLines.length; k < len; k++) {
                    if (k != len - 1) sBuilder.append(commentLines[k] + "\n");
                    else sBuilder.append(commentLines[k]);
                }
                String pComment = sBuilder.toString();

                /* Generate and display horizontal rule */
                if (i != 1) {
                    View horizontalRule = new View(this);
                    horizontalRule.setLayoutParams(horizontalRuleParam);
                    horizontalRule.setId(99 - i);
                    horizontalRule.setBackgroundColor(Color.parseColor("#B3B3B3"));
                    othersCommentsRow.addView(horizontalRule);
                }

                /* Generate views */
                TextView nameView = new TextView(this);
                nameView.setLayoutParams(halfWidth);

                nameView.setText(pUserName);
                nameView.setTypeface(null, Typeface.BOLD);
                nameView.setTextColor(Color.parseColor("#EB9100"));
                nameView.setTextSize(15);

                TextView timeView = new TextView(this);
                timeView.setLayoutParams(halfWidth);
                timeView.setGravity(Gravity.RIGHT);
                timeView.setText(pTime);
                timeView.setTextSize(10);

                TextView commentView = new TextView(this);
                commentView.setLayoutParams(fullWidthText);
                commentView.setText(pComment);
                commentView.setTypeface(null, Typeface.ITALIC);
                commentView.setTextSize(15);
                commentView.setTextColor(Color.parseColor("#000000"));

                /* Generate new horizontal layout for name and timeStamp and specify params */
                LinearLayout hLL = new LinearLayout(getApplicationContext());
                hLL.setOrientation(LinearLayout.HORIZONTAL);

                /* Add created views */
                hLL.addView(nameView);
                hLL.addView(timeView);
                othersCommentsRow.addView(hLL);
                othersCommentsRow.addView(commentView);
                i++;
            }
        }
    }


    private void displayMyComment() {
        /* Display user's comment at the top with edit option, hide the EditText and postCommentButton */
        myCommentRow = (LinearLayout) findViewById(R.id.myCommentRow);
        /* Specify layout parameters for vertical linear layout */
        LinearLayout.LayoutParams fullWidth = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        fullWidth.setMargins(10, 5, 10, 0);
        /* Create new horizontal layout for name and timeStamp and specify layout parameters */
        LinearLayout horizontalForNameTime = new LinearLayout(getApplicationContext());
        horizontalForNameTime.setOrientation(LinearLayout.HORIZONTAL);
        /* HorizontalLayout - set weightSum*/
        LinearLayout.LayoutParams halfWidth = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f);
        halfWidth.setMargins(10, 5, 10, 0);
        /* Split fullComment into name and comment http://stackoverflow.com/questions/3451903/extracting-substring-by-lines */
        String[] commentLines = beer.getMyComment().split(System.getProperty("line.separator"));
        String parsedComment = "";
        String parsedUserName = "";
        String parsedTime = "";
        if (commentLines.length > 0) {
            parsedUserName = commentLines[0];
        }
        if (commentLines.length > 1) {
            parsedTime = commentLines[1];
        }
        if (commentLines.length > 2) {
            /* Reconstruct comment from lines array by copying array from index 1 onwards into a commentString */
            StringBuilder aggregate = new StringBuilder();
            for (int k = 2, len = commentLines.length; k < len; k++) {
                if (k != len - 1) aggregate.append(commentLines[k] + "\n");
                else aggregate.append(commentLines[k]);
            }
            parsedComment = aggregate.toString();

        }
        /* User's name - set view parameters and display */
        myNameView = new TextView(this);
        myNameView.setLayoutParams(halfWidth);
        myNameView.setText(parsedUserName);
        myNameView.setTypeface(null, Typeface.BOLD);
        myNameView.setTextColor(Color.parseColor("#EB9100"));
        myNameView.setTextSize(15);
        /* Comment date and time - set view parameters and display */
        timeView = new TextView(this);
        timeView.setLayoutParams(halfWidth);
        timeView.setText(parsedTime);
        timeView.setGravity(Gravity.RIGHT);
        timeView.setTextSize(10);
        /* Comment text - set view parameters and display */
        editableCommentView = new TextView(this);
        editableCommentView.setLayoutParams(fullWidth);
        editableCommentView.setText(parsedComment);
        editableCommentView.setTypeface(null, Typeface.ITALIC);
        editableCommentView.setLayoutParams(fullWidth);
        editableCommentView.setTextSize(15);
        editableCommentView.setTextColor(Color.parseColor("#000000"));
        /* "Edit Comment" link - set view parameters and display */
        editCommentClickableView = new TextView(this);
        editCommentClickableView.setText("Edit Comment");
        editCommentClickableView.setTextColor(Color.parseColor("#006699"));
        editCommentClickableView.setLayoutParams(fullWidth);
        editCommentClickableView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postInitialCommentRow.setVisibility(View.VISIBLE);
                commentTextBox.setVisibility(View.VISIBLE);
                postCommentButton.setVisibility(View.VISIBLE);
                myNameView.setVisibility(View.GONE);
                timeView.setVisibility(View.GONE);
                editableCommentView.setVisibility(View.GONE);
                editCommentClickableView.setVisibility(View.GONE);
                if (beer.getMyComment() != null) {
                    String[] commentLines = beer.getMyComment().split(System.getProperty("line.separator"));
                    /* Reconstruct comment from lines array by copying array from index 1 into a commentString*/
                    StringBuilder aggregate = new StringBuilder();
                    for (int k = 2, len = commentLines.length; k < len; k++) {
                        if (k != len - 1) aggregate.append(commentLines[k] + "\n");
                        else aggregate.append(commentLines[k]);
                    }
                    String parsedComment = aggregate.toString();
                    commentTextBox.setText(parsedComment);
                }
            }
        });
        horizontalForNameTime.addView(myNameView);
        horizontalForNameTime.addView(timeView);
        myCommentRow.addView(horizontalForNameTime);
        myCommentRow.addView(editableCommentView);
        myCommentRow.addView(editCommentClickableView);
    }

    /* Save comment locally to object, and send to API */
    private void postComment() {
        userComment = commentTextBox.getText().toString();   //get text from EditText view
        if  (userComment != null &&
                (!TextUtils.equals(userComment, "null")) &&
                (!TextUtils.equals(userComment, "NULL")) &&
                (!TextUtils.isEmpty(userComment))) {
            String timeStamp = new SimpleDateFormat("MMM dd, yyyy hh:mm aaa").format(Calendar.getInstance().getTime());
            userComment = user.getFirstName() + "\n" + timeStamp + "\n" + userComment;
            beer.setMyComment(userComment);
            callReviewService(); //encodes comment and updates API
            commentTextBox.setVisibility(View.GONE); //gone means removed from layout and won't occupy space
            postCommentButton.setVisibility(View.GONE); //invisible means removed form layout but still occupies space
            postInitialCommentRow.setVisibility(View.GONE);
            myCommentRow = (LinearLayout) findViewById(R.id.myCommentRow);
            myCommentRow.setVisibility(View.VISIBLE);
            if (((LinearLayout) myCommentRow).getChildCount() > 0) {
                ((LinearLayout) myCommentRow).removeAllViews();
            }
            displayMyComment();
        } else {
            commentTextBox.setError("Please enter a comment.");
            return;
        }

    }

    /* After a rating image is clicked, set others to clickable */
    private void setClickable(int img1, int img2, int img3, int img4) {
        ratingImages.get(img1).setClickable(true);
        ratingImages.get(img2).setClickable(true);
        ratingImages.get(img3).setClickable(true);
        ratingImages.get(img4).setClickable(true);
    }

    private void setPicassoImage(Context ctx, int res, ImageView iv){
        Picasso.with(ctx)
                .load(res)
                .fit()
                .centerInside()
                .into(iv);
    }

    /* Darkens other images on rating image click */
    private void ratingClicked(double rating, boolean makeCall) {
        Context ctx = getApplicationContext();
        int intRating = (int) rating;
        beer.setRating(rating);
        ratingImages.get(intRating - 1).setClickable(false);
        switch (intRating) {
            case 1: {
                setClickable(1, 2, 3, 4);
                //ratingImages.get(0).setImageResource(R.drawable.rate1);
                setPicassoImage(ctx, R.drawable.rate1, ratingImages.get(0));
                //ratingImages.get(1).setImageResource(R.drawable.rate2dark);
                setPicassoImage(ctx, R.drawable.rate2dark, ratingImages.get(1));
                //ratingImages.get(2).setImageResource(R.drawable.rate3dark);
                setPicassoImage(ctx, R.drawable.rate3dark, ratingImages.get(2));
                //ratingImages.get(3).setImageResource(R.drawable.rate4dark);
                setPicassoImage(ctx,R.drawable.rate4dark,ratingImages.get(3));
                //ratingImages.get(4).setImageResource(R.drawable.rate5dark);
                setPicassoImage(ctx,R.drawable.rate5dark,ratingImages.get(4));

                break;
            }
            case 2: {
                setClickable(0, 2, 3, 4);
                //ratingImages.get(1).setImageResource(R.drawable.rate2);
                setPicassoImage(ctx, R.drawable.rate2, ratingImages.get(1));
                //ratingImages.get(0).setImageResource(R.drawable.rate1dark);
                setPicassoImage(ctx, R.drawable.rate1dark, ratingImages.get(0));
                //ratingImages.get(2).setImageResource(R.drawable.rate3dark);
                setPicassoImage(ctx, R.drawable.rate3dark, ratingImages.get(2));
                //ratingImages.get(3).setImageResource(R.drawable.rate4dark);
                setPicassoImage(ctx, R.drawable.rate4dark, ratingImages.get(3));
                //ratingImages.get(4).setImageResource(R.drawable.rate5dark);
                setPicassoImage(ctx, R.drawable.rate5dark, ratingImages.get(4));

                break;
            }
            case 3: {
                setClickable(0, 1, 3, 4);
                //ratingImages.get(2).setImageResource(R.drawable.rate3);
                setPicassoImage(ctx, R.drawable.rate3, ratingImages.get(2));
                //ratingImages.get(0).setImageResource(R.drawable.rate1dark);
                setPicassoImage(ctx, R.drawable.rate1dark, ratingImages.get(0));
                //ratingImages.get(1).setImageResource(R.drawable.rate2dark);
                setPicassoImage(ctx, R.drawable.rate2dark, ratingImages.get(1));
                //ratingImages.get(3).setImageResource(R.drawable.rate4dark);
                setPicassoImage(ctx, R.drawable.rate4dark, ratingImages.get(3));
                //ratingImages.get(4).setImageResource(R.drawable.rate5dark);
                setPicassoImage(ctx, R.drawable.rate5dark, ratingImages.get(4));
                break;
            }
            case 4: {
                setClickable(0, 1, 2, 4);
                //ratingImages.get(3).setImageResource(R.drawable.rate4);
                setPicassoImage(ctx, R.drawable.rate4, ratingImages.get(3));
                //ratingImages.get(0).setImageResource(R.drawable.rate1dark);
                setPicassoImage(ctx, R.drawable.rate1dark, ratingImages.get(0));
                //ratingImages.get(1).setImageResource(R.drawable.rate2dark);
                setPicassoImage(ctx, R.drawable.rate2dark, ratingImages.get(1));
                //ratingImages.get(2).setImageResource(R.drawable.rate3dark);
                setPicassoImage(ctx, R.drawable.rate3dark, ratingImages.get(2));
                //ratingImages.get(4).setImageResource(R.drawable.rate5dark);
                setPicassoImage(ctx, R.drawable.rate5dark, ratingImages.get(4));
                break;
            }
            case 5: {
                setClickable(0, 1, 2, 3);
                //ratingImages.get(4).setImageResource(R.drawable.rate5);
                setPicassoImage(ctx, R.drawable.rate5, ratingImages.get(4));
                //ratingImages.get(0).setImageResource(R.drawable.rate1dark);
                setPicassoImage(ctx, R.drawable.rate1dark, ratingImages.get(0));
                //ratingImages.get(1).setImageResource(R.drawable.rate2dark);
                setPicassoImage(ctx, R.drawable.rate2dark, ratingImages.get(1));
                //ratingImages.get(2).setImageResource(R.drawable.rate3dark);
                setPicassoImage(ctx, R.drawable.rate3dark, ratingImages.get(2));
                //ratingImages.get(3).setImageResource(R.drawable.rate4dark);
                setPicassoImage(ctx, R.drawable.rate4dark, ratingImages.get(3));
                break;
            }
            default: { //rate null, no action
            }
        }
        if (makeCall) {
            callReviewService();
        }
    }




    /* Sends rating and non-empty comment for API call */
    private void callReviewService() {
        final int beerId = beer.getId();
        final double rating = beer.getRating();
        final String myComment = beer.getMyComment();
        RequestQueue queue = Volley.newRequestQueue(BeerProfile.this);
        String url = null;
        try {
            url = baseUrl + user.getId() + "/" + beerId + "/" + rating + "/" + URLEncoder.encode(myComment, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("BeerProfile", response);
                    for (Beer beer : DefaultEventAllBeers.beers) {
                        if (beer.getId() == beerId) {
                            beer.setRating(rating);
                            beer.setMyComment(myComment);
                        }
                    }
                    for (Beer beer : DefaultEventAllBeers.favoriteBeers) {
                        if (beer.getId() == beerId) {
                            beer.setRating(rating);
                            beer.setMyComment(myComment);
                        }
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

    /* Set view fields in beer profile */
    private void populateBeer() {
        breweryLogo = (ImageView) findViewById(R.id.breweryLogo);
        Picasso.with(getApplicationContext())
                .load(beer.getBreweryLogoURL())
                .fit()
                .centerInside()
                .into(breweryLogo);

        ((TextView) findViewById(R.id.beerName)).setText(beer.getName());
        if (beer.getType() != null && !beer.getType().isEmpty() && !beer.getType().equals("")) {
            ((TextView) findViewById(R.id.beerType)).setText(beer.getType());
        }
        ((TextView) findViewById(R.id.breweryName)).setText(beer.getBrewery());
        if (beer.getAbv() != null && beer.getIbu() != null) {
            ((TextView) findViewById(R.id.beerABVIBU)).setText("ABV " + beer.getAbv() + ", IBU " + beer.getIbu());
        }
        ((TextView) findViewById(R.id.beerDescription)).setText(beer.getDescription());

        if (beer.getAverageRating() == 0.0) {
            ((TextView) findViewById(R.id.averageRatingText)).setText("Average rating ");
        } else {
            ((TextView) findViewById(R.id.averageRatingText)).setText("Average rating ");
            ((TextView) findViewById(R.id.averageRatingNumber)).setText(String.format("%.1f", beer.getAverageRating()));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_beer_profile, menu);
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
}