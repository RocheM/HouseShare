package itt.matthew.houseshare.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.TableQueryCallback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import itt.matthew.houseshare.Adapters_CustomViews.DrawerAdapter;
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
   // private ActionBarDrawerToggle actionBarDrawerToggle;
  //  private DrawerLayout drawerLayout;
   // private ListView navList;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;



    public static final String SHAREDPREFFILE = "temp";
    public static final String USERIDPREF = "uid";
    public static final String TOKENPREF = "tkn";
    public static final String FBTOKENPREF = "fbt";

    private Window window;
    private Target loadtarget;

    private Toolbar toolbar;                              // Declaring the Toolbar Object

    RecyclerView mRecyclerView;                           // Declaring RecyclerView
    RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View
    RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
    DrawerLayout Drawer;                                  // Declaring DrawerLayout

    ActionBarDrawerToggle mDrawerToggle;                  // Declaring Action Bar Drawer Toggle

    private float x1,x2;
    static final int MIN_DISTANCE = 150;

    String TITLES[] = {"Home","Finance", "Tasks"};
    int ICONS[] = {R.drawable.ic_home_24dp, R.drawable.ic_local_atm_24dp,R.drawable.ic_insert_invitation_24dp};
    private  OnItemTouchListener listener;

    //Similarly we Create a String Resource for the name and email in the header view
    //And we also create a int resource for profile picture in the header view


    @Override
    protected void onStart() {
        super.onStart();
        org.greenrobot.eventbus.EventBus.getDefault().register(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                float deltaX = x2 - x1;

                if (Math.abs(deltaX) > MIN_DISTANCE)
                {
                    // Left to Right swipe action
                    if (x2 > x1)
                    {
                        Toast.makeText(this, "Left to Right swipe [Next]", Toast.LENGTH_SHORT).show ();
                    }

                    // Right to left swipe action
                    else
                    {
                        Toast.makeText(this, "Right to Left swipe [Previous]", Toast.LENGTH_SHORT).show ();
                    }

                }
                else
                {
                    // consider as something else - a screen tap for example
                }
                break;
        }
        return super.onTouchEvent(event);
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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupData();
        setupAzure();
        setContentView(R.layout.activity_main);


        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_main);
        this.setSupportActionBar(toolbar);


        window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));



        listener = new OnItemTouchListener() {
            @Override
            public void onItemViewTouch(View view, int position) {
                loadSelection(position - 1);

            }
        };

        mAdapter = new DrawerAdapter(TITLES,ICONS,current,this, listener);
        mRecyclerView = (RecyclerView) findViewById(R.id.nav_rv); // Assigning the RecyclerView Object to the xml View

        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setAdapter(mAdapter);                              // Setting the adapter to RecyclerView

        mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager

        mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager


        Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);        // Drawer object Assigned to the view
        mDrawerToggle = new ActionBarDrawerToggle(this,Drawer,toolbar,R.string.open_drawer,R.string.close_drawer){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // code here will execute once the drawer is opened( As I dont want anything happened whe drawer is
                // open I am not going to put anything here)
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Code here will execute once drawer is closed
            }



        }; // Drawer Toggle Object Made
        Drawer.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
        mDrawerToggle.syncState();               // Finally we set the drawer toggle sync State



        fragmentManager = getSupportFragmentManager();

        setupAccount();
        loadSelection(0);
    }


    private void logout(){
        Toast.makeText(this, "Logged out", Toast.LENGTH_LONG).show();
    }

    public void loadBitmap(String url) {

        if (loadtarget == null) loadtarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                // do something with the Bitmap
                handleLoadedBitmap(bitmap);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {


            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }
        };
        Picasso.with(this).load(url).into(loadtarget);
    }

    public void handleLoadedBitmap(Bitmap b) {


    }


    private void setupData(){

        Bundle b = getIntent().getBundleExtra("Bundle");
        current = b.getParcelable("Account");
        house = b.getParcelable("House");

    }

    private void setupAccount(){
        lookupAccount(current.getFacebookID());
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
//        result.setSelectionAtPosition(i);
        Drawer.closeDrawers();
        switch (i) {

            case 0:
                DetailsFragment detailsFragment = new DetailsFragment();
                setTitle("My House Mates");
                fragmentTransaction = fragmentManager.beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putParcelable("house", house);
                bundle.putParcelable("account", current);
                getIntent().putExtra("extra", bundle);
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
   //     actionBarDrawerToggle.syncState();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        int id = item.getItemId();
//
//        if (id == android.R.id.home){
//            if (drawerLayout.isDrawerOpen(navList)){
//                drawerLayout.closeDrawer(navList);
//            }else
//                drawerLayout.openDrawer(navList);
//        }

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
  //      drawerLayout.closeDrawer(navList);

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

    public interface OnItemTouchListener {
        public void onItemViewTouch(View view, int position);

    }
}