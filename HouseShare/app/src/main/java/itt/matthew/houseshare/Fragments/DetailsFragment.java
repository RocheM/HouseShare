package itt.matthew.houseshare.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.TableQueryCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import itt.matthew.houseshare.Events.UpdateAccountEvent;
import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.Events.AccountEvent;
import itt.matthew.houseshare.Adapters_CustomViews.GridAdapter;
import itt.matthew.houseshare.Adapters_CustomViews.GridItem;
import itt.matthew.houseshare.Models.House;
import itt.matthew.houseshare.Events.MessageEvent;
import itt.matthew.houseshare.R;

public class DetailsFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private MobileServiceClient mClient;
    private MobileServiceTable<Account> mAccountTable;
    private MobileServiceTable<House> mHouseTable;
    private Account current;
    private House house;
    private GridView gridView;

    private float x1,x2;
    static final int MIN_DISTANCE = 150;

    public static final String SHAREDPREFFILE = "temp";
    public static final String USERIDPREF = "uid";
    public static final String TOKENPREF = "tkn";
    public static final String FBTOKENPREF = "fbt";




    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public DetailsFragment() {
        // Required empty public constructor
    }

    public static DetailsFragment newInstance(String param1, String param2) {
        DetailsFragment fragment = new DetailsFragment();
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

        Bundle extra = getActivity().getIntent().getExtras().getBundle("extra");
        current = extra.getParcelable("account");
        house = extra.getParcelable("house");

        lookupAccount(current.getFacebookID());



        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }




    @Subscribe
    public void onUpdateAccountEvent(UpdateAccountEvent event){
        current = event.getAccount();
        house = event.getHouse();
    }


    @Override
    public void onResume() {
        super.onResume();

        setupAzure();
        lookupAccount(current.getFacebookID());
        EventBus.getDefault().post(new MessageEvent("My House Mates",  0));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gridView = (GridView)getView().findViewById(R.id.gridview);

    }

    private void loadImages(){

        List<GridItem> toAddList = new ArrayList<GridItem>();

        for(int i = 0; i < house.getMembers().size(); i++){
            ImageView imageHolder = new ImageView(getContext());
            String name = house.getMembers().get(i).getName();
            String url = "https://graph.facebook.com/"+ house.getMembers().get(i).getFacebookID() +"/picture?type=large";
            toAddList.add(new GridItem(name, imageHolder, url));
        }


        gridView.setAdapter(new GridAdapter(this.getContext(), toAddList));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                EventBus.getDefault().post(new AccountEvent(house.getMembers().get(position), house));
            }
        });
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details, container, false);
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    public void setupAzure(){


        try {
            mClient = new MobileServiceClient(
                    "https://backendhs.azurewebsites.net",
                    this.getActivity());

        } catch (Exception e) {


        }

        loadUserTokenCache(mClient);
        mAccountTable = mClient.getTable(Account.class);
        mHouseTable = mClient.getTable(House.class);
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


    public void lookupAccount(final String facebookID) {


        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    mAccountTable.where()
                            .field("facebookID")
                            .eq(facebookID)
                            .execute(new TableQueryCallback<Account>() {
                                         @Override
                                         public void onCompleted(List<Account> result, int count, Exception exception, ServiceFilterResponse response) {
                                             if (exception == null) {
                                                 current = result.get(0);
                                                 lookupHouse();
                                             } else
                                                 exception.printStackTrace();
                                         }
                                     }
                            );
                } catch (Exception exception) {
                    exception.printStackTrace();
                }


                return null;
            }

        }.execute();
    }



    public void lookupHouse(){

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    mHouseTable.where()
                            .field("HouseID")
                            .eq(current.getHouseID())
                            .execute(new TableQueryCallback<House>() {
                                         @Override
                                         public void onCompleted(List<House> result, int count, Exception exception, ServiceFilterResponse response) {
                                             if (exception == null) {
                                                 house = new House(result.get(0));
                                                 EventBus.getDefault().post(new UpdateAccountEvent(current, house));
                                                 loadImages();

                                             } else
                                                 exception.printStackTrace();
                                         }
                                     }
                            );
                } catch (Exception exception) {
                    exception.printStackTrace();
                }


                return null;
            }

        }.execute();

    }

}
