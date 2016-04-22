package itt.matthew.houseshare.Fragments;


import android.os.Bundle;
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
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import itt.matthew.houseshare.Events.CostEvent;
import itt.matthew.houseshare.Events.TaskEvent;
import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.Models.Cost;
import itt.matthew.houseshare.Models.CostSplit;
import itt.matthew.houseshare.Models.House;
import itt.matthew.houseshare.Models.Interval;
import itt.matthew.houseshare.Models.Task;
import itt.matthew.houseshare.Models.TaskArea;
import itt.matthew.houseshare.R;

public class CreateTaskFragment extends Fragment {



    private House house;
    private Account account;
    private Task WorkingTask = new Task();

    private Spinner intervalSpinner, numberSpinner;
    private int interval = 1, intervalNumber = 1;
    private Button startDateButton, endDateButton;
    private EditText amount;
    private MaterialDialog dialog;
    private char dateField;
    private int categorySelectedColor;
    private Calendar startDateSelected = new GregorianCalendar();
    private Calendar endDateSelected = new GregorianCalendar();
    private DatePickerDialog startDPD, endDPD;
    boolean error = false;


    public CreateTaskFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and chan types and number of parameters
    public static CreateTaskFragment newInstance(String param1, String param2) {
        CreateTaskFragment fragment = new CreateTaskFragment();


        return fragment;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupData();
        setupUI();
    }

    private void setupUI() {


        intervalSpinner = (Spinner) getView().findViewById(R.id.task_IntervalSpinnerUI);
        intervalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                interval = generateInterval(position);
                WorkingTask.setInterval(intervalNumber * interval);
                EventBus.getDefault().post(new TaskEvent(WorkingTask));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                interval = 1;
                WorkingTask.setInterval(1);
                EventBus.getDefault().post(new TaskEvent(WorkingTask));
            }
        });

        numberSpinner = (Spinner) getView().findViewById(R.id.task_numberSpinner);
        numberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                intervalNumber = position + 1;
                WorkingTask.setInterval(interval * intervalNumber);
                EventBus.getDefault().post(new TaskEvent(WorkingTask));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                intervalNumber = 1;
                WorkingTask.setInterval(interval);
                EventBus.getDefault().post(new TaskEvent(WorkingTask));
            }
        });



        startDateButton = (Button) getView().findViewById(R.id.task_startDateButton);
        endDateButton = (Button) getView().findViewById(R.id.task_endDateButton);


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
                                    startDateButton.setError(getString(R.string.StartDateCannotBeBeforeCurrentDate));
                                    startDateButton.setText(R.string.chooseStartDate);
                                    Toast.makeText(getContext(), getString(R.string.StartDateCannotBeBeforeCurrentDate), Toast.LENGTH_SHORT).show();
                                    error = true;
                                }
                                else {
                                    startDateButton.setError(null);
                                    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                                    String date = formatter.format(cal.getTime());
                                    startDateButton.setText(date);
                                    startDateSelected = cal;
                                    WorkingTask.setStartDate(startDateSelected);
                                    error = false;
                                    EventBus.getDefault().post(new TaskEvent(WorkingTask));
                                }
                            }
                        },
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                startDPD.show(getActivity().getFragmentManager(), getString(R.string.chooseStartDate));
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
                                    endDateButton.setError(getString(R.string.endDateCannotBeOnOrBeforeStartDate));
                                    endDateButton.setText(getString(R.string.chooseEndDate));
                                    if (cal.getTimeInMillis() <= startDateSelected.getTimeInMillis()) {
                                        Toast.makeText(getContext(), getString(R.string.endDateCannotBeBeofreCurrentDate), Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                        Toast.makeText(getContext(), R.string.endDateCannotBeBeofreCurrentDate, Toast.LENGTH_SHORT).show();

                                    error = true;
                                }
                                else {

                                    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                                    String date = formatter.format(cal.getTime());
                                    endDateButton.setError(null);
                                    endDateButton.setText(date);
                                    endDateSelected = cal;
                                    WorkingTask.setEndDate(cal);
                                    error = false;
                                    EventBus.getDefault().post(new TaskEvent(WorkingTask));
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

        final MaterialDialog.Builder builder = new MaterialDialog.Builder(this.getContext())
                .title(R.string.createNewArea)
                .customView(R.layout.dialog_category, true)
                .positiveText(R.string.create)
                .negativeText(R.string.cancel)
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_task, container, false);
    }

    @Subscribe
    public void onTaskEvent(TaskEvent taskEvent){
        WorkingTask = taskEvent.getTask() ;
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

    private void setupData() {
        Bundle extras = this.getActivity().getIntent().getBundleExtra("extra");
        account = extras.getParcelable("account");
        house = extras.getParcelable("house");


        if (house.getTask() != null) {
            WorkingTask.setTaskID(house.getCost().size() + 1);
            EventBus.getDefault().post(new TaskEvent(WorkingTask));
        }
        else {
            WorkingTask.setTaskID(0);
            EventBus.getDefault().post(new TaskEvent(WorkingTask));
        }
        WorkingTask.setInterval(1);
        EventBus.getDefault().post(new TaskEvent(WorkingTask));

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


}
