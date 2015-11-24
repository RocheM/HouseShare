package itt.matthew.houseshare;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.windowsazure.mobileservices.*;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceAuthenticationProvider;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.TableOperationCallback;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.Arrays;

public class FBLogin extends AppCompatActivity {

    private MobileServiceClient mClient;
    private LoginButton FBLoginButton;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_fblogin);


        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {

                AccessToken.setCurrentAccessToken(currentAccessToken);
            }
        };

        FBLoginButton = (LoginButton) findViewById(R.id.login_button);

            FBLoginButton.setReadPermissions(Arrays.asList("public_profile", "user_location", "user_birthday", "user_likes", "user_friends","user_about_me"));


            FBLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {

                    AccessToken.setCurrentAccessToken(loginResult.getAccessToken());

                }

                @Override
                public void onCancel() {
                    Log.d("Facebook login", "The user did not give permission");

                }

                @Override
                public void onError(FacebookException exception) {

                    exception.printStackTrace();

                }
            });

            try {
                mClient = new MobileServiceClient(
                        "https://houseshareproject.azure-mobile.net/",
                        "iuqOtKPRNqrMfasRrLARUYNrihSzwh94",
                        this
                );

            } catch (Exception e) {


            }

        }

    public void upload(String Message) {
        Account item = new Account();
        item.Text = Message;
        mClient.getTable(Account.class).insert(item, new TableOperationCallback<Account>() {
            public void onCompleted(Account entity, Exception exception, ServiceFilterResponse response) {
                if (exception == null) {

                } else {


                }
            }
        });

    }

    private void authenticate() {

        ListenableFuture<MobileServiceUser> mLogin = mClient.login(MobileServiceAuthenticationProvider.Facebook);

        Futures.addCallback(mLogin, new FutureCallback<MobileServiceUser>() {
            @Override
            public void onFailure(Throwable exc) {

                upload(exc.getMessage());
            }

            @Override
            public void onSuccess(MobileServiceUser user) {

                upload("YUSS");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);


        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);


    }

    public void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }

}
