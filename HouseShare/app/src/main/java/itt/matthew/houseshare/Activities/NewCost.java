package itt.matthew.houseshare.Activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.Models.Cost;
import itt.matthew.houseshare.Models.CostCategory;
import itt.matthew.houseshare.Models.House;
import itt.matthew.houseshare.Models.Interval;
import itt.matthew.houseshare.R;

public class NewCost extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {


    private Account current;
    private House house;


    private ArrayList<String> CategoryStrings = new ArrayList<String>();
    private ArrayAdapter<String> categoryAdapter;


    private MobileServiceClient mClient;
    private MobileServiceTable<Account> mAccountTable;
    private MobileServiceTable<House> mHouseTable;

    private Spinner intervalSpinner, numberSpinner, category;
    private Button startDateButton, endDateButton;
    private TextView startEdit, endEdit;
    private EditText amount;
    private FloatingActionButton fab;
    private MaterialDialog dialog;
    private char dateField;
    private int categorySelectedColor;
    private Calendar startDateSelected = new GregorianCalendar();
    private Calendar endDateSelected = new GregorianCalendar();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_cost);

        setupAzure();
        setupData();
        setupUI();
    }


    public void setupAzure() {


        try {
            mClient = new MobileServiceClient(
                    "https://backendhs.azurewebsites.net",
                    this
            );

        } catch (Exception e) {
            new MaterialDialog.Builder(this)
                    .title("Error")
                    .content(e.getMessage())
                    .positiveText("Ok")
                    .show();

        }

        mAccountTable = mClient.getTable(Account.class);
        mHouseTable = mClient.getTable(House.class);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.cost_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    private int setUpInterval(int index) {

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

        return 0;
    }


    private void buildColor() {
        ColorPickerDialogBuilder
                .with(this)
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

    private void changeBackgroundColor(int color) {
        CollapsingToolbarLayout background = (CollapsingToolbarLayout) findViewById(R.id.cost_collapsing);
        background.setBackgroundColor(color);
    }


    private void setupData() {
        Bundle extras = this.getIntent().getBundleExtra("extra");
        current = extras.getParcelable("account");
        house = extras.getParcelable("house");
    }


    private void setupUI() {


        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.cost_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        intervalSpinner = (Spinner) findViewById(R.id.IntervalSpinnerUI);
        numberSpinner = (Spinner) findViewById(R.id.numberSpinner);
        startDateButton = (Button) findViewById(R.id.startDateButton);
        endDateButton = (Button) findViewById(R.id.endDateButton);
        startEdit = (TextView) findViewById(R.id.startDate);
        endEdit = (TextView) findViewById(R.id.endDate);
        category = (Spinner) findViewById(R.id.CategorySpinner);
        amount = (EditText) findViewById(R.id.Amount);
        fab = (FloatingActionButton) findViewById(R.id.cost_fab);

        startDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dateField = 's';
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        NewCost.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Pick Start Date");
            }
        });

        endDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dateField = 'e';
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        NewCost.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Pick Start Date");
            }
        });

        boolean wrapInScrollView = true;
        final MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
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

        intervalSpinner.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_item, strings));


        for (int i = 0; i < house.getCostCategory().size(); i++) {
            CategoryStrings.add(i, house.getCostCategory().get(i).getName());
        }


        numberSpinner.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_item, ints.toArray()));

        categoryAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_title, CategoryStrings);
        category.setAdapter(categoryAdapter);
        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                changeBackgroundColor(house.getCostCategory().get(category.getSelectedItemPosition()).getColor());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog = builder
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                View dialogView = dialog.getView();
                                boolean error = false;
                                EditText categoryName = (EditText) dialogView.findViewById(R.id.categoryNameInput);


                                String name = categoryName.getText().toString();
                                if (name.length() <= 0) {
                                    categoryName.setError("Must enter a name");
                                    error = true;
                                } else
                                    error = false;


                                if (!error) {
                                    categoryName.setText("");
                                    CostCategory newCategory = new CostCategory(name, categorySelectedColor);
                                    ArrayList<CostCategory> cat = house.getCostCategory();
                                    cat.add(newCategory);
                                    house.setCostCategories(cat);
                                    UpdateUI();
                                    dialog.dismiss();
                                }

                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        }).build();
                dialog.show();
                View dialogView = dialog.getCustomView();


                Button colorButton = (Button) dialogView.findViewById(R.id.colorButton);
                colorButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        buildColor();
                    }
                });

                ArrayList<String> strings = new ArrayList<String>();
                ArrayList<Integer> ints = new ArrayList<Integer>();
                for (int i = 1; i <= 10; i++)
                    ints.add(i);


                Interval[] test = Interval.values();
                for (int i = 0; i < test.length; i++) {
                    strings.add(test[i].toString() + "(s)");
                }


            }
        });

    }

    private void UpdateUI() {


        final ArrayList<CostCategory> categoryTest = house.getCostCategory();
        CategoryStrings.clear();
        for (int i = 0; i < categoryTest.size(); i++) {
            CategoryStrings.add(i, categoryTest.get(i).getName());
        }


        categoryAdapter.notifyDataSetChanged();


    }

    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            CreateNewCost();
        }

        return super.onOptionsItemSelected(item);
    }

    private void CreateNewCost() {

        CostCategory newCostCategory = house.getCostCategory().get(category.getSelectedItemPosition());

        Double newCostAmount = 0.0;
        newCostAmount = Double.parseDouble(amount.getText().toString());


        Calendar newCostStartDate = startDateSelected;

        newCostStartDate = startDateSelected;

        Calendar newCostEndDate = endDateSelected;
        newCostEndDate = endDateSelected;

        int newCostDays = (Integer)numberSpinner.getSelectedItem() * setUpInterval(intervalSpinner.getSelectedItemPosition());


        Cost newCost = new Cost(newCostDays, newCostCategory, newCostAmount, newCostStartDate, newCostEndDate);
        ArrayList<Cost> toSet = house.getCost();
        toSet.add(newCost);
        house.setCosts(toSet);
        updateItem(house);
    }

    private void updateItem(final House item) {
        if (mClient == null) {
            return;
        }

        final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Updating");
        progress.setMessage("Updating House...");
        progress.show();


        final MaterialDialog error = new MaterialDialog.Builder(getApplicationContext()).build();

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    mHouseTable.update(item).get();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progress.hide();
                            finish();
                        }
                    });
                } catch (Exception exception) {
                    progress.hide();
                }
                return null;
            }
        }.execute();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        if (dateField == 's') {
            startEdit.setText(Integer.toString(year) + "/" + Integer.toString(monthOfYear + 1) + "/" + Integer.toString(dayOfMonth));
            startDateSelected.set(year, monthOfYear, dayOfMonth);
        } else {
            endEdit.setText(Integer.toString(year) + "/" + Integer.toString(monthOfYear + 1) + "/" + Integer.toString(dayOfMonth));
            startDateSelected.set(year, monthOfYear, dayOfMonth);
        }
    }
}

