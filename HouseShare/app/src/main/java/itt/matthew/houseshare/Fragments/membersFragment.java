package itt.matthew.houseshare.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import itt.matthew.houseshare.Activities.AccountActivity;
import itt.matthew.houseshare.Adapters_CustomViews.MembersAdapter;
import itt.matthew.houseshare.Events.AccountEvent;
import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.Models.House;
import itt.matthew.houseshare.R;

public class membersFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private Account current;
    private House house;
    private membersFragment.OnItemTouchListener itemTouchListener;
    private ArrayList<Integer> moderators;

    public membersFragment() {
    }

    // TODO: Rename and change types and number of parameters
    public static membersFragment newInstance(String param1, String param2) {
        membersFragment fragment = new membersFragment();
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_members, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupData();
        setupUI();


    }

    private void setupUI() {

        itemTouchListener = new OnItemTouchListener(){
            @Override
            public void onCardViewTouch(View item, int position) {
                super.onCardViewTouch(item, position);

                    startAccountActivity(house.getMembers().get(position));

            }

            @Override
            public void onCardViewHeld(View item, int position) {
                super.onCardViewHeld(item, position);

                new MaterialDialog.Builder(item.getContext())
                        .title("Member Options")
                        .content("TODO")
                        .positiveText("Agree")
                        .icon(getView().getResources().getDrawable(R.drawable.ic_person_24dp))
                        .show();
            }
        };

        moderators = new ArrayList<>();

        for (int i = 0; i < house.getMembers().size(); i++){

            Boolean moderator = false;

            for (int j = 0; j < house.getOperators().size(); j++){
                if (house.getMembers().get(i).getFacebookID().equals(house.getOperators().get(j))){
                    moderator = true;
                }
            }

            if (moderator){
                moderators.add(1);
            }else
                moderators.add(0);
        }


        RecyclerView rv = (RecyclerView) getView().findViewById(R.id.members_rv);
        rv.setAdapter(new MembersAdapter(house, moderators, itemTouchListener, true));
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    private void startAccountActivity(Account account) {


            Bundle bundle = new Bundle();
            bundle.putParcelable("account", account);
            bundle.putParcelable("house", house);

            Intent i = new Intent(this.getContext(), AccountActivity.class);
            i.putExtra("extra", bundle);
            getActivity().finish();
            startActivity(i);

    }

    private void setupData(){
        
        Bundle b = getActivity().getIntent().getBundleExtra("extra");
        house = b.getParcelable("house");
        current = b.getParcelable("account");
        
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
