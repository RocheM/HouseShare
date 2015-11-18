package com.example.matthew.facebooklogintest;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.microsoft.windowsazure.mobileservices.*;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceAuthenticationProvider;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.TableOperationCallback;


import java.net.MalformedURLException;


public class MainActivity extends AppCompatActivity {


    private MobileServiceClient mClient;
    private MobileServiceUser mUser;
    private Button FBLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        FBLogin = (Button) findViewById(R.id.loginFB);
        FBLogin.setOnClickListener(new View.OnClickListener() {

                                       @Override
                                       public void onClick(View view){

                                           authenticate();

                                       }
                                   }

        );

        try {
            mClient = new MobileServiceClient(
                    "https://testroche2.azure-mobile.net/",
                    "rRLdRoMQNahVIZcwWlvKpfxVwnpufK79",
                    this
            );
        }catch (MalformedURLException e){

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void authenticate() {
        mClient.login(MobileServiceAuthenticationProvider.Facebook, new UserAuthenticationCallback() {
            @Override
            public void onCompleted(MobileServiceUser mobileServiceUser, Exception e, ServiceFilterResponse serviceFilterResponse) {
                if (e==null)
                {

                    mUser = mobileServiceUser;
                    String userID = mobileServiceUser.getUserId();
                    final Intent i = new Intent(getApplicationContext(),LoggedIn.class);
                    i.putExtra("ID", userID);  // pass your values and retrieve them in the other Activity using keyName

                    User newUser = new User();
                    newUser.FacebookID = userID;
                    mClient.getTable(User.class).insert(newUser, new TableOperationCallback<User>() {
                        public void onCompleted(User entity, Exception exception, ServiceFilterResponse response) {
                            if (exception == null) {
                                startActivity(i);

                            } else {
                                FBLogin.setText(exception.getMessage());
                            }
                        }
                    });


                }
                else
                {
                    FBLogin.setText(e.getMessage());
                }
            }
        });
    }

}
