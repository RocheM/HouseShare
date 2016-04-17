package itt.matthew.houseshare.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.BundleCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import itt.matthew.houseshare.Activities.NewCost;
import itt.matthew.houseshare.Activities.NewTask;
import itt.matthew.houseshare.Events.MessageEvent;
import itt.matthew.houseshare.Events.RequestDetailsEvent;
import itt.matthew.houseshare.Events.UpdateAccountEvent;
import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.Models.House;
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

            Bundle extra = getActivity().getIntent().getExtras().getBundle("extra");
            current= extra.getParcelable("account");
            house = extra.getParcelable("house");
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

        setupUI();
        super.onViewCreated(view, savedInstanceState);
    }

    private void setupUI(){

        com.getbase.floatingactionbutton.FloatingActionButton fab = (com.getbase.floatingactionbutton.FloatingActionButton) getView().findViewById(R.id.tasks_action_a);
        fab.setTitle("New Task");
        fab.setIcon(R.drawable.ic_assignment_black_24dp);

        com.getbase.floatingactionbutton.FloatingActionButton fab2 = (com.getbase.floatingactionbutton.FloatingActionButton) getView().findViewById(R.id.tasks_action_b);
        fab2.setTitle("View Archive");
        fab2.setIcon(R.drawable.ic_assignment_turned_in_black_24dp);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EventBus.getDefault().post(new RequestDetailsEvent('t'));
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "TODO", Toast.LENGTH_LONG).show();
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

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
}
