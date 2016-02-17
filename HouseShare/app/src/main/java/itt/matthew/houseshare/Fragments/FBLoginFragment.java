package itt.matthew.houseshare.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
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

import org.json.JSONObject;

import java.security.AuthProvider;
import java.util.Arrays;
import java.util.List;

import itt.matthew.houseshare.Activities.JoinHouse;
import itt.matthew.houseshare.Activities.MainActivity;
import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FBLoginFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FBLoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FBLoginFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private CallbackManager mCallbackManager;
    private TextView details;
    private AccessTokenTracker mTokenTracker;
    private ProfileTracker mProfileTracker;
    private MobileServiceClient mClient;
    private Account newAccount;
    private MobileServiceTable<Account> mAccountTable;
    private JSONObject JSONresponse;
    private ProgressDialog progress;
    private MaterialDialog materialDialog;

    private FacebookCallback<LoginResult> mCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            Profile profile = Profile.getCurrentProfile();


            details.setText(constructWelcomeMessage(profile));
            checkIfExists();



        }

        @Override
        public void onCancel() {

            details.setText("Login Cancelled");

        }

        @Override
        public void onError(FacebookException error) {

            details.setText(error.getMessage());

        }
    };

    public FBLoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FBLoginFragment.
     */
    // TODO: Rename and change types and number of parameters
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

        FacebookSdk.sdkInitialize(this.getContext());
        mCallbackManager = CallbackManager.Factory.create();
        setupTokenTracker();
        setupProfileTracker();
        setupAzure();


        mTokenTracker.startTracking();
        mProfileTracker.startTracking();



        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);


        }
    }

    private void setupAzure() {

        try {
            mClient = new MobileServiceClient(
                    "https://backendhs.azurewebsites.net",
                    this.getActivity());
        } catch (Exception e) {

            new MaterialDialog.Builder(this.getContext())
                    .title("Error")
                    .content(e.getMessage())
                    .positiveText("Ok")
                    .show();


        }
        mAccountTable = mClient.getTable(Account.class);
        

    }

    private void setupTokenTracker() {
        mTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                Log.d("HouseShare", "" + currentAccessToken);
                checkIfExists();
            }
        };
    }

    private void setupProfileTracker() {
        mProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                Log.d("HouseShare", "" + currentProfile);
                details.setText(constructWelcomeMessage(currentProfile));
            }
        };
    }

    public void onResume() {
        super.onResume();

        Profile profile = Profile.getCurrentProfile();
        if (profile != null) {
            details.setText(constructWelcomeMessage(profile));
            checkIfExists();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        checkIfExists();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_fblogin, container, false);
    }

    public void startJoinHouseActivity(Account acc){
        Intent i = new Intent(getActivity(), JoinHouse.class);
        Bundle b = new Bundle();
        b.putParcelable("Account", acc);
        i.putExtra("Bundle", b);

        startActivity(i);
    }

    public void startMainActivity(Account acc) {

        Intent i = new Intent(getActivity(), MainActivity.class);
        Bundle b = new Bundle();
        b.putParcelable("Account", acc);
        i.putExtra("Bundle", b);
        startActivity(i);

    }

    public boolean isLoggedIn() {

        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        if (accessToken == null || accessToken.isExpired())
            return false;
        else
            return true;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        setupTextDetails(view);
        setupLoginButton(view);

        if(Profile.getCurrentProfile() != null){
            checkIfExists();
        }

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
        mListener = null;
    }

    @Override
    public void onStop() {
        super.onStop();
        mTokenTracker.stopTracking();
        mProfileTracker.stopTracking();
    }


    private void setupLoginButton(View view) {
        LoginButton mButtonLogin = (LoginButton) view.findViewById(R.id.login_button);
        mButtonLogin.setFragment(this);
        mButtonLogin.setReadPermissions(Arrays.asList("public_profile", "user_location", "user_birthday", "user_likes", "user_friends", "user_about_me"));
        mButtonLogin.registerCallback(mCallbackManager, mCallback);
    }

    private void setupTextDetails(View view) {
        details = (TextView) getView().findViewById(R.id.text_details);

        MaterialDialog materialDialog = new MaterialDialog.Builder(this.getContext()).title("Error").build();

    }

    private void createAndShowDialog(String title, String message){
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this.getContext())
                .title(title)
                .content(message)
                .positiveText("Ok");
    }

    private void createAndShowProgress(String title, String content){

        progress = new ProgressDialog(this.getContext());
        progress.setTitle(title);
        progress.setMessage(content);
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.show();

    }

    private String constructWelcomeMessage(Profile profile) {
        StringBuffer stringBuffer = new StringBuffer();
        if (profile != null) {
            stringBuffer.append("Welcome " + profile.getName());
        }
        return stringBuffer.toString();
    }

    public void checkIfExists(){

        createAndShowProgress("Logging in", "Logging you in...");

        if (Profile.getCurrentProfile() != null) {
            mAccountTable.where().field("facebookID").eq(Profile.getCurrentProfile().getId()).execute(new TableQueryCallback<Account>() {
                @Override
                public void onCompleted(List<Account> result, int count, Exception exception, ServiceFilterResponse response) {
                    if (exception == null) {
                        if (count == 0) {
                            populateProfile(Profile.getCurrentProfile());
                        } else {
                            newAccount = result.get(0);

                            if (newAccount.getHouseID() == -1) {
                                progress.hide();
                                startJoinHouseActivity(newAccount);
                            } else
                                progress.hide();
                            startMainActivity(newAccount);
                        }
                    } else {
                        exception.printStackTrace();
                    }
                }
            });
        }

    }

    public void populateProfile(final Profile toUpdate) {

        if (toUpdate != null) {

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
                                                public void onCompleted(JSONObject object, GraphResponse response) {
                                                    JSONresponse = object;

                                                    newAccount = new Account(toUpdate.getId(), toUpdate.getFirstName() + " " + toUpdate.getLastName());

                                                    String name;
                                                    String birthday;
                                                    String location;
                                                    String about;
                                                    String url;

                                                    try {
                                                        birthday = JSONresponse.getString("birthday");
                                                        newAccount.setBirthday(birthday);
                                                    } catch (Exception ex) {

                                                        ex.printStackTrace();

                                                    }

                                                    try {
                                                        about = JSONresponse.getString("bio");
                                                        newAccount.setAbout(about);
                                                    } catch (Exception ex) {
                                                        ex.printStackTrace();
                                                    }

                                                    try {
                                                        location = JSONresponse.getJSONObject("location").getString("name");
                                                        newAccount.setLocation(location);
                                                    } catch (Exception ex) {
                                                        ex.printStackTrace();
                                                    }
                                                    try {
                                                        url = JSONresponse.getJSONObject("cover").getString("source");
                                                        newAccount.setCoverPhotoURL(url);
                                                    } catch (Exception ex){
                                                        ex.printStackTrace();
                                                    }

                                                    upload(newAccount);
                                                }

                                            });

                                    Bundle parameters = new Bundle();
                                    parameters.putString("fields", "id,name,bio,birthday,location,cover");
                                    request.setParameters(parameters);
                                    request.executeAsync();
                                }
                            } else
                                createAndShowDialog("Error", exception.getMessage());
                                exception.printStackTrace();
                        }
                    });
    }

    else

    {
        Log.d("Facebook", "NULL PROFILE");
    }
}

    public void upload(Account item) {

        mClient.getTable(Account.class).insert(item, new TableOperationCallback<Account>() {
            public void onCompleted(Account entity, Exception exception, ServiceFilterResponse response) {
                if (exception == null) {

                    progress.hide();
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
}
