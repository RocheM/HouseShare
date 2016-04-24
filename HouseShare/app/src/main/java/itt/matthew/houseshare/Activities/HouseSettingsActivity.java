package itt.matthew.houseshare.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.notifications.NotificationsManager;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.Subscribe;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.ArrayList;

import itt.matthew.houseshare.Events.AccountEvent;
import itt.matthew.houseshare.Events.MyHandler;
import itt.matthew.houseshare.Fragments.ArchivedCostFragment;
import itt.matthew.houseshare.Fragments.ArchivedTaskFragment;
import itt.matthew.houseshare.Fragments.DetailsFragment;
import itt.matthew.houseshare.Fragments.FinanceFragment;
import itt.matthew.houseshare.Fragments.color_dialog;
import itt.matthew.houseshare.Fragments.membersFragment;
import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.Models.House;
import itt.matthew.houseshare.R;

public class HouseSettingsActivity extends AppCompatActivity {

    private Account current;
    private House house;


    public static final String SHAREDPREFFILE = "temp";
    public static final String USERIDPREF = "uid";
    public static final String TOKENPREF = "tkn";
    public static final String FBTOKENPREF = "fbt";
    public static final String SENDER_ID = "18540511502";
    private String founder, operators;
    private MobileServiceClient mClient;
    private MobileServiceTable<Account> mAccountTable;
    private MobileServiceTable<House> mHouseTable;
    private TextView id;
    private Boolean nameChanged, descriptionChanged;
    private EditText description, houseName;
    private Toolbar toolbar;
    private Window window;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_settings);

        setupData();
        setupAzure();
        setupUI();
    }


    private void setupData(){


        Bundle b = getIntent().getBundleExtra("extra");
        house = b.getParcelable("house");
        current = b.getParcelable("account");
        this.setTitle("House Settings");

        operators = "";
        founder = "";
        for (int i = 0; i < house.getMembers().size(); i++){
            for (int j = 0; j < house.getOperators().size(); j++){
                if(house.getMembers().get(i).getFacebookID().equals(house.getOperators().get(j))){
                    operators = operators.concat(house.getMembers().get(i).getName());
                }
            }

            if (house.getFounder().equals(house.getMembers().get(i).getFacebookID())){
                founder = house.getMembers().get(i).getName();
            }

        }

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


    private void setupAzure(){


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.house_settings_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {


            DateFormat formatter = DateFormat.getDateInstance(DateFormat.SHORT);
            String content = "";


           content =  content.concat("House ID: " + house.getHouseID() + "\nHouse Name: " + house.getName() + "\nHouse Description: " + house.getDescription() + "\nFounder: " + founder + "\nNumber of Members: " + house.getMembers().size() + "\nFounded On: " + formatter.format(house.getCreatedOn().getTime()) + "\nModerators: \n" + operators);


            new MaterialDialog.Builder(this)
                    .title("House Info")
                    .content(content)
                    .positiveText("Okay")
                    .show();
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupUI() {
        DateFormat formatter = DateFormat.getDateInstance(DateFormat.SHORT);

        toolbar = (Toolbar) findViewById(R.id.house_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_backspace_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        assert toolbar != null;
        toolbar.setTitle("House Settings");

        window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        ViewPager viewPager = (ViewPager) findViewById(R.id.house_viewpager);
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.house_tabs);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.settings_fab);
        fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_done_24dp));

        assert viewPager != null;
        viewPager.setAdapter(new TabsAdapter(getSupportFragmentManager()));
        assert tabLayout != null;
        tabLayout.setupWithViewPager(viewPager);

        nameChanged = false;
        descriptionChanged = false;

        houseName = (EditText) findViewById(R.id.house_name);
        assert houseName != null;
        houseName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                  fab.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        description = (EditText) findViewById(R.id.house_description);
        assert description != null;
        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fab.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        final TextInputLayout nameEditText = (TextInputLayout) findViewById(R.id.house_houseNameInput);
        assert nameEditText != null;
        nameEditText.setHint("Name: " + house.getName());
        nameEditText.setSelected(false);


        final TextInputLayout descriptionEditText = (TextInputLayout) findViewById(R.id.house_houseDescInput);
        assert descriptionEditText != null;
        descriptionEditText.setHint("Description: " + house.getDescription());
        descriptionEditText.setSelected(false);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Boolean error = false;

                String name = nameEditText.getEditText().getText().toString();
                String houseDescription = descriptionEditText.getEditText().getText().toString();

                if(!(house.getName().equals(name))) {
                    if (name.length() < 5) {
                        error = true;
                        nameEditText.setError("Name Must Be Greater Than 5 Characters");
                    }

                    if (name.length() > 50) {
                        error = true;
                        nameEditText.setError("Description Must Be Less Than 50 Characters");
                    }
                }

                if(!(house.getDescription().equals(houseDescription))) {
                    if (houseDescription.length() < 5) {
                        error = true;
                        descriptionEditText.setError("Description Must Be Greater Than 5 Characters");
                    }

                    if (houseDescription.length() > 100) {
                        error = true;
                        descriptionEditText.setError("Description Must Be Less Than 100 Characters");
                    }
                }


                if (!error) {
                    nameEditText.setError(null);
                    descriptionEditText.setError(null);
                    house.setName(nameEditText.getEditText().getText().toString());
                    house.setDescription(descriptionEditText.getEditText().getText().toString());

                    boolean wrapInScrollView = true;
                    new MaterialDialog.Builder(v.getContext())
                            .title("Confirm Change?")
                            .content("House Details:\n\nName:\t" + name + "\nDescription:\t" + houseDescription)
                            .positiveText("Confirm")
                            .negativeText("Cancel")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    updateItem(house);
                                }
                            })
                            .show();
                }
            }
        });
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
                            startMainActivity(current);
                        }
                    });
                } catch (Exception exception) {

                    exception.printStackTrace();
                }
                return null;
            }
        }.execute();
    }


    public void startMainActivity(Account acc) {

        Intent i = new Intent(this, MainActivity.class);
        Bundle b = new Bundle();
        b.putParcelable("Account", acc);
        i.putExtra("Bundle", b);
        startActivity(i);
        finish();


    }

    class TabsAdapter extends FragmentPagerAdapter {
        public TabsAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Fragment getItem(int i) {
            switch(i) {
                case 0:
                    Bundle b = getIntent().getExtras().getBundle("extra");
                    b.putBoolean("reorder", false);
                    return membersFragment.newInstance("Test", "Test");
                case 1:return ArchivedCostFragment.newInstance("Test", "Test");
                case 2: return ArchivedTaskFragment.newInstance("Test", "Test");
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch(position) {
                case 0: return "Members";
                case 1: return "Archived Costs";
                case 2: return "Archived Tasks";
            }
            return "";
        }
    }

}
