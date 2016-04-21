package itt.matthew.houseshare.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.thesurix.gesturerecycler.DefaultItemClickListener;
import com.thesurix.gesturerecycler.GestureAdapter;
import com.thesurix.gesturerecycler.GestureManager;
import com.thesurix.gesturerecycler.RecyclerItemTouchListener;

import java.util.ArrayList;

import itt.matthew.houseshare.Activities.AccountActivity;
import itt.matthew.houseshare.Adapters_CustomViews.MembersAdapter;
import itt.matthew.houseshare.Adapters_CustomViews.MembersReorderAdapter;
import itt.matthew.houseshare.Adapters_CustomViews.SimpleItemTouchHelperCallback;
import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.Models.House;
import itt.matthew.houseshare.R;

public class memberReorderFragment extends Fragment implements MembersReorderAdapter.OnDragStartListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private Account current;
    private House house;
    private ArrayList<Account> members;
    private GestureManager mGestureManager;
    private MembersReorderAdapter adapter;
    private ItemTouchHelper mItemTouchHelper;



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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupData();
        setupUI();

    }


    private void setupUI() {

        members = house.getMembers();
        final RecyclerView rv = (RecyclerView) getView().findViewById(R.id.member_reorder_rv);


        final LinearLayoutManager manager = new LinearLayoutManager(getContext());
        rv.setHasFixedSize(true);
        rv.setLayoutManager(manager);

        adapter = new MembersReorderAdapter(members, getContext(), this);
        rv.setAdapter(adapter);
        rv.addOnItemTouchListener(new RecyclerItemTouchListener(getActivity(), new DefaultItemClickListener() {
            @Override
            public boolean onItemClick(final View view, final int position) {
                Snackbar.make(view, "Click event on the " + position + " position", Snackbar.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public void onItemLongPress(final View view, final int position) {
                Snackbar.make(view, "Long press event on the " + position + " position", Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public boolean onDoubleTap(final View view, final int position) {
                Snackbar.make(view, "Double tap event on the " + position + " position", Snackbar.LENGTH_SHORT).show();
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
