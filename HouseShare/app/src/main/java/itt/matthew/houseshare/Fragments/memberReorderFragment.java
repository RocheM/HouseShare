package itt.matthew.houseshare.Fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thesurix.gesturerecycler.DefaultItemClickListener;
import com.thesurix.gesturerecycler.GestureManager;
import com.thesurix.gesturerecycler.RecyclerItemTouchListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import itt.matthew.houseshare.Adapters_CustomViews.MembersReorderAdapter;
import itt.matthew.houseshare.Adapters_CustomViews.SimpleItemTouchHelperCallback;
import itt.matthew.houseshare.Events.RequestTaskEvent;
import itt.matthew.houseshare.Events.TaskEvent;
import itt.matthew.houseshare.Events.AddToListEvent;
import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.Models.House;
import itt.matthew.houseshare.Models.Task;
import itt.matthew.houseshare.R;

public class memberReorderFragment extends Fragment implements MembersReorderAdapter.OnDragStartListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private Account current;
    private House house;
    private Task newTask = new Task();
    private ArrayList<Account> members;
    private GestureManager mGestureManager;
    private MembersReorderAdapter adapter;
    private ItemTouchHelper mItemTouchHelper;
    private RecyclerView rv;


    // TODO: Rename and chan types and number of parameters
    public static memberReorderFragment newInstance(String param1, String param2) {
        memberReorderFragment fragment = new memberReorderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public memberReorderFragment() {
        // Required empty public constructor
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
        return inflater.inflate(R.layout.fragment_member_reorder, container, false);
    }


    @Subscribe
    public void onAddToListEvent(AddToListEvent addToListEvent){
        ArrayList<Account> accountArrayList = adapter.getMembers();
        accountArrayList.add(addToListEvent.getToAdd());
        adapter = new MembersReorderAdapter(accountArrayList, getContext(), this);
        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(rv);
        rv.setAdapter(adapter);

    }

    @Subscribe
    public void onTaskEvent(TaskEvent taskEvent){
        newTask = taskEvent.getTask();
    }

    @Subscribe
    public void onRequestTaskEvent(RequestTaskEvent requestTaskEvent){


        if (requestTaskEvent.getReqestID() == 1){

            ArrayList<Account> accounts = adapter.getMembers();
            ArrayList<String> IDs = new ArrayList<>();

            for (int i =0 ; i < accounts.size(); i++) {
                IDs.add(accounts.get(i).getFacebookID());
            }

            newTask.setUsers(IDs);

            EventBus.getDefault().post(new TaskEvent(newTask));

        }

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupData();
        setupUI();

    }


    private void setupUI() {

        members = new ArrayList<>();
//        members.add(current);
        members = house.getMembers();

        rv = (RecyclerView) getView().findViewById(R.id.member_reorder_rv);


        final LinearLayoutManager manager = new LinearLayoutManager(getContext());
        rv.setHasFixedSize(true);
        rv.setLayoutManager(manager);

        adapter = new MembersReorderAdapter(members, getContext(), this);
        rv.setAdapter(adapter);
        rv.addOnItemTouchListener(new RecyclerItemTouchListener(getActivity(), new DefaultItemClickListener() {
            @Override
            public boolean onItemClick(final View view, final int position) {
                return true;
            }

            @Override
            public void onItemLongPress(final View view, final int position) {
            }

            @Override
            public boolean onDoubleTap(final View view, final int position) {
                return true;
            }
        }));

        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(rv);

    }

    private void setupData(){

        Bundle b = getActivity().getIntent().getBundleExtra("extra");
        house = b.getParcelable("house");
        current = b.getParcelable("account");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onStart() {
        super.onStart();
        org.greenrobot.eventbus.EventBus.getDefault().register(this);

    }

    @Override
    public void onStop() {
        super.onStop();
        org.greenrobot.eventbus.EventBus.getDefault().unregister(this);

    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onDragStarted(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
