package itt.matthew.houseshare;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.TableOperationCallback;
import com.microsoft.windowsazure.mobileservices.table.TableQueryCallback;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class FBLogin extends AppCompatActivity {

    private MobileServiceClient mClient;
    private LoginButton FBLoginButton;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private JSONObject JSONresponse;
    private Account newAccount;
    private MobileServiceTable<Account> mAccountTable;
    private String FBProfileID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_fblogin);

        FBLoginButton = (LoginButton) findViewById(R.id.login_button);

            FBLoginButton.setReadPermissions(Arrays.asList("public_profile", "user_location", "user_birthday", "user_likes", "user_friends","user_about_me" ));


            FBLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {

                    FBProfileID = loginResult.getAccessToken().getUserId();
                    Profile.fetchProfileForCurrentAccessToken();
                    populateProfile();

                }

                @Override
                public void onCancel() {
                    Log.d("Facebook login", "The user did not give permission");


                }

                @Override
                public void onError(FacebookException exception) {

                    Log.d("Facebook login", "The user did not give permission");
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
        mAccountTable = mClient.getTable(Account.class);
        }




    public void populateProfile() {

        if (Profile.getCurrentProfile() != null) {

            mAccountTable.where()
                    .field("facebookID")
                    .eq(Profile.getCurrentProfile().getId())
                    .execute(new TableQueryCallback<Account>() {
                        @Override
                        public void onCompleted(List<Account> result, int count, Exception exception, ServiceFilterResponse response) {
                            if (exception == null) {
                                if (count == 0) {

                                    GraphRequest request = GraphRequest.newMeRequest(
                                            AccessToken.getCurrentAccessToken(),
                                            new GraphRequest.GraphJSONObjectCallback() {
                                                @Override
                                                public void onCompleted(
                                                        JSONObject object,
                                                        GraphResponse response) {
                                                    JSONresponse = object;

                                                    try {

                                                        String name;
                                                        String birthday;
                                                        String location;
                                                        String about;


                                                        name = JSONresponse.getString("name");
                                                        birthday = JSONresponse.getString("birthday");
                                                        about = JSONresponse.getString("bio");
                                                        location = JSONresponse.getJSONObject("location").getString("name");
                                                        newAccount = new Account(Profile.getCurrentProfile().getId(), name, birthday, location, about);

                                                        upload(newAccount);

                                                    } catch (JSONException ex) {
                                                        ex.printStackTrace();
                                                    } catch (NullPointerException ex) {
                                                        ex.printStackTrace();
                                                    }
                                                }

                                            });

                                    Bundle parameters = new Bundle();
                                    parameters.putString("fields", "id,name,bio,birthday,location");
                                    request.setParameters(parameters);
                                    request.executeAsync();

                                }
                            } else
                                exception.printStackTrace();

                        }
                    });
        }
        else{
            Log.d("Facebook", "NULL PROFILE");
        }
    }


    public void upload(Account item) {
        mClient.getTable(Account.class).insert(item, new TableOperationCallback<Account>() {
            public void onCompleted(Account entity, Exception exception, ServiceFilterResponse response) {
                if (exception == null) {

                } else {

                    exception.printStackTrace();

                }
            }
        });

    }

//    private void authenticate() {
//
//        ListenableFuture<MobileServiceUser> mLogin = mClient.login(MobileServiceAuthenticationProvider.Facebook);
//
//        Futures.addCallback(mLogin, new FutureCallback<MobileServiceUser>() {
//            @Override
//            public void onFailure(Throwable exc) {
//
//                upload(exc.getMessage());
//            }
//
//            @Override
//            public void onSuccess(MobileServiceUser user) {
//
//                upload("YUSS");
//            }
//        });
//    }

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
