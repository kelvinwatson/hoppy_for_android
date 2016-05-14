package com.iamhoppy.hoppy;

import android.content.pm.ActivityInfo;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.test.suitebuilder.annotation.LargeTest;
import android.widget.Button;
import android.widget.EditText;

import junit.framework.AssertionFailedError;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.instanceOf;

/*
 * Remove margins on buttons before running this test
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class HoppyEspressoTest {
    private final long TIME_OUT = 30000;
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule(MainActivity.class);
    private String HOPPY_APP = "Hoppy";
    private UiDevice mDevice;

    @Before
    public void setUp() throws Exception {
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        login();
    }

    //requires that buttons be 90% in view
    public void checkButtons() {
        try {
            onView(withId(R.id.favoriteBeersButton)).perform(click());
            onView(withId(R.id.allBeersButton)).perform(click());
        } catch (NoMatchingViewException | AssertionFailedError e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCaseLoginAndViewBeers() {
        viewFirstBeerProfile();
    }

    @Test
    public void testCaseSelectEvent(){
        selectNextEvent();
    }

    @Test
    public void testCaseChangeOrientation(){
        changeOrientationToLandscape();
        changeOrientationToPortrait();
    }

    private void changeOrientationToLandscape(){
        mActivityRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    private void changeOrientationToPortrait(){
        mActivityRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void selectNextEvent(){
        onView(withId(R.id.eventSpinner)).perform(click()); //click spinner
        onData(allOf(instanceOf(Event.class)))
            .atPosition(1)
            .perform(click());
    }

    public void viewFirstBeerProfile() {
        try {
            onView(withId(R.id.beerList)).check(matches(isDisplayed()));
        } catch (NoMatchingViewException e) {
            e.printStackTrace();
        }
        onData(anything())
                .inAdapterView(withId(R.id.beerList))
                .atPosition(0)
                .perform(click());
        pressBack();
//        onData(anything())
//                .inAdapterView(withId(R.id.beerList))
//                .atPosition(10)
//                .perform(click());
//        pressBack();
    }

    public void login() {
        try {
            onView(withId(R.id.progressBar)).check(doesNotExist());
            //user has already logged in previously
        } catch (NoMatchingViewException | AssertionError e) {
            //perform login procedure
            try {
                loginWithFacebook();
            } catch (UiObjectNotFoundException e2) {
                e2.printStackTrace();
            }
        }
    }

    public void loginWithFacebook() throws UiObjectNotFoundException {
        UiObject facebookLoginBtn = mDevice.findObject(new UiSelector().text("Log in with Facebook"));
        facebookLoginBtn.clickAndWaitForNewWindow();
        //waitForWindowUpdate(null, TIME_OUT); //bypass progress spinner
        UiObject loginWebView = mDevice.findObject(new UiSelector()
                .instance(0)
                .className("android.webkit.WebView"));
        loginWebView.waitForExists(TIME_OUT);

        UiObject loginBtn = mDevice.findObject(new UiSelector()
                .instance(0)
                .className(Button.class));
        loginBtn.waitForExists(TIME_OUT);

        if (loginWebView.exists() || loginBtn.exists()) {
            UiObject userName = mDevice.findObject(new UiSelector()
                    .instance(0)
                    .className(EditText.class));
            userName.setText("open_gtuldca_user@tfbnw.net");

            UiObject password = mDevice.findObject(new UiSelector()
                    .instance(1)
                    .className(EditText.class));
            password.setText("3']k%c/4/K,%!NfF");

            loginBtn.clickAndWaitForNewWindow();
            //mDevice.waitForWindowUpdate(null, TIME_OUT); //bypass progress spinner
            UiObject confirmWebView = mDevice.findObject(new UiSelector()
                    .instance(0)
                    .className("android.webkit.WebView"));
            confirmWebView.waitForExists(TIME_OUT);

            UiObject okBtn = mDevice.findObject(new UiSelector()
                    .instance(1)
                    .className(Button.class));
            okBtn.waitForExists(TIME_OUT);

            if (confirmWebView.exists()) { //Confirm screen
                okBtn.clickAndWaitForNewWindow();
            }
        }
    }

    //looks for app shortcut in home screen
    public void findAndOpenApp() throws UiObjectNotFoundException {
        mDevice.pressHome();
        UiObject hoppyApp = mDevice.findObject(new UiSelector().text(HOPPY_APP));
        hoppyApp.clickAndWaitForNewWindow();
    }



}