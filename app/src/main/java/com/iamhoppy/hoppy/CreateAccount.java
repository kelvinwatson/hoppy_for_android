package com.iamhoppy.hoppy;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;

public class CreateAccount extends AccountAuthenticatorActivity {
    private Account account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        Button createAccountButton = (Button) findViewById(R.id.createAccountButton);
        final EditText firstNameEntry = (EditText) findViewById(R.id.firstNameEntry);
        final EditText lastNameEntry = (EditText) findViewById(R.id.lastNameEntry);
        final Activity activity = this;
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = firstNameEntry.getText().toString();
                String lastName = lastNameEntry.getText().toString();
                if (firstName != null && firstName.length() > 0 && lastName != null && lastName.length() > 0) {
                    account = new Account(firstName + " " + lastName, "com.iamhoppy.hoppy.login");
                    AccountManager am = AccountManager.get(getApplicationContext());
                    Bundle acctBundle = new Bundle();
                    am.addAccount("com.iamhoppy.hoppy.login", "com.iamhoppy.hoppy.login", null, acctBundle, activity, new OnAccountAddComplete(), null);
                } else {
                    //TODO: Put a toast or error
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_account, menu);
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

    private class OnAccountAddComplete implements AccountManagerCallback<Bundle> {
        @Override
        public void run(AccountManagerFuture<Bundle> result) {
            Bundle bundle;
            try {
                bundle = result.getResult();
            } catch (OperationCanceledException e) {
                e.printStackTrace();
                return;
            } catch (AuthenticatorException e) {
                e.printStackTrace();
                return;
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            account = new Account(
                    bundle.getString(AccountManager.KEY_ACCOUNT_NAME),
                    bundle.getString(AccountManager.KEY_ACCOUNT_TYPE)
            );
            Log.d("main", "Added account " + account.name + ", fetching");
            //startAuthTokenFetch();
        }
    }
}
