package itt.matthew.houseshare.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.SimpleFormatter;

import itt.matthew.houseshare.Activities.NewCost;
import itt.matthew.houseshare.Events.CostEvent;
import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.Models.Cost;
import itt.matthew.houseshare.Models.CostCategory;
import itt.matthew.houseshare.Models.CostSplit;
import itt.matthew.houseshare.Models.House;
import itt.matthew.houseshare.Models.Interval;
import itt.matthew.houseshare.R;

public class CreateCostFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";


    private House house;
    private Account account;
    private Cost WorkingCost = new Cost();

    private ArrayList<String> CategoryStrings = new ArrayList<String>();
    private Spinner intervalSpinner, numberSpinner;
    private int interval = 1, intervalNumber = 1;
    private Button startDateButton, endDateButton;
    private TextView startEdit, endEdit;
    private EditText amount;
    private MaterialDialog dialog;
    private char dateField;
    private int categorySelectedColor;
    private Calendar startDateSelected = new GregorianCalendar();
    private Calendar endDateSelected = new GregorianCalendar();
    private DatePickerDialog startDPD, endDPD;
    boolean error = false;

    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CreateCostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateCostFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateCostFragment newInstance(String param1, String param2) {
        CreateCostFragment fragment = new CreateCostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    private void setupData() {
        Bundle extras = this.getActivity().getIntent().getBundleExtra("extra");
        account = extras.getParcelable("account");
        house = extras.getParcelable("house");

        WorkingCost.setAmount(0);
        ArrayList<CostSplit> newCostSplit = new ArrayList<>();
        for(int i = 0; i < house.getMembers().size(); i++){
            CostSplit temp = new CostSplit(house.getMembers().get(i).getFacebookID(), 0);
            newCostSplit.add(temp);
        }
        WorkingCost.setSplit(newCostSplit);




        if (house.getCost() != null) {
            WorkingCost.setCostID(house.getCost().size() + 1);
            EventBus.getDefault().post(new CostEvent(WorkingCost));
        }
        else {
            WorkingCost.setCostID(0);
            EventBus.getDefault().post(new CostEvent(WorkingCost));
        }
        WorkingCost.setInterval(1);
        EventBus.getDefault().post(new CostEvent(WorkingCost));

    }


    @Subscribe
    public void onCostEvent(CostEvent costEvent){
        WorkingCost = costEvent.getCost();
    }


    private int generateInterval(int i){

        switch (i) {
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

    private void setupUI() {


        intervalSpinner = (Spinner) getView().findViewById(R.id.IntervalSpinnerUI);
        intervalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                interval = generateInterval(position);
                WorkingCost.setInterval(intervalNumber * interval);
                EventBus.getDefault().post(new CostEvent(WorkingCost));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                interval = 1;
                WorkingCost.setInterval(1);
                EventBus.getDefault().post(new CostEvent(WorkingCost));
            }
        });

        numberSpinner = (Spinner) getView().findViewById(R.id.numberSpinner);
        numberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                intervalNumber = position + 1;
                WorkingCost.setInterval(interval * intervalNumber);
                EventBus.getDefault().post(new CostEvent(WorkingCost));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                intervalNumber = 1;
                WorkingCost.setInterval(interval);
                EventBus.getDefault().post(new CostEvent(WorkingCost));
            }
        });



        startDateButton = (Button) getView().findViewById(R.id.startDateButton);
        endDateButton = (Button) getView().findViewById(R.id.endDateButton);
        startEdit = (TextView) getView().findViewById(R.id.startDate);
        endEdit = (TextView) getView().findViewById(R.id.endDate);


        amount = (EditText) getView().findViewById(R.id.Amount);
        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                try {
                    if (Double.parseDouble(s.toString()) < 0 || Double.parseDouble(s.toString()) == 0) {
                        amount.setError("Amount Cannot be negative or Zero");
                        Toast.makeText(getContext(), "Amount Cannot be negative or Zero", Toast.LENGTH_SHORT).show();
                        error = true;
                    } else {
                        WorkingCost.setAmount(Double.parseDouble(s.toString()));
                        ArrayList<CostSplit> costSplits = new ArrayList<CostSplit>();
                        double divide = WorkingCost.getAmount() / house.getMembers().size();
                        for (int i = 0; i < house.getMembers().size(); i++){
                            CostSplit split = new CostSplit(house.getMembers().get(i).getFacebookID(), divide);
                            costSplits.add(split);
                        }
                        WorkingCost.setSplit(costSplits);

                        error = false;
                        EventBus.getDefault().post(new CostEvent(WorkingCost));
                    }
                } catch (NumberFormatException ex){
                    ex.printStackTrace();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        startDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar now = Calendar.getInstance();
                startDPD = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                GregorianCalendar cal = new GregorianCalendar();
                                cal.set(year, monthOfYear, dayOfMonth);
                                if (cal.getTimeInMillis() < Calendar.getInstance().getTimeInMillis())
                                {
                                    startEdit.setError("Start Date Cannot be before current Date");
                                    startEdit.setText("Choose Start Date");
                                    Toast.makeText(getContext(), "Start Date Cannot be before current Date", Toast.LENGTH_SHORT).show();
                                    error = true;
                                }
                                else {
                                    startEdit.setError(null);
                                    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                                    String date = formatter.format(cal.getTime());
                                    startEdit.setText(date);
                                    startDateSelected = cal;
                                    WorkingCost.setStartDate(startDateSelected);
                                    error = false;
                                    EventBus.getDefault().post(new CostEvent(WorkingCost));
                                }
                            }
                        },
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                startDPD.show(getActivity().getFragmentManager(), "Pick Start Date");
            }
        });

        endDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Calendar now = Calendar.getInstance();
                endDPD = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                GregorianCalendar cal = new GregorianCalendar();
                                cal.set(year, monthOfYear, dayOfMonth);
                                if (cal.getTimeInMillis() <= startDateSelected.getTimeInMillis() || cal.getTimeInMillis() <= GregorianCalendar.getInstance().getTimeInMillis())
                                {
                                    endEdit.setError("End Date Cannot be on or  before Start Date");
                                    endEdit.setText("Optional End Date");
                                    if (cal.getTimeInMillis() <= startDateSelected.getTimeInMillis()) {
                                        Toast.makeText(getContext(), "End Date Cannot be on or before Start Date", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                        Toast.makeText(getContext(), "End Date Cannot be before current Date", Toast.LENGTH_SHORT).show();

                                    error = true;
                                }
                                else {

                                    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                                    String date = formatter.format(cal.getTime());
                                    endEdit.setError(null);
                                    endEdit.setText(date);
                                    endDateSelected = cal;
                                    WorkingCost.setEndDate(cal);
                                    error = false;
                                    EventBus.getDefault().post(new CostEvent(WorkingCost));
                                }
                            }
                        },
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                endDPD.show(getActivity().getFragmentManager(), "Pick Start Date");
            }
        });

        boolean wrapInScrollView = true;
        final MaterialDialog.Builder builder = new MaterialDialog.Builder(this.getContext())
                .title("Create New Category")
                .customView(R.layout.dialog_category, wrapInScrollView)
                .positiveText("Create")
                .negativeText("Cancel")
                .autoDismiss(false);


        ArrayList<String> strings = new ArrayList<String>();
        ArrayList<Integer> ints = new ArrayList<Integer>();

        for (int i = 1; i <= 10; i++)
            ints.add(i);


        Interval[] test = Interval.values();
        for (int i = 0; i < test.length; i++) {
            strings.add(test[i].toString() + "(s)");
        }

        intervalSpinner.setAdapter(new ArrayAdapter<String>(this.getContext(), R.layout.spinner_item, strings));
        numberSpinner.setAdapter(new ArrayAdapter<>(this.getContext(), R.layout.spinner_item, ints.toArray()));
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
        return inflater.inflate(R.layout.fragment_create_cost, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupData();
        setupUI();
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
