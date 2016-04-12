package itt.matthew.houseshare.Fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.greenrobot.eventbus.Subscribe;

import itt.matthew.houseshare.Adapters_CustomViews.OverviewAdapter;
import itt.matthew.houseshare.Events.CostEvent;
import itt.matthew.houseshare.Events.OverviewEvent;
import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.Models.Cost;
import itt.matthew.houseshare.Models.House;
import itt.matthew.houseshare.R;

public class CostOverviewFragment extends Fragment {


    private House house;
    private Account current;
    private Cost cost;
    private OnItemTouchListener itemTouchListener;

    public CostOverviewFragment() {



    }



    private void setupData(){

        Bundle extras = this.getActivity().getIntent().getBundleExtra("extra");
        house = extras.getParcelable("house");
        int costLocation = extras.getInt("cost");
        current = extras.getParcelable("account");
        cost = house.getCost().get(costLocation);

    }

    private void setupUI(){

        itemTouchListener = new OnItemTouchListener() {
            @Override
            public void onCardViewTouch(View view, int position) {

                org.greenrobot.eventbus.EventBus.getDefault().post(new OverviewEvent(position));

            }
        };



        RecyclerView rv = (RecyclerView) getView().findViewById(R.id.cost_overview_rv);
        rv.setLayoutManager(new LinearLayoutManager(this.getContext()));
        rv.setAdapter(new OverviewAdapter(house, current, getActivity().getApplicationContext(), cost, itemTouchListener));




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_cost_overview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupData();
        setupUI();
    }


    public interface OnItemTouchListener {
        void onCardViewTouch(View view, int position);
    }


    @Subscribe
    public void onEvent(OverviewEvent event){
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
}
