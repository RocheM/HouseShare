package itt.matthew.houseshare.Activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.view.Window;
import android.view.WindowManager;
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
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.google.common.eventbus.EventBus;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.greenrobot.eventbus.Subscribe;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import de.hdodenhof.circleimageview.CircleImageView;
import itt.matthew.houseshare.Events.CostEvent;
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

public class NewCost extends AppCompatActivity implements ColorChooserDialog.ColorCallback {


    private MobileServiceClient mClient;
    private MobileServiceTable<Account> mAccountTable;
    private MobileServiceTable<House> mHouseTable;


    private Account current;
    private House house;
    private Cost newCost;
    private ArrayList<String> CategoryStrings = new ArrayList<String>();
    private ArrayAdapter<String> categoryAdapter;

    private Spinner category;
    private FloatingActionButton fab;
    private MaterialDialog dialog;

    private int categorySelectedColor = 0;
    private CircleImageView colorPreview;
    private TextView colorPreviewText;
    private Button colorButton;
    private EditText categoryName;




    public static final String SHAREDPREFFILE = "temp";
    public static final String USERIDPREF = "uid";
    public static final String TOKENPREF = "tkn";
    public static final String FBTOKENPREF = "fbt";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_cost);

        setupAzure();
        setupData();
        setupUI();
    }


    @Subscribe
    public void onCostEvent(CostEvent costEvent){
        newCost = costEvent.getCost();
    }


    public void setupAzure(){


        try {
            mClient = new MobileServiceClient(
                    "https://backendhs.azurewebsites.net",
                    this);

        } catch (Exception e) {


        }

        loadUserTokenCache(mClient);
        mAccountTable = mClient.getTable(Account.class);
        mHouseTable = mClient.getTable(House.class);
    }



    private boolean loadUserTokenCache(MobileServiceClient client)
    {
        SharedPreferences prefs = getSharedPreferences(SHAREDPREFFILE, Context.MODE_PRIVATE);
        String userId = prefs.getString(USERIDPREF, "undefined");
        if (userId == "undefined")
            return false;
        String token = prefs.getString(TOKENPREF, "undefined");
        if (token == "undefined")
            return false;

        MobileServiceUser user = new MobileServiceUser(userId);
        user.setAuthenticationToken(token);
        client.setCurrentUser(user);

        return true;
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.cost_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    private void buildColor() {

        showColorChooserPrimary();

    }



    private void changeBackgroundColor(int color) {
        LinearLayout background = (LinearLayout) findViewById(R.id.cost_title_container);
        Toolbar toolbar = (Toolbar) findViewById(R.id.cost_toolbar);
        TabLayout tabs = (TabLayout) findViewById(R.id.costs_tabs);
        background.setBackgroundColor(color);
        toolbar.setBackgroundColor(color);
        tabs.setBackgroundColor(color);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(darker(color));


    }

    public static int darker (int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = 1.0f - 0.8f * (1.0f - hsv[2]);
        color = Color.HSVToColor(hsv);
        return color;
    }

    private void setupData() {
        Bundle extras = this.getIntent().getBundleExtra("extra");
        current = extras.getParcelable("account");
        house = extras.getParcelable("house");

    }

    private void setupUI() {


        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.cost_toolbar);
        ViewPager viewPager  = (ViewPager) findViewById(R.id.cost_viewpager);
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.costs_tabs);

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

                CostCategory costCategory = house.getCostCategory().get(category.getSelectedItemPosition());
                changeBackgroundColor(house.getCostCategory().get(category.getSelectedItemPosition()).getColor());
                newCost.setCategory(costCategory);
                org.greenrobot.eventbus.EventBus.getDefault().post(new CostEvent(newCost));

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
                                ColorDrawable  drawable = new ColorDrawable(getResources().getColor(R.color.white));
                                colorPreview.setImageDrawable(drawable);
                                colorPreviewText.setText("Selected Color");



                                String name = categoryName.getText().toString();
                                if (name.length() <= 0) {
                                    categoryName.setError("Must enter a name");
                                    error = true;
                                } else
                                    error = false;

                                if (categorySelectedColor == 0){
                                    error = true;
                                    colorButton.setError("Must Select A Color");
                                    Toast.makeText(getApplicationContext(), "Must Select A Color", Toast.LENGTH_SHORT).show();
                                }


                                if (!error) {
                                    colorButton.setError(null);
                                    categoryName.setText("");
                                    categoryName.setError(null);
                                    drawable = new ColorDrawable(getResources().getColor(R.color.white));
                                    colorPreview.setImageDrawable(drawable);

                                    colorPreviewText.setText("");


                                    CostCategory newCategory = new CostCategory(name, categorySelectedColor);
                                    categorySelectedColor = 0;
                                    ArrayList<CostCategory> cat = house.getCostCategory();
                                    cat.add(newCategory);
                                    house.setCostCategories(cat);
                                    UpdateUI();
                                    newCost.setCategory(newCategory);
                                    org.greenrobot.eventbus.EventBus.getDefault().post(new CostEvent(newCost));
                                    dialog.dismiss();
                                }

                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();

                                colorButton.setError(null);
                                categoryName.setText("");
                                categoryName.setError(null);
                                ColorDrawable drawable = new ColorDrawable(getResources().getColor(R.color.white));
                                colorPreview.setImageDrawable(drawable);
                                categorySelectedColor = 0;
                                colorPreviewText.setText("");


                            }
                        }).build();
                dialog.show();
                View dialogView = dialog.getCustomView();


                colorButton = (Button) dialogView.findViewById(R.id.colorButton);
                colorButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        buildColor();
                    }
                });

                colorPreview = (CircleImageView) dialogView.findViewById(R.id.color_preview);
                colorPreviewText = (TextView) dialogView.findViewById(R.id.dialog_color_preview);
                categoryName = (EditText) dialogView.findViewById(R.id.categoryNameInput);

                ColorDrawable  drawable = new ColorDrawable(getResources().getColor(R.color.white));
                colorPreview.setImageDrawable(drawable);
                colorPreviewText.setText("Selected Color");



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
        category.setSelection(CategoryStrings.size() - 1);
        changeBackgroundColor(categorySelectedColor);

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
        org.greenrobot.eventbus.EventBus.getDefault().register(this);

    }

    @Override
    public void onStop() {
        super.onStop();
        org.greenrobot.eventbus.EventBus.getDefault().unregister(this);

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
                case 0:return CreateCostFragment.newInstance("Test", "Test");

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

        ArrayList<CostSplit> costSplits = new ArrayList<>();

        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        String Content = newCost.getCategory().getName() + "\n" + newCost.getAmount() + "\n" + newCost.getInterval() + "\n" + formatter.format(newCost.getStartDate().getTime()) + " to " + formatter.format(newCost.getEndDate().getTime());
        for (int i = 0; i < newCost.getSplit().size(); i++) {
            Content = Content.concat("\n" + newCost.getSplit().get(i).getName() + " pays " + newCost.getSplit().get(i).getAmount());
        }


        new MaterialDialog.Builder(this)
                .title("Confirm")
                .content(Content)
                .positiveText("Confirm")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        ArrayList<Cost> costs = house.getCost();
                        costs.add(newCost);
                        house.setCosts(costs);
                        updateItem(house);
                    }
                })
                .negativeText("Cancel")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();

    }



    public void showColorChooserPrimary() {

        new ColorChooserDialog.Builder(this, R.string.color_palette)
                .titleSub(R.string.color_palette)  // title of dialog when viewing shades of a color
                .doneButton(R.string.md_done_label)  // changes label of the done button
                .cancelButton(R.string.md_cancel_label)  // changes label of the cancel button
                .backButton(R.string.md_back_label)  // changes label of the back button
                .dynamicButtonColor(false)  // defaults to true, false will disable changing action buttons' color to currently selected color
                .allowUserColorInput(false)
                .show();

    }




    @Override
    public void onColorSelection(ColorChooserDialog dialog, int selectedColor) {

        categorySelectedColor = selectedColor;
        ColorDrawable drawable = new ColorDrawable();
        drawable.setColor(selectedColor);


        colorPreview.setImageDrawable(drawable);

    }


}

