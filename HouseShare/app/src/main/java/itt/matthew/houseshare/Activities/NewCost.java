package itt.matthew.houseshare.Activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import itt.matthew.houseshare.Fragments.CostSplitFragment;
import itt.matthew.houseshare.Fragments.CreateCostFragment;
import itt.matthew.houseshare.Fragments.TasksFragment;
import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.Models.Cost;
import itt.matthew.houseshare.Models.CostCategory;
import itt.matthew.houseshare.Models.CostSplit;
import itt.matthew.houseshare.Models.House;
import itt.matthew.houseshare.Models.Interval;
import itt.matthew.houseshare.R;

public class NewCost extends AppCompatActivity {


    private MobileServiceClient mClient;
    private MobileServiceTable<Account> mAccountTable;
    private MobileServiceTable<House> mHouseTable;


    private Account current;
    private House house;
    private Cost newCost;
    private CostSplit newCostSplit;

    private ArrayList<String> CategoryStrings = new ArrayList<String>();
    private ArrayAdapter<String> categoryAdapter;

    private Spinner category;
    private FloatingActionButton fab;
    private MaterialDialog dialog;
    private int categorySelectedColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_cost);

        setupData();
        setupUI();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.cost_menu, menu);
        return super.onCreateOptionsMenu(menu);
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
        LinearLayout background = (LinearLayout) findViewById(R.id.cost_title_container);
        Toolbar toolbar = (Toolbar) findViewById(R.id.cost_toolbar);
        background.setBackgroundColor(color);
        toolbar.setBackgroundColor(color);
    }


    private void setupData() {
        Bundle extras = this.getIntent().getBundleExtra("extra");
        current = extras.getParcelable("account");
        house = extras.getParcelable("house");
    }


    private void setupUI() {


        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.cost_toolbar);
        ViewPager viewPager  = (ViewPager) findViewById(R.id.cost_viewpager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.costs_tabs);


        viewPager.setAdapter(new TabsAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);


        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_backspace_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);



        category = (Spinner) findViewById(R.id.CategorySpinner);
        fab = (FloatingActionButton) findViewById(R.id.cost_fab);

        fab.setImageResource(R.drawable.ic_add_24dp);


        boolean wrapInScrollView = true;
        final MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                .title("Create New Category")
                .customView(R.layout.dialog_category, wrapInScrollView)
                .positiveText("Create")
                .negativeText("Cancel")
                .autoDismiss(false);




        for (int i = 0; i < house.getCostCategory().size(); i++) {
            CategoryStrings.add(i, house.getCostCategory().get(i).getName());
        }

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
                            finish();
                        }
                    });
                } catch (Exception exception) {

                    exception.printStackTrace();
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

    class TabsAdapter extends FragmentPagerAdapter {
        public TabsAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int i) {
            switch(i) {
                case 0: return CreateCostFragment.newInstance("Test", "Test");
                case 1: return CostSplitFragment.newInstance("Test", "Test");
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch(position) {
                case 0: return "Cost Details";
                case 1: return "Cost Split";
            }
            return "";
        }
    }


    private void CreateNewCost(){

    }


}

