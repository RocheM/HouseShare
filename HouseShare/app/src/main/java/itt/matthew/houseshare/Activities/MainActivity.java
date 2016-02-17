package itt.matthew.houseshare.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.TableQueryCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import itt.matthew.houseshare.Events.RequestDetailsEvent;
import itt.matthew.houseshare.Events.UpdateAccountEvent;
import itt.matthew.houseshare.Fragments.DetailsFragment;
import itt.matthew.houseshare.Fragments.FinanceFragment;
import itt.matthew.houseshare.Fragments.TasksFragment;
import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.Events.AccountEvent;
import itt.matthew.houseshare.Models.House;
import itt.matthew.houseshare.Events.MessageEvent;
import itt.matthew.houseshare.Events.ReplyEvent;
import itt.matthew.houseshare.R;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private MobileServiceClient mClient;
    private MobileServiceTable<Account> mAccountTable;
    private MobileServiceTable<House> mHouseTable;
    private Account current;
    private House house;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout drawerLayout;
    private ListView navList;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;


    @Override
    protected void onStart() {
        super.onStart();
        org.greenrobot.eventbus.EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        org.greenrobot.eventbus.EventBus.getDefault().unregister(this);
    }

    public void onResume(){
        super.onResume();
        lookupHouse();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_settings, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Subscribe
    public void onUpdateAccountEvent(UpdateAccountEvent event){
        current = event.getAccount();
        house = event.getHouse();
    }

    @Subscribe
    public void onEvent(AccountEvent event){
        Bundle bundle = new Bundle();
        bundle.putParcelable("account", event.account);
        bundle.putParcelable("house", event.house);

        startAccountActivity(bundle);

    }



    @Subscribe
    public void onEvent(RequestDetailsEvent event){
        if (event.getRequestFlag() == 'h') {

            Bundle bundle = new Bundle();
            bundle.putParcelable("account", current);
            bundle.putParcelable("house", house);

            startCostActivity(bundle);
        }
        else if (event.getRequestFlag() == 'f')
            org.greenrobot.eventbus.EventBus.getDefault().post(new ReplyEvent(house, current));

    }

    public void onEvent(MessageEvent event){
        setTitle(event.message);
        navList.setItemChecked(event.index, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (FacebookSdk.isInitialized() == false){
            FacebookSdk.sdkInitialize(this);
        }

        setupData();
        setupAzure();
        setContentView(R.layout.activity_main);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        navList = (ListView)findViewById(R.id.navList);
        ArrayList<String> navArray = new ArrayList<>();
        navArray.add("My House");
        navArray.add("Finance");
        navArray.add("Tasks");
        navList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_activated_1,navArray);
        navList.setAdapter(adapter);
        navList.setOnItemClickListener(this);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,R.string.open_drawer, R.string.close_drawer);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBarDrawerToggle.syncState();

        fragmentManager = getSupportFragmentManager();

        setupAccount();
        loadSelection(0);
    }

    private void setupData(){

        Bundle b = getIntent().getBundleExtra("Bundle");
        current = b.getParcelable("Account");
        house = b.getParcelable("House");

    }

    private void setupAccount(){
        lookupAccount(Profile.getCurrentProfile().getId());
    }

    private void startAccountActivity(Bundle b){

        Intent i = new Intent(this, AccountActivity.class);
        i.putExtra("extra", b);
        startActivity(i);
    }


    private void startCostActivity(Bundle b){

        Intent i = new Intent(this, NewCost.class);
        i.putExtra("extra", b);
        startActivity(i);
    }


    private void loadSelection(int i){
        navList.setItemChecked(i, true);
        switch (i) {

            case 0:
                DetailsFragment detailsFragment = new DetailsFragment();
                setTitle("My House Mates");
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentHolder, detailsFragment);
                fragmentTransaction.addToBackStack(detailsFragment.getTag());
                fragmentTransaction.commit();
                break;
            case 1:

                FinanceFragment financeFragment = new FinanceFragment();
                setTitle("House Finances");
                fragmentTransaction = fragmentManager.beginTransaction();
                Bundle b = new Bundle();
                b.putParcelable("house", house);
                b.putParcelable("account", current);
                getIntent().putExtra("extra", b);
                fragmentTransaction.replace(R.id.fragmentHolder, financeFragment);
                fragmentTransaction.addToBackStack(financeFragment.getTag());
                fragmentTransaction.commit();
                break;
            case 2:
                TasksFragment tasksFragment = new TasksFragment();
                setTitle("House Tasks");
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentHolder, tasksFragment);
                fragmentTransaction.addToBackStack(tasksFragment.getTag());
                fragmentTransaction.commit();
                break;
        }
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        actionBarDrawerToggle.syncState();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        int id = item.getItemId();

        if (id == android.R.id.home){
            if (drawerLayout.isDrawerOpen(navList)){
                drawerLayout.closeDrawer(navList);
            }else
                drawerLayout.openDrawer(navList);
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupAzure(){


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


    private void lookupAccount(final String facebookID) {


        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                        mAccountTable.where()
                                .field("facebookID")
                                .eq(facebookID)
                                .execute(new TableQueryCallback<Account>() {
                                             @Override
                                             public void onCompleted(List<Account> result, int count, Exception exception, ServiceFilterResponse response) {
                                                 if (exception == null) {
                                                     current = result.get(0);
                                                     lookupHouse();

                                                 } else
                                                     exception.printStackTrace();
                                             }
                                         }
                                );
                } catch (Exception exception) {
                    exception.printStackTrace();
                }


                return null;
            }

        }.execute();
    }

    private void lookupHouse(){

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    mHouseTable.where()
                            .field("HouseID")
                            .eq(current.getHouseID())
                            .execute(new TableQueryCallback<House>() {
                                         @Override
                                         public void onCompleted(List<House> result, int count, Exception exception, ServiceFilterResponse response) {
                                             if (exception == null) {
                                                 house = new House(result.get(0));
                                                 EventBus.getDefault().post(new UpdateAccountEvent(current, house));
                                             } else
                                                 exception.printStackTrace();
                                         }
                                     }
                            );
                } catch (Exception exception) {
                    exception.printStackTrace();
                }


                return null;
            }

        }.execute();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        loadSelection(position);
        drawerLayout.closeDrawer(navList);

    }

    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 1) {

            new AlertDialog.Builder(this)
                    .setTitle("Really Exit?")
                    .setMessage("Are you sure you want to exit?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            moveTaskToBack(true);
                        }
                    }).create().show();


        } else {
            getSupportFragmentManager().popBackStackImmediate();
        }
    }


    public void onFragmentInteraction(){


    }
}