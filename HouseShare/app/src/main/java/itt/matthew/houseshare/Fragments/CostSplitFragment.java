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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.common.eventbus.EventBus;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import itt.matthew.houseshare.Activities.MainActivity;
import itt.matthew.houseshare.Adapters_CustomViews.DialogAmountAdapter;
import itt.matthew.houseshare.Adapters_CustomViews.RVAccountAdapter;
import itt.matthew.houseshare.Adapters_CustomViews.RVAdapter;
import itt.matthew.houseshare.Events.CostEvent;
import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.Models.Cost;
import itt.matthew.houseshare.Models.CostSplit;
import itt.matthew.houseshare.Models.House;
import itt.matthew.houseshare.R;

public class CostSplitFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private android.support.v7.widget.RecyclerView mRecyclerView;
    private android.support.v7.widget.RecyclerView.LayoutManager mLayoutManager;
    private RVAccountAdapter adapter;
    private House current_house;
    private Account current_account;
    private OnItemTouchListener itemTouchListener;
    private ArrayList<CostSplit> customCostsSplits;
    private double customAmounts;
    private int customPosition;
    private Cost newCost = new Cost();



    public CostSplitFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CostSplitFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CostSplitFragment newInstance(String param1, String param2) {
        CostSplitFragment fragment = new CostSplitFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Subscribe
    public void onCostEvent(CostEvent costEvent){
        newCost = costEvent.getCost();
        adapter = new RVAccountAdapter(current_house, newCost, newCost.getSplit(), itemTouchListener);
        mRecyclerView.setAdapter(adapter);
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setupData();
        itemTouchListener = new OnItemTouchListener() {
            @Override
            public void onCardViewTouch(View view, int position) {
                onDialogSeekBarChanged seekBarChanged = new onDialogSeekBarChanged() {
                    @Override
                    public void onSeekBarChanged(View view, int position, double amount) {
                        customAmounts = amount;
                        customPosition = position;
                    }
                };

                new MaterialDialog.Builder(getContext())
                        .title("Test")
                        .adapter(new DialogAmountAdapter(getActivity(), seekBarChanged, position, newCost.getSplit(), current_house.getMembers()),
                                new MaterialDialog.ListCallback() {
                                    @Override
                                    public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {

                                    }
                                })
                        .positiveText("Submit")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(MaterialDialog dialog, DialogAction which) {
                                        ArrayList<CostSplit> costSplits = newCost.getSplit();
                                        costSplits.set(customPosition, new CostSplit(costSplits.get(customPosition).getUserFacebookID(), customAmounts));
                                        newCost.setSplit(costSplits);
                                        org.greenrobot.eventbus.EventBus.getDefault().post(new CostEvent(newCost));
                                    }
                                })
                                        .show();
            }
        };

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private void setupData(){


        Bundle extra = getActivity().getIntent().getExtras().getBundle("extra");
        current_account = extra.getParcelable("account");
        current_house = extra.getParcelable("house");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_cost_split, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.account_rv);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        newCost.setAmount(0);

        adapter = new RVAccountAdapter(current_house, newCost, newCost.getSplit(), itemTouchListener);

        mRecyclerView.setAdapter(adapter);
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


    @Override
    public void onResume() {
        super.onResume();

    }

    public interface onDialogSeekBarChanged {
        public void onSeekBarChanged(View view, int position, double amount);
    }

    public interface OnItemTouchListener {
        public void onCardViewTouch(View view, int position);
    }
}

