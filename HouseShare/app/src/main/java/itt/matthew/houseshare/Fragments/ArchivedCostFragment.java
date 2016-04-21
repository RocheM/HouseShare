package itt.matthew.houseshare.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import itt.matthew.houseshare.Adapters_CustomViews.ArchiveAdapter;
import itt.matthew.houseshare.Adapters_CustomViews.RVAdapter;
import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.Models.House;
import itt.matthew.houseshare.R;
public class ArchivedCostFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private Account current;
    private House house;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;


    public ArchivedCostFragment() {
    }

    public static ArchivedCostFragment newInstance(String param1, String param2) {
        ArchivedCostFragment fragment = new ArchivedCostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupData();
        setupUI();

    }

    private void setupUI(){

        ArchivedCostFragment.OnItemTouchListener itemTouch = new ArchivedCostFragment.OnItemTouchListener() {
            @Override
            public void onCardViewTouch(View view, int position) {
                Toast.makeText(view.getContext(), Integer.toString(position), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCardViewHeld(View view, int position) {

            }
        };


        mRecyclerView = (RecyclerView) getView().findViewById(R.id.archived_RV);


        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        ArchiveAdapter adapter = new ArchiveAdapter(house, current, itemTouch);

        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setClickable(true);


    }

    private void setupData(){

        Bundle b = getActivity().getIntent().getBundleExtra("extra");
        house = b.getParcelable("house");
        current = b.getParcelable("account");

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_archived_cost, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    public class OnItemTouchListener {
        public void onCardViewTouch(View item, int position) {
        }
        public void onCardViewHeld(View item, int position) {
        }
    }

}
