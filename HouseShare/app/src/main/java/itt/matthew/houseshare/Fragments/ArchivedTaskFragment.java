package itt.matthew.houseshare.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import itt.matthew.houseshare.Adapters_CustomViews.ArchiveAdapter;
import itt.matthew.houseshare.Adapters_CustomViews.ArchiveTaskAdapter;
import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.Models.House;
import itt.matthew.houseshare.R;

public class ArchivedTaskFragment extends Fragment {


    private Account current;
    private House house;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;



    public ArchivedTaskFragment() {
    }


    public static ArchivedTaskFragment newInstance(String param1, String param2) {
        ArchivedTaskFragment fragment = new ArchivedTaskFragment();
        Bundle args = new Bundle();
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

        ArchivedTaskFragment.OnItemTouchListener itemTouch = new ArchivedTaskFragment.OnItemTouchListener() {
            @Override
            public void onCardViewTouch(View view, int position) {
                Toast.makeText(view.getContext(), Integer.toString(position), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCardViewHeld(View view, int position) {

            }
        };


        mRecyclerView = (RecyclerView) getView().findViewById(R.id.archived_task_RV);


        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        ArchiveTaskAdapter adapter = new ArchiveTaskAdapter(house, current, itemTouch);

        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setClickable(true);


    }

    private void setupData(){

        Bundle b = getActivity().getIntent().getBundleExtra("extra");
        house = b.getParcelable("house");
        current = b.getParcelable("account");

    }




    public class OnItemTouchListener {
        public void onCardViewTouch(View item, int position) {
        }
        public void onCardViewHeld(View item, int position) {
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_archived_task, container, false);
    }

}
