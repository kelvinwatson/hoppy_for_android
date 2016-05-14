package com.iamhoppy.hoppy;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class HoppyAuthenticatorService extends Service {
    public HoppyAuthenticatorService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        HoppyAuthenticator authenticator = new HoppyAuthenticator(this);
        return authenticator.getIBinder();
    }
}
