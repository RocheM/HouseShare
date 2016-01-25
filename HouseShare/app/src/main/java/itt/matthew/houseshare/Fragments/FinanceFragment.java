package itt.matthew.houseshare.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import de.greenrobot.event.EventBus;
import itt.matthew.houseshare.Models.Cost;
import itt.matthew.houseshare.Events.DateMessage;
import itt.matthew.houseshare.Models.Interval;
import itt.matthew.houseshare.Events.MessageEvent;
import itt.matthew.houseshare.Events.ReplyEvent;
import itt.matthew.houseshare.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FinanceFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FinanceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FinanceFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button testButton, startDateButton, endDateButton;
    private EditText startEdit, endEdit, category, amount;
    private int day, month, year;
    private Calendar startCalendar, endCalendar;
    private Cost newCost;
    private Spinner intervalSpinner;


    public FinanceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FinanceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FinanceFragment newInstance(String param1, String param2) {
        FinanceFragment fragment = new FinanceFragment();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);
    }

    public void onEvent(ReplyEvent event){

        day = event.getDay();
        month = event.getMonth();
        year = event.getYear();




        if(event.getType() == 's') {
            startEdit.setText(Integer.toString(day) + "/" + Integer.toString(month) + "/" + Integer.toString(year));
            startCalendar = new GregorianCalendar();
            startCalendar.set(year, month, day);
        }
        else {
            endEdit.setText(Integer.toString(day) + "/" + Integer.toString(month) + "/" + Integer.toString(year));
            endCalendar = new GregorianCalendar();
            endCalendar.set(year, month, day);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_finance, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupUI();
    }

    private int setUpInterval(int index){

        switch (index) {
            case 0:
                return 1;
            case 1:
                return 7;
            case 2:
                return 30;
            case 3:
                return 365;
        }

        return 1;

    }

    private void setupUI(){

        testButton = (Button)getView().findViewById(R.id.testButton);


        FloatingActionButton fab = (FloatingActionButton) getView().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });




        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean wrapInScrollView = true;
                MaterialDialog d = new MaterialDialog.Builder(getContext())
                        .title("New Cost")
                        .customView(R.layout.add_dialog, wrapInScrollView)
                        .positiveText("Create")
                        .negativeText("Cancel")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {


                                int intervalDays = setUpInterval(intervalSpinner.getSelectedItemPosition());

                                ArrayList<Integer> integers = new ArrayList<Integer>();
                                integers.add(0);
                                integers.add(1);

                                integers.add(2);

                                newCost = new Cost(intervalDays, category.getText().toString(),
                                                    Double.parseDouble(amount.getText().toString()),
                                                    integers, startCalendar, endCalendar);

                                Toast.makeText(getContext(), "Cost of category " + newCost.getCategory()
                                        + " amount: " + newCost.getAmount() + " reoccuring every " + Integer.toString(intervalDays) + " days", Toast.LENGTH_LONG).show();
                            }
                        })
                        .show();


                startEdit = (EditText)d.getView().findViewById(R.id.startDate);
                endEdit = (EditText)d.getView().findViewById(R.id.endDate);

                intervalSpinner = (Spinner) d.findViewById(R.id.IntervalSpinner);
                intervalSpinner.setAdapter(new ArrayAdapter<Interval>(getContext(), android.R.layout.simple_spinner_item, Interval.values()));


                category = (EditText) d.findViewById(R.id.Category);
                amount = (EditText) d.findViewById(R.id.Amount);


                startDateButton = (Button)d.getView().findViewById(R.id.startDateButton);
                startDateButton.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {

                        EventBus.getDefault().post(new DateMessage(0, 0, 0, 's'));

                    }
                });

                endDateButton = (Button) d.getView().findViewById(R.id.endDateButton);
                endDateButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        EventBus.getDefault().post(new DateMessage(0, 0, 0, 'e'));

                    }
                });


            }
        });


    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onResume() {
        super.onResume();


        EventBus.getDefault().post(new MessageEvent("Finance", 1));
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
