package itt.matthew.houseshare.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;

import itt.matthew.houseshare.Events.OverviewEvent;
import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.Models.Cost;
import itt.matthew.houseshare.Models.House;
import itt.matthew.houseshare.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalCostOverview extends Fragment {


    private House house;
    private Account current;
    private Account selected;
    private Cost cost;

    public PersonalCostOverview() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_personal_cost_overview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupData();
    }



    private void setupData(){

        Bundle extras = this.getActivity().getIntent().getBundleExtra("extra");
        house = extras.getParcelable("house");
        int costLocation = extras.getInt("cost");
        int selectedLoc = extras.getInt("selected");
        current = extras.getParcelable("account");
        cost = house.getCost().get(costLocation);

        selected = house.getMembers().get(selectedLoc);

        Toast.makeText(this.getContext(), selected.getName(), Toast.LENGTH_SHORT).show();
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
