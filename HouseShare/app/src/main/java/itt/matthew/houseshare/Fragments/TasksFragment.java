package itt.matthew.houseshare.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.BundleCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import itt.matthew.houseshare.Activities.CostDetails;
import itt.matthew.houseshare.Activities.NewCost;
import itt.matthew.houseshare.Activities.NewTask;
import itt.matthew.houseshare.Activities.TaskDetails;
import itt.matthew.houseshare.Adapters_CustomViews.RVAdapter;
import itt.matthew.houseshare.Adapters_CustomViews.RVTasksAdapter;
import itt.matthew.houseshare.Events.MessageEvent;
import itt.matthew.houseshare.Events.RequestDetailsEvent;
import itt.matthew.houseshare.Events.UpdateAccountEvent;
import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.Models.Cost;
import itt.matthew.houseshare.Models.House;
import itt.matthew.houseshare.Models.Task;
import itt.matthew.houseshare.R;

public class TasksFragment extends Fragment  {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private House house;
    private Account current;
    private Boolean personal;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private RVTasksAdapter adapter;
    private MobileServiceClient mClient;
    private MobileServiceTable<Account> mAccountTable;
    private MobileServiceTable<House> mHouseTable;
    FinanceFragment.OnItemTouchListener onItemTouchListener;


    public static final String SHAREDPREFFILE = "temp";
    public static final String USERIDPREF = "uid";
    public static final String TOKENPREF = "tkn";
    public static final String FBTOKENPREF = "fbt";

    public TasksFragment() {
        // Required empty public constructor
    }



    @Subscribe
    public void onUpdateAccountEvent(UpdateAccountEvent event){
        current = event.getAccount();
        house = event.getHouse();
    }



    public static TasksFragment newInstance(String param1, String param2) {
        TasksFragment fragment = new TasksFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tasks, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        setupAzure();
        setupData();
        setupUI();
        super.onViewCreated(view, savedInstanceState);
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




    private void setupData() {

        Bundle extra = getActivity().getIntent().getExtras().getBundle("extra");
        current= extra.getParcelable("account");
        house = extra.getParcelable("house");
        personal = extra.getBoolean("personal");
    }

    private void setupUI(){

        com.getbase.floatingactionbutton.FloatingActionButton fab = (com.getbase.floatingactionbutton.FloatingActionButton) getView().findViewById(R.id.tasks_action_a);
        fab.setTitle("New Task");
        fab.setIcon(R.drawable.ic_assignment_black_24dp);


        if (personal){

            FloatingActionsMenu menu = (FloatingActionsMenu) getView().findViewById(R.id.tasks_multiple_actions);
            menu.setVisibility(View.GONE);
        }

        onItemTouchListener = new FinanceFragment.OnItemTouchListener() {
            @Override
            public void onItemViewTouch(View view, int position) {
                Intent i = new Intent(view.getContext(), TaskDetails.class);
                Bundle b = new Bundle();
                b.putParcelable("house", house);
                b.putParcelable("account", current);
                b.putInt("task", position);
                i.putExtra("extra", b);
                view.getContext().startActivity(i);
            }

            @Override
            public void onItemHeld(View view, final int position) {


                Boolean isOp = false;

                for (int i = 0; i < house.getOperators().size(); i++) {
                    if (current.getFacebookID().equals(house.getOperators().get(i))) {
                        isOp = true;
                    }
                }

                if (isOp) {
                    MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                            .title("Archive?")
                            .content("Would you like to Archive this Task?")
                            .positiveText(R.string.confirm)
                            .negativeText(R.string.cancel)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {

                                    ArrayList<Task> toMove = house.getTask();
                                    Task toAdd = house.getTask().get(position);
                                    toMove.remove(position);
                                    ArrayList<Task> archive = house.getArchivedTasks();
                                    archive.add(toAdd);


                                    house.setTasks(toMove);
                                    house.setArchivedTasks(archive);
                                    updateItem(house);


                                }
                            })
                            .show();
                }
            }

            ;
        };

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EventBus.getDefault().post(new RequestDetailsEvent('t'));
            }
        });



        mRecyclerView = (RecyclerView) getView().findViewById(R.id.tasks_rv);

        if(house.getTask().size() == 0) {

            mRecyclerView.setVisibility(View.GONE);
            CardView empty = (CardView) getView().findViewById(R.id.tasks_empty);
            empty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new RequestDetailsEvent('t'));
                }
            });
            empty.setVisibility(View.VISIBLE);

        }else
        {

                mLayoutManager = new LinearLayoutManager(getActivity());
                mRecyclerView.setLayoutManager(mLayoutManager);


                if (!personal) {
                    mRecyclerView.setPadding(0, 150, 0, 0);
                }


                adapter = new RVTasksAdapter(house, current, personal, onItemTouchListener);

                mRecyclerView.setAdapter(adapter);
                mRecyclerView.setClickable(true);
            }
        }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }




    private void populateItems(House house){

        EventBus.getDefault().post(new RequestDetailsEvent('f'));


        if (house.getTask().size() == 0) {

            mRecyclerView.setVisibility(View.GONE);
            CardView empty = (CardView) getView().findViewById(R.id.tasks_empty);
            empty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new RequestDetailsEvent('h'));
                }
            });
            empty.setVisibility(View.VISIBLE);

        }else {
            adapter = new RVTasksAdapter(house, current, personal, onItemTouchListener);
            mRecyclerView.setAdapter(adapter);
        }
    }



    private void updateItem(final House item) {
        if (mClient == null) {
            return;
        }

        final ProgressDialog progress = new ProgressDialog(this.getContext());
        progress.setTitle(R.string.updating);
        progress.setMessage(getString(R.string.updatingHouse));
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
                                    .content("Task Archived Successfully")
                                    .positiveText(R.string.confirm).show();

                            populateItems(item);
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

                            populateItems(item);
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


    private void populateItems(){

        EventBus.getDefault().post(new RequestDetailsEvent('f'));

        if (house.getTask().size() == 0) {

            mRecyclerView.setVisibility(View.GONE);
            CardView empty = (CardView) getView().findViewById(R.id.tasks_empty);
            empty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new RequestDetailsEvent('h'));
                }
            });
            empty.setVisibility(View.VISIBLE);

        }else {
            adapter = new RVTasksAdapter(house, current, personal, onItemTouchListener);
            mRecyclerView.setAdapter(adapter);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().post(new MessageEvent("Tasks", 2));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
