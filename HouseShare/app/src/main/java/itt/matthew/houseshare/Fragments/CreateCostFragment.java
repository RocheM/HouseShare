package itt.matthew.houseshare.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import itt.matthew.houseshare.Activities.NewCost;
import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.Models.CostCategory;
import itt.matthew.houseshare.Models.House;
import itt.matthew.houseshare.Models.Interval;
import itt.matthew.houseshare.R;

public class CreateCostFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";


    private House house;
    private Account account;

    private ArrayList<String> CategoryStrings = new ArrayList<String>();
    private Spinner intervalSpinner, numberSpinner;
    private Button startDateButton, endDateButton;
    private TextView startEdit, endEdit;
    private EditText amount;
    private MaterialDialog dialog;
    private char dateField;
    private int categorySelectedColor;
    private Calendar startDateSelected = new GregorianCalendar();
    private Calendar endDateSelected = new GregorianCalendar();

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


    private void buildColor() {
        ColorPickerDialogBuilder
                .with(this.getContext())
                .setTitle("Choose color")
                .initialColor(R.color.mdtp_red)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
                        categorySelectedColor = selectedColor;
                    }
                })
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        categorySelectedColor = selectedColor;
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }


    private void setupData() {
        Bundle extras = this.getActivity().getIntent().getBundleExtra("extra");
        account = extras.getParcelable("account");
        house = extras.getParcelable("house");
    }


    private void setupUI() {


        intervalSpinner = (Spinner) getView().findViewById(R.id.IntervalSpinnerUI);
        numberSpinner = (Spinner) getView().findViewById(R.id.numberSpinner);
        startDateButton = (Button) getView().findViewById(R.id.startDateButton);
        endDateButton = (Button) getView().findViewById(R.id.endDateButton);
        startEdit = (TextView) getView().findViewById(R.id.startDate);
        endEdit = (TextView) getView().findViewById(R.id.endDate);
        amount = (EditText) getView().findViewById(R.id.Amount);

        startDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dateField = 's';
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

                            }
                        },
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getActivity().getFragmentManager(), "Pick Start Date");
            }
        });

        endDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dateField = 'e';
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

                            }
                        },
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getActivity().getFragmentManager(), "Pick Start Date");
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
