package itt.matthew.houseshare.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.common.util.concurrent.SettableFuture;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceAuthenticationProvider;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;

import com.afollestad.materialdialogs.MaterialDialog;


import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.windowsazure.mobileservices.*;
import com.microsoft.windowsazure.mobileservices.http.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.TableOperationCallback;
import com.microsoft.windowsazure.mobileservices.table.TableQueryCallback;
import com.microsoft.windowsazure.notifications.NotificationsManager;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;


import itt.matthew.houseshare.Activities.JoinHouse;
import itt.matthew.houseshare.Activities.MainActivity;
import itt.matthew.houseshare.Events.MyHandler;
import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.R;

public class FBLoginFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public static final String SHAREDPREFFILE = "temp";
    public static final String USERIDPREF = "uid";
    public static final String TOKENPREF = "tkn";
    public static final String FBTOKENPREF = "fbt";

    private OnFragmentInteractionListener mListener;
    public static MobileServiceClient mClient;
    private Account newAccount;
    private MobileServiceTable<Account> mAccountTable;
    private JSONObject JSONresponse;
    private ProgressDialog progress;
    private MaterialDialog materialDialog;
    private Button loginButton;
    private OkHttpClient client = new OkHttpClient();
    private String userToken;
    public boolean bAuthenticating = false;
    public final Object mAuthenticationLock = new Object();
    private ProgressDialog progressDialog;

    public FBLoginFragment() {
    }

    public static FBLoginFragment newInstance(String param1, String param2) {

        FBLoginFragment fragment = new FBLoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupAzure();


        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);


        }
    }



    private void setupAzure() {

        try {
            mClient = new MobileServiceClient(
                    "https://backendhs.azurewebsites.net",
                    this.getActivity())
            .withFilter(new ProgressFilter())
            .withFilter(new RefreshTokenCacheFilter());
        } catch (Exception e) {

            new MaterialDialog.Builder(this.getContext())
                    .title("Error")
                    .content(e.getMessage())
                    .positiveText("Ok")
                    .show();


        }
        mAccountTable = mClient.getTable(Account.class);


    }

    private void setupUI(){

        loginButton = (Button) getView().findViewById(R.id.FB_Login);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Logging in");
        progressDialog.setMessage("Please Wait");
        progressDialog.hide();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticate(true);
            }
        });

    }

    public void onResume() {
        super.onResume();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_fblogin, container, false);
    }

    public void startJoinHouseActivity(Account acc){
        Intent i = new Intent(getActivity(), JoinHouse.class);
        Bundle b = new Bundle();
        b.putParcelable("Account", acc);
        i.putExtra("Bundle", b);

        startActivity(i);
        getActivity().finish();
    }

    public void startMainActivity(Account acc) {

        Intent i = new Intent(getActivity(), MainActivity.class);
        Bundle b = new Bundle();
        b.putParcelable("Account", acc);
        i.putExtra("Bundle", b);
        startActivity(i);
        getActivity().finish();

    }

    private void cacheUserToken(MobileServiceUser user)
    {
        SharedPreferences prefs = getActivity().getSharedPreferences(SHAREDPREFFILE, Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putString(USERIDPREF, user.getUserId());
        editor.putString(TOKENPREF, user.getAuthenticationToken());
        editor.commit();
    }

    private boolean loadUserTokenCache(MobileServiceClient client)
    {
        SharedPreferences prefs = getActivity().getSharedPreferences(SHAREDPREFFILE, Context.MODE_PRIVATE);
        String userId = prefs.getString(USERIDPREF, "undefined");
        if (userId == "undefined")
            return false;
        String token = prefs.getString(TOKENPREF, "undefined");
        if (token == "undefined")
            return false;

        MobileServiceUser user = new MobileServiceUser(userId);
        user.setAuthenticationToken(token);
        client.setCurrentUser(user);

        return true;
    }

    private void authenticate(boolean bRefreshCache) {

        bAuthenticating = true;


        if (bRefreshCache || !loadUserTokenCache(mClient))
        {
            // New login using the provider and update the token cache.
            mClient.login(MobileServiceAuthenticationProvider.Facebook,
                    new UserAuthenticationCallback() {
                        @Override
                        public void onCompleted(MobileServiceUser user,
                                                Exception exception, ServiceFilterResponse response) {

                            synchronized(mAuthenticationLock)
                            {
                                if (exception == null) {
                                    cacheUserToken(mClient.getCurrentUser());
                                    getDetails();
                                } else {
                                    createAndShowDialog(exception.getMessage(), "Login Error");
                                }
                                bAuthenticating = false;
                                mAuthenticationLock.notifyAll();
                            }
                        }
                    });
        }
        else
        {
            // Other threads may be blocked waiting to be notified when
            // authentication is complete.
            synchronized(mAuthenticationLock)
            {
                bAuthenticating = false;
                mAuthenticationLock.notifyAll();
            }
        }
    }
    public boolean detectAndWaitForAuthentication()
    {
        boolean detected = false;
        synchronized(mAuthenticationLock)
        {
            do
            {
                if (bAuthenticating == true)
                    detected = true;
                try
                {
                    mAuthenticationLock.wait(1000);
                }
                catch(InterruptedException e)
                {}
            }
            while(bAuthenticating == true);
        }
        if (bAuthenticating == true)
            return true;

        return detected;
    }

    private void waitAndUpdateRequestToken(ServiceFilterRequest request)
    {
        MobileServiceUser user = null;
        if (detectAndWaitForAuthentication())
        {
            user = mClient.getCurrentUser();
            if (user != null)
            {
                request.removeHeader("X-ZUMO-AUTH");
                request.addHeader("X-ZUMO-AUTH", user.getAuthenticationToken());
            }
        }
    }


    private void getDetails()  {


        new AsyncTask<String, Integer, String>(){

            @Override
            protected String doInBackground(String... params) {

                try {

                    Request request = new Request.Builder()
                            .addHeader("X-ZUMO-AUTH", mClient.getCurrentUser().getAuthenticationToken())
                            .url("https://backendhs.azurewebsites.net/.auth/me")
                            .build();


                    Response response = client.newCall(request).execute();

                    return response.body().string();

                } catch (Exception ex){
                    ex.printStackTrace();
                    Log.d("Get Details", "Get Details");
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                try {

                    JSONArray Json = new JSONArray(result);

                    SharedPreferences prefs = getActivity().getSharedPreferences(SHAREDPREFFILE, Context.MODE_PRIVATE);
                    Editor editor = prefs.edit();
                    editor.putString(FBTOKENPREF, Json.getJSONObject(0).getString("access_token"));
                    editor.commit();

                    populateProfile();

                } catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }.execute();


    }

    private void populateProfile(){

        SharedPreferences prefs = getActivity().getSharedPreferences(SHAREDPREFFILE, Context.MODE_PRIVATE);
        final String accessToken = prefs.getString(FBTOKENPREF, "undefined");
        if (accessToken == "undefined"){
            return;
        }


        new AsyncTask<String, Integer, String>(){

            @Override
            protected String doInBackground(String... params) {

                try {

                    String FBurl = "https://graph.facebook.com/v2.5/me?fields=name%2Ccover%2Cemail&access_token=" + accessToken;

                    Request request = new Request.Builder()
                            .url(FBurl)
                            .build();


                    Response response = client.newCall(request).execute();

                    return response.body().string();

                } catch (Exception ex){
                    ex.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                try {

                    JSONresponse = new JSONObject(result);

                    String name, birthday, about, email, id, cover;

                    try {
                        name = JSONresponse.getString("name");
                    } catch (Exception ex){
                        name = "null";
                    }
                    try {
                        id = JSONresponse.getString("id");
                    } catch (Exception ex){
                        id = "null";
                    }
                    try {

                        email = JSONresponse.getString("email");
                    } catch (Exception ex){
                        email = "null";
                    }
                    try {
                        cover = JSONresponse.getJSONObject("cover").getString("source");
                    } catch (Exception ex){
                        cover = "null";
                    }

                        newAccount = new Account(id, name, email, cover);


                    checkIfExists();


                } catch (Exception ex){
                    ex.printStackTrace();
                    Log.d("Populate Profile", "Populate Profile");
                }
            }
        }.execute();


    }


    public void checkIfExists(){


            mAccountTable.where().field("facebookID").eq(newAccount.getFacebookID()).execute(new TableQueryCallback<Account>() {
                @Override
                public void onCompleted(List<Account> result, int count, Exception exception, ServiceFilterResponse response) {


                    if (exception == null) {
                        if (count == 0) {
                            upload(newAccount);
                        } else {
                            newAccount = result.get(0);

                            if (newAccount.getHouseID() == -1) {
                                 startJoinHouseActivity(newAccount);
                            } else {
                                startMainActivity(newAccount);
                            }
                        }
                    } else {
                        checkIfExists();
                    }
                }
            });
        }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        setupUI();



    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onFragmentInteraction();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void createAndShowDialog(String title, String message){

        MaterialDialog builder = new MaterialDialog.Builder(this.getContext())
                .title(title)
                .content(message)
                .positiveText("Ok")
                .show();
    }

    private void createAndShowProgress(String title, String content){

        progress = new ProgressDialog(this.getContext());
        progress.setTitle(title);
        progress.setMessage(content);
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.show();

    }


    public void upload(Account item) {

        mClient.getTable(Account.class).insert(item, new TableOperationCallback<Account>() {
            public void onCompleted(Account entity, Exception exception, ServiceFilterResponse response) {
                if (exception == null) {
                    startJoinHouseActivity(newAccount);

                } else {
                    exception.printStackTrace();
                }
            }
        });

    }


/**
 * This interface must be implemented by activities that contain this
 * fragment to allow an interaction in this fragment to be communicated
 * to the activity and potentially other fragments contained in that
 * activity.
 * <p/>
 * See the Android Training lesson <a href=
 * "http://developer.android.com/training/basics/fragments/communicating.html"
 * >Communicating with Other Fragments</a> for more information.
 */
public interface OnFragmentInteractionListener {
    // TODO: Update argument type and name
    void onFragmentInteraction();
}

    private class ProgressFilter implements ServiceFilter {
        @Override
        public ListenableFuture<ServiceFilterResponse> handleRequest(ServiceFilterRequest request, NextServiceFilterCallback nextServiceFilterCallback) {

            final SettableFuture<ServiceFilterResponse> resultFuture = SettableFuture.create();

            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (progressDialog != null) progressDialog.show();
                }
            });

            ListenableFuture<ServiceFilterResponse> future = nextServiceFilterCallback.onNext(request);

            Futures.addCallback(future, new FutureCallback<ServiceFilterResponse>() {
                @Override
                public void onFailure(Throwable e) {
                    resultFuture.setException(e);
                }

                @Override
                public void onSuccess(ServiceFilterResponse response) {
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (progressDialog != null) progressDialog.hide();
                        }
                    });

                    resultFuture.set(response);
                }
            });

            return resultFuture;
        }
    }

    private class RefreshTokenCacheFilter implements ServiceFilter {

        AtomicBoolean mAtomicAuthenticatingFlag = new AtomicBoolean();

        @Override
        public ListenableFuture<ServiceFilterResponse> handleRequest(
                final ServiceFilterRequest request,
                final NextServiceFilterCallback nextServiceFilterCallback
        )
        {
            // In this example, if authentication is already in progress we block the request
            // until authentication is complete to avoid unnecessary authentications as
            // a result of HTTP status code 401.
            // If authentication was detected, add the token to the request.
            waitAndUpdateRequestToken(request);

            // Send the request down the filter chain
            // retrying up to 5 times on 401 response codes.
            ListenableFuture<ServiceFilterResponse> future = null;
            ServiceFilterResponse response = null;
            int responseCode = 401;
            for (int i = 0; (i < 5 ) && (responseCode == 401); i++)
            {
                future = nextServiceFilterCallback.onNext(request);
                try {
                    response = future.get();
                    responseCode = response.getStatus().code;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    if (e.getCause().getClass() == MobileServiceException.class)
                    {
                        MobileServiceException mEx = (MobileServiceException) e.getCause();
                        responseCode = mEx.getResponse().getStatus().code;
                        if (responseCode == 401)
                        {
                            // Two simultaneous requests from independent threads could get HTTP status 401.
                            // Protecting against that right here so multiple authentication requests are
                            // not setup to run on the UI thread.
                            // We only want to authenticate once. Requests should just wait and retry
                            // with the new token.
                            if (mAtomicAuthenticatingFlag.compareAndSet(false, true))
                            {
                                // Authenticate on UI thread
                               getActivity().runOnUiThread(new Runnable() {
                                   @Override
                                   public void run() {
                                       // Force a token refresh during authentication.
                                       authenticate(true);
                                   }
                               });
                            }

                            // Wait for authentication to complete then update the token in the request.
                            waitAndUpdateRequestToken(request);
                            mAtomicAuthenticatingFlag.set(false);
                        }
                    }
                }
            }
            return future;
        }
    }
}
