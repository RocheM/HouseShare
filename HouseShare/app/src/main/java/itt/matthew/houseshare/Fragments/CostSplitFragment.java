package itt.matthew.houseshare.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.common.eventbus.EventBus;
import com.mikepenz.materialize.color.Material;

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
    private MaterialDialog dialog;
    private Cost newCost = new Cost();
    private int customCount;



    private void initalizeSplit(){

        CostSplit split;
        ArrayList<CostSplit> splits = new ArrayList<>();

        for (int i = 0; i < current_house.getMembers().size(); i++){
            split = new CostSplit(current_house.getMembers().get(i).getFacebookID(), current_house.getMembers().get(i).getName(), 0, false);
            splits.add(i, split);
        }

        newCost.setSplit(splits);

        org.greenrobot.eventbus.EventBus.getDefault().post(new CostEvent(newCost));

    }


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
        double amount = newCost.getAmount();
        double customAmount = calculateTotalCustomAmount(newCost.getSplit());

        TextView textView = (TextView) getView().findViewById(R.id.amountDisplay);
        textView.setText(String.format("%.2f", amount));
        TextView textView1 = (TextView) getView().findViewById(R.id.customAmountDisplay);
        textView1.setText(String.format("%.2f", customAmount));

        if (Double.parseDouble(textView1.getText().toString()) < Double.parseDouble(textView.getText().toString()))
        {
            textView1.setTextColor(getResources().getColor(R.color.md_red_400));
        }
        else
            textView1.setTextColor(getResources().getColor(R.color.colorPrimary));

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

               dialog = new MaterialDialog.Builder(getContext())
                        .title("Set Cost")
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
                                costSplits.set(customPosition, new CostSplit(costSplits.get(customPosition).getUserFacebookID(), costSplits.get(customPosition).getName(), customAmounts, true));
                                newCost.setSplit(costSplits);
                                double newSplitAmount = calculateAmount(costSplits);
                                newSplitAmount = newSplitAmount / (costSplits.size() - customCount);

                                for (int i = 0; i < costSplits.size(); i++) {
                                    if (!costSplits.get(i).getCustom()) {
                                        costSplits.get(i).setAmount(newSplitAmount);
                                    }
                                }

                                org.greenrobot.eventbus.EventBus.getDefault().post(new CostEvent(newCost));
                            }
                        })
                                        .show();
            }

            @Override
            public void onCheckViewTouch(View view, final int position){

                final MaterialDialog materialDialog = new MaterialDialog.Builder(getContext())
                        .title("Are you sure?")
                        .content("This will reset your custom amount")
                        .positiveText("Confirm")
                        .negativeText("Cancel")
                        .cancelable(false)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {

                                ArrayList<CostSplit> costSplits = newCost.getSplit();
                                costSplits.set(position, new CostSplit(costSplits.get(position).getUserFacebookID(), costSplits.get(position).getName(),newCost.getAmount() / costSplits.size() , false));
                                customCount--;
                                newCost.setSplit(costSplits);
                                double newSplitAmount = calculateAmount(costSplits);
                                newSplitAmount = newSplitAmount / (costSplits.size() - customCount);

                                for (int i = 0; i < costSplits.size(); i++) {
                                    if (!costSplits.get(i).getCustom()) {
                                        costSplits.get(i).setAmount(newSplitAmount);
                                    }
                                }

                                org.greenrobot.eventbus.EventBus.getDefault().post(new CostEvent(newCost));

                                dialog.dismiss();
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {


                                org.greenrobot.eventbus.EventBus.getDefault().post(new CostEvent(newCost));
                                dialog.dismiss();
                            }
                        })
                        .build();

                materialDialog.show();

            }
        };

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    private double calculateTotalCustomAmount(ArrayList<CostSplit> splits){

        double customAmount = 0;


        for (int i = 0; i < splits.size(); i++) {


            customAmount = customAmount + splits.get(i).getAmount();
        }



        return  customAmount;

    }

    private double calculateAmount(ArrayList<CostSplit> splits){

        double customAmount =0;
        customCount = 0;

        for (int i = 0; i < splits.size(); i++){

            if (splits.get(i).getCustom()){

                customCount++;
                customAmount = customAmount + splits.get(i).getAmount() ;
            }
        }


        return newCost.getAmount() - customAmount;
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
        initalizeSplit();

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
        public void onCheckViewTouch(View view, int position);

    }
}

