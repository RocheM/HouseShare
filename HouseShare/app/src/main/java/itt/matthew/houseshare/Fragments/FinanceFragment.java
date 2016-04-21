package itt.matthew.houseshare.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.TableQueryCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import itt.matthew.houseshare.Activities.CostDetails;
import itt.matthew.houseshare.Activities.NewCost;
import itt.matthew.houseshare.Adapters_CustomViews.RVAdapter;
import itt.matthew.houseshare.Events.LongPressEvent;
import itt.matthew.houseshare.Events.MessageEvent;
import itt.matthew.houseshare.Events.ReplyEvent;
import itt.matthew.houseshare.Events.RequestDetailsEvent;
import itt.matthew.houseshare.Events.UpdateAccountEvent;
import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.Models.Cost;
import itt.matthew.houseshare.Models.CostInstance;
import itt.matthew.houseshare.Models.CostSplit;
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
    private Boolean personal = false;

    private OnItemTouchListener itemTouch;



    public static final String SHAREDPREFFILE = "temp";
    public static final String USERIDPREF = "uid";
    public static final String TOKENPREF = "tkn";
    public static final String FBTOKENPREF = "fbt";



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
        personal = extra.getBoolean("personal");


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

    @Subscribe
    public void onEvent(final LongPressEvent event){

        final int location = event.getLocation();
        final Cost item = event.getCost();
        Boolean isOp = false;

        for (int i = 0; i < current_house.getOperators().size(); i++){
            if(event.getCurrent().getFacebookID().equals(current_house.getOperators().get(i))){
                isOp = true;
            }
        }

        if (isOp) {
            MaterialDialog dialog = new MaterialDialog.Builder(this.getContext())
                    .title("Archive?")
                    .content("Would you like to Archive this cost?")
                    .positiveText(R.string.confirm)
                    .negativeText(R.string.cancel)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {

                            ArrayList<Cost> toMove = current_house.getCost();
                            toMove.remove(location);
                            ArrayList<Cost> archive = current_house.getArchivedCosts();
                            archive.add(item);


                            for (int i = 0; i < toMove.size(); i++) {
                                Log.d("Costs", toMove.get(i).getCategory().getName());
                            }


                            current_house.setCosts(toMove);
                            current_house.setArchivedCosts(archive);
                            updateItem(current_house);


                        }
                    })
                    .show();
        } else {


            ArrayList<Account> Ops = new ArrayList<>();
            String content = "Administrators:\n";
            for (int i = 0; i < current_house.getOperators().size(); i++){
                for (int j = 0; j < current_house.getMembers().size(); j++){
                    if(current_house.getOperators().get(i).equals(current_house.getMembers().get(j).getFacebookID())){
                        Ops.add(current_house.getMembers().get(j));
                        content = content.concat(current_house.getMembers().get(j).getName() + "\n");
                    }
                }
            }


            MaterialDialog dialog = new MaterialDialog.Builder(this.getContext())
                    .title("Error")
                    .content("You must be a House Administrator to Archive a Cost.\n\n" + content)
                    .positiveText(R.string.confirm)
                    .show();
        }
    }


    private void updateItem(final House item) {
        if (mClient == null) {
            return;
        }

        final ProgressDialog progress = new ProgressDialog(this.getContext());
        progress.setTitle("Updating");
        progress.setMessage("Updating House...");
        progress.show();

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    mHouseTable.update(item).get();
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            progress.hide();


                            MaterialDialog success = new MaterialDialog.Builder(getContext())
                                    .title("Success")
                                    .content("Cost Archived Successfully")
                                    .positiveText(R.string.confirm).show();

                            populateItems();
                        }
                    });
                } catch (Exception exception) {

                    final Exception ex = exception;

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MaterialDialog error = new MaterialDialog.Builder(getContext())
                                    .title("Error")
                                    .content(ex.getMessage())
                                    .positiveText(R.string.confirm).show();
                        }
                    });
                }
                return null;
            }
        }.execute();
    }




    private void updateItemBG(final House item) {
        if (mClient == null) {
            return;
        }

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    mHouseTable.update(item).get();
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {

                            populateItems();
                            Log.d("Expired", "Item Expired");
                        }
                    });
                } catch (Exception exception) {


                    exception.printStackTrace();
                }
                return null;
            }
        }.execute();
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
    if (!personal) {
        itemTouch = new OnItemTouchListener() {
            @Override
            public void onItemViewTouch(View view, int position) {
                Intent i = new Intent(view.getContext(), CostDetails.class);
                Bundle b = new Bundle();
                b.putParcelable("house", current_house);
                b.putParcelable("account", current_account);
                b.putInt("cost", position);
                i.putExtra("extra", b);
                view.getContext().startActivity(i);

            }

            @Override
            public void onItemHeld(View view, final int position) {

                Boolean isOp = false;

                for (int i = 0; i < current_house.getOperators().size(); i++) {
                    if (current_account.getFacebookID().equals(current_house.getOperators().get(i))) {
                        isOp = true;
                    }
                }

                if (isOp) {
                    MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                            .title("Archive?")
                            .content("Would you like to Archive this cost?")
                            .positiveText(R.string.confirm)
                            .negativeText(R.string.cancel)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {

                                    ArrayList<Cost> toMove = current_house.getCost();
                                    Cost toAdd = current_house.getCost().get(position);
                                    toMove.remove(position);
                                    ArrayList<Cost> archive = current_house.getArchivedCosts();
                                    archive.add(toAdd);


                                    current_house.setCosts(toMove);
                                    current_house.setArchivedCosts(archive);
                                    updateItem(current_house);


                                }
                            })
                            .show();
                } else {


                    ArrayList<Account> Ops = new ArrayList<>();
                    String content = "Administrators:\n";
                    for (int i = 0; i < current_house.getOperators().size(); i++) {
                        for (int j = 0; j < current_house.getMembers().size(); j++) {
                            if (current_house.getOperators().get(i).equals(current_house.getMembers().get(j).getFacebookID())) {
                                Ops.add(current_house.getMembers().get(j));
                                content = content.concat(current_house.getMembers().get(j).getName() + "\n");
                            }
                        }
                    }


                    MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                            .title("Error")
                            .content("You must be a House Administrator to Archive a Cost.\n\n" + content)
                            .positiveText(R.string.confirm)
                            .show();
                }

            }
        };
    }else {
        itemTouch = new OnItemTouchListener() {
            @Override
            public void onItemViewTouch(View view, int position) {
                Intent i = new Intent(view.getContext(), CostDetails.class);
                Bundle b = new Bundle();
                b.putParcelable("house", current_house);
                b.putParcelable("account", current_account);
                b.putInt("cost", position);
                i.putExtra("extra", b);
                view.getContext().startActivity(i);

            }

            @Override
            public void onItemHeld(View view, int position) {

            }
        };
    }
        View rootView = inflater.inflate(R.layout.fragment_finance, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv);

        if (!personal){
            mRecyclerView.setPadding(0, 150, 0, 0);
        }

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        adapter = new RVAdapter(current_house, current_account, personal, itemTouch);

        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setClickable(true);

       // checkExpired();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupUI();
        populateItems();
        setupAzure();
    }

    private void setupUI(){

        if (!personal) {

            com.getbase.floatingactionbutton.FloatingActionButton fab = (com.getbase.floatingactionbutton.FloatingActionButton) getView().findViewById(R.id.action_a);
            fab.setTitle("New Cost");
            fab.setIcon(R.drawable.ic_note_add_24dp);

            com.getbase.floatingactionbutton.FloatingActionButton fab2 = (com.getbase.floatingactionbutton.FloatingActionButton) getView().findViewById(R.id.action_b);
            fab2.setTitle("View Archive");
            fab2.setIcon(R.drawable.ic_insert_drive_file_24dp);


            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    EventBus.getDefault().post(new RequestDetailsEvent('h'));
                }
            });

            fab2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "TODO", Toast.LENGTH_LONG).show();
                }
            });
        }


        else{
                FloatingActionsMenu menu = (FloatingActionsMenu) getView().findViewById(R.id.multiple_actions);
                menu.setVisibility(View.GONE);


                com.getbase.floatingactionbutton.FloatingActionButton fab = (com.getbase.floatingactionbutton.FloatingActionButton) getView().findViewById(R.id.action_a);
                fab.setTitle("New Cost");
                fab.setIcon(R.drawable.ic_note_add_24dp);
                fab.setVisibility(View.GONE);

                com.getbase.floatingactionbutton.FloatingActionButton fab2 = (com.getbase.floatingactionbutton.FloatingActionButton) getView().findViewById(R.id.action_b);
                fab2.setTitle("View Archive");
                fab2.setIcon(R.drawable.ic_insert_drive_file_24dp);
                fab2.setVisibility(View.GONE);

        }
    }

    private void checkExpired(){

        Boolean expired = false;

        for(int i = 0; i < current_house.getCost().size(); i++) {
            CostInstance last = current_house.getCost().get(i).getIntervals().get(current_house.getCost().size() - 1);

            Calendar lastDate = last.getDate();
            Calendar current = Calendar.getInstance();
            current.add(Calendar.DATE,current_house.getCost().get(i).getInterval());

            if(lastDate.after(current)){
                expired = true;
                ArrayList<Cost> newCosts = current_house.getCost();
                ArrayList<Cost> newArchive = current_house.getArchivedCosts();

                newCosts.remove(i);
                newArchive.add(current_house.getCost().get(i));
                current_house.setCosts(newCosts);
                current_house.setArchivedCosts(newArchive);
            }
        }

        if (expired){
            updateItemBG(current_house);
        }
    };


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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    private void setupAzure(){


        try {
            mClient = new MobileServiceClient(
                    "https://backendhs.azurewebsites.net",
                    this.getActivity()
            );


        } catch (Exception e) {
            new MaterialDialog.Builder(this.getActivity())
                    .title("Error")
                    .content(e.getMessage())
                    .positiveText("Ok")
                    .show();

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




    private void populateItems(){

        adapter = new RVAdapter(current_house, current_account, personal, itemTouch);
        mRecyclerView.setAdapter(adapter);

        adapter = new RVAdapter(current_house, current_account, personal, itemTouch);
        mRecyclerView.setAdapter(adapter);

    }


    public interface OnItemTouchListener {
        void onItemViewTouch(View view, int position);
        void onItemHeld(View view, int position);
    }

}
