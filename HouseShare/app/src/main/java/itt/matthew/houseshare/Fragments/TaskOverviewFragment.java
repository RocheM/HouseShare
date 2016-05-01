package itt.matthew.houseshare.Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.prefs.MaterialEditTextPreference;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import itt.matthew.houseshare.Adapters_CustomViews.DateGridAdapter;
import itt.matthew.houseshare.Adapters_CustomViews.OverviewAdapter;
import itt.matthew.houseshare.Adapters_CustomViews.OverviewTaskAdapter;
import itt.matthew.houseshare.Events.OverviewEvent;
import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.Models.Cost;
import itt.matthew.houseshare.Models.House;
import itt.matthew.houseshare.Models.Task;
import itt.matthew.houseshare.R;

public class TaskOverviewFragment extends Fragment {


    private House house;
    private Account current;
    private Task task;
    private OnItemTouchListener itemTouchListener;
    private EditText notes;
    private TextView taskDescription;
    private RecyclerView rv;
    private int selectedTask;
    private int taskLocation;


    private MobileServiceClient mClient;
    private MobileServiceTable<Account> mAccountTable;
    private MobileServiceTable<House> mHouseTable;


    public static final String SHAREDPREFFILE = "temp";
    public static final String USERIDPREF = "uid";
    public static final String TOKENPREF = "tkn";
    public static final String FBTOKENPREF = "fbt";


    public TaskOverviewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task_overview, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupData();
        setupAzure();
        setupUI();
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


    private void setupData(){

        Bundle extras = this.getActivity().getIntent().getBundleExtra("extra");
        house = extras.getParcelable("house");
        int taskloc = extras.getInt("task");
        current = extras.getParcelable("account");
        task = house.getTask().get(taskloc);
        taskLocation = taskloc;

    }

    private void setupUI() {

        itemTouchListener = new OnItemTouchListener() {
            @Override
            public void onCardViewTouch(View view, int position) {


                selectedTask = position;
                Boolean isSelected = false;
                if (task.getTaskInstances().get(position).getAccount().equals(current.getFacebookID())){
                    isSelected = true;
                }

                Boolean isPaid = false;
                if (task.getTaskInstances().get(position).getPaid()){
                    isPaid = true;
                }

                if (isPaid) {

                    String content = "";
                    DateFormat formatter = DateFormat.getDateInstance(DateFormat.SHORT);
                    content = content.concat("Date Completed:\t" + formatter.format(task.getTaskInstances().get(selectedTask).getDate().getTime()) + "\nNotes:\t" + task.getTaskInstances().get(selectedTask).getNotes());

                    new MaterialDialog.Builder(getContext())
                            .title("Cost Details")
                            .content(content)
                            .positiveText(R.string.confirm)
                            .show();

                }
                else {

                    boolean wrapInScrollView = true;
                    MaterialDialog materialDialog = new MaterialDialog.Builder(view.getContext())
                            .title(R.string.confirmTask)
                            .customView(R.layout.dialog_confirm_task, wrapInScrollView)
                            .positiveText(R.string.markAsDone)
                            .negativeText(R.string.cancel)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                    task.getTaskInstances().get(selectedTask).setPaid(true);
                                    task.getTaskInstances().get(selectedTask).setNotes(notes.getText().toString());
                                    task.getTaskInstances().get(selectedTask).setPaidOn(Calendar.getInstance());

                                    ArrayList<Task> taskList = house.getTask();
                                    taskList.set(taskLocation, task);
                                    house.setTasks(taskList);

                                    updateItemBG(house);

                                }
                            }).onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    notes.setText("");
                                    dialog.dismiss();
                                }
                            }).build();

                    View v = materialDialog.getView();
                    notes = (EditText) v.findViewById(R.id.task_notes_input);
                    taskDescription = (TextView) v.findViewById(R.id.task_dialog_description);
                    taskDescription.setText(task.getArea().getDescription());

                    if (!isSelected && !isPaid) {
                        materialDialog =  new MaterialDialog.Builder(view.getContext())
                                .title(R.string.confirmTask)
                                .content(R.string.theOwnerHasNotCompletedThisTaskYet)
                                .positiveText(R.string.markAsDone)
                                .build();

                    }

                    materialDialog.show();
                }
            }
        };


        rv = (RecyclerView) getView().findViewById(R.id.task_overview_rv);
        rv.setLayoutManager(new GridLayoutManager(getView().getContext(), 2));
        rv.setAdapter(new OverviewTaskAdapter(house, current, getActivity().getApplicationContext(), task, itemTouchListener));
    }


    private void updateItemBG(final House item) {
        if (mClient == null) {
            return;
        }

        final ProgressDialog dialog = new ProgressDialog(this.getContext());
        dialog.setTitle("Updating Costs...");
        dialog.setMessage("Updating payment");
        dialog.show();

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    mHouseTable.update(item).get();
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {

                            dialog.hide();
                            MaterialDialog successDialog = new MaterialDialog.Builder(getContext())
                                    .title("Success")
                                    .content("Task Successfully Updated.")
                                    .positiveText("Okay")
                                    .show();

                            updateRV();
                        }
                    });
                } catch (Exception exception) {


                    exception.printStackTrace();
                }
                return null;
            }
        }.execute();
    }


    private void updateRV(){

        rv.setLayoutManager(new GridLayoutManager(this.getContext(), 2));
        rv.setLayoutManager(new GridLayoutManager(getView().getContext(), 2));
        rv.setAdapter(new OverviewTaskAdapter(house, current, getActivity().getApplicationContext(), task, itemTouchListener));

    }

    public interface OnItemTouchListener {
        void onCardViewTouch(View view, int position);
    }

}
