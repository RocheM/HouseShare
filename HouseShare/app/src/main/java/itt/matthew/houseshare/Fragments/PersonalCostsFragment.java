package itt.matthew.houseshare.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import itt.matthew.houseshare.Adapters_CustomViews.RVAdapter;
import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.Models.Cost;
import itt.matthew.houseshare.Models.House;
import itt.matthew.houseshare.R;

public class PersonalCostsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private Account account;
    private House house;


    public PersonalCostsFragment() {
    }


    private void setupData() {
        Bundle extras = this.getActivity().getIntent().getBundleExtra("extra");
        account = extras.getParcelable("account");
        house = extras.getParcelable("house");
    }


    // TODO: Rename and change types and number of parameters
    public static PersonalCostsFragment newInstance(String param1, String param2) {
        PersonalCostsFragment fragment = new PersonalCostsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupData();

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_personal_costs, container, false);


        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.personal_rv);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);


        House personalHouse = house;
        ArrayList<Cost> costs = house.getCost();
        ArrayList<Cost> personalCosts = new ArrayList<>();

        for (int i = 0; i < house.getCost().size(); i++){
            for (int j = 0; j < costs.get(i).getSplit().size(); j++){
                if (account.getFacebookID().equals(costs.get(i).getSplit().get(j).getUserFacebookID())){
                    Cost c = costs.get(i);
                    c.setAmount(costs.get(i).getSplit().get(j).getAmount());
                    personalCosts.add(c);
                    Log.d("ADDED", costs.get(i).getCategory().getName());
                }
            }
        }
        personalHouse.setCosts(personalCosts);

        mRecyclerView.setAdapter(new RVAdapter(personalHouse));
        mRecyclerView.setClickable(true);


        return rootView;
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
}
