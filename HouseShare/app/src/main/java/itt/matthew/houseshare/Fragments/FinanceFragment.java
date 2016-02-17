package itt.matthew.houseshare.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.TableQueryCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import itt.matthew.houseshare.Activities.NewCost;
import itt.matthew.houseshare.Adapters_CustomViews.RVAdapter;
import itt.matthew.houseshare.Events.MessageEvent;
import itt.matthew.houseshare.Events.ReplyEvent;
import itt.matthew.houseshare.Events.RequestDetailsEvent;
import itt.matthew.houseshare.Events.UpdateAccountEvent;
import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.Models.House;
import itt.matthew.houseshare.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FinanceFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FinanceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FinanceFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RVAdapter adapter;

    private MobileServiceClient mClient;
    private android.support.v7.widget.RecyclerView mRecyclerView;
    private android.support.v7.widget.RecyclerView.LayoutManager mLayoutManager;

    private MobileServiceTable<Account> mAccountTable;
    private MobileServiceTable<House> mHouseTable;
    private Account current_account;
    private House current_house;




    public FinanceFragment() {
        // Required empty public constructor
    }




    @Subscribe
    public void onUpdateAccountEvent(UpdateAccountEvent event){
        current_account = event.getAccount();
        current_house = event.getHouse();

        populateItems();
    }

    public static FinanceFragment newInstance(String param1, String param2) {
        FinanceFragment fragment = new FinanceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extra = getActivity().getIntent().getExtras().getBundle("extra");
        current_account = extra.getParcelable("account");
        current_house = extra.getParcelable("house");


        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }



    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    @Subscribe
    public void onEvent(ReplyEvent event){

        current_account = event.getAccount();
        current_house = event.getHouse();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_finance, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        adapter = new RVAdapter(current_house);

        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setClickable(true);


        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupUI();
        populateItems();
    }

    private void setupUI(){


        com.getbase.floatingactionbutton.FloatingActionButton fab = (com.getbase.floatingactionbutton.FloatingActionButton)getView().findViewById(R.id.action_a);
        fab.setTitle("New Cost");
        fab.setIcon(R.drawable.ic_note_add_24dp);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EventBus.getDefault().post(new RequestDetailsEvent('h'));
            }
        });


    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onResume() {
        super.onResume();


        populateItems();
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
        void onFragmentInteraction(Uri uri);
    }


    public void setupAzure(){


        try {
            mClient = new MobileServiceClient(
                    "https://backendhs.azurewebsites.net",
                    this.getActivity());

        } catch (Exception e) {


        }

        mAccountTable = mClient.getTable(Account.class);
        mHouseTable = mClient.getTable(House.class);
    }


    private void populateItems(){

        adapter = new RVAdapter(current_house);
        mRecyclerView.setAdapter(adapter);

        adapter = new RVAdapter(current_house);
        mRecyclerView.setAdapter(adapter);

    }

}
