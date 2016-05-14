package com.iamhoppy.hoppy;

import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;

import com.facebook.login.widget.LoginButton;

public class HoppyInstrumentedTest extends ActivityInstrumentationTestCase2<MainActivity> {
    MainActivity ma;

    public HoppyInstrumentedTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ma = getActivity();
    }

    @UiThreadTest
    public void testClickLogin() {
        final LoginButton loginButton = (LoginButton) ma.findViewById(R.id.login_button);
        loginButton.performClick();

    }

}
