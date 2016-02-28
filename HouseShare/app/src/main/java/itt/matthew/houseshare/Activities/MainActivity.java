package itt.matthew.houseshare.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.TableQueryCallback;
import com.mikepenz.crossfadedrawerlayout.view.CrossfadeDrawerLayout;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.MiniDrawer;
import com.mikepenz.materialdrawer.interfaces.ICrossfader;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;
import com.mikepenz.materialize.util.UIUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.net.URL;
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
   // private ActionBarDrawerToggle actionBarDrawerToggle;
  //  private DrawerLayout drawerLayout;
   // private ListView navList;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;



    public static final String SHAREDPREFFILE = "temp";
    public static final String USERIDPREF = "uid";
    public static final String TOKENPREF = "tkn";
    public static final String FBTOKENPREF = "fbt";
    private CrossfadeDrawerLayout crossfadeDrawerLayout;
    private AccountHeader headerResult;
    private Drawer result;
    private Window window;
    private Target loadtarget;
    private Bitmap bc;
    private URL url;
    private ProfileDrawerItem profileDrawerItem;
    private DrawerBuilder builder;
    private Drawer drawer;
    private android.support.v7.widget.Toolbar toolbar;


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
        result.deselect();
        result.setSelectionAtPosition(event.index, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupData();
        setupAzure();
        setContentView(R.layout.activity_main);
//        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
//
//        navList = (ListView)findViewById(R.id.navList);
//        ArrayList<String> navArray = new ArrayList<>();
//        navArray.add("My House");
//        navArray.add("Finance");
//        navArray.add("Tasks");
//        navList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_activated_1,navArray);
//        navList.setAdapter(adapter);
//        navList.setOnItemClickListener(this);


        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_main);
        this.setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        loadBitmap("https://graph.facebook.com/" + current.getFacebookID() + "/picture?type=large");



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

        profileDrawerItem = new ProfileDrawerItem().withIcon(b).withName(current.getName()).withEmail(current.getEmail());
        bc = b.copy(Bitmap.Config.ARGB_8888, true);



        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withOnlyMainProfileImageVisible(true)
                .addProfiles(
                        profileDrawerItem,
                        new ProfileDrawerItem().withName("Logout").withIcon(FontAwesome.Icon.faw_sign_out)
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        if (profile.getName().getText().equals("Logout")) {
                            logout();
                            headerResult.setActiveProfile(headerResult.getProfiles().get(0));
                        }

                        return false;
                    }
                })
                .withCurrentProfileHiddenInList(true)
                .build();


//create the drawer and remember the `Drawer` result object
        result = new DrawerBuilder()
                .withAccountHeader(headerResult)
                .withActivity(this)
                .withToolbar(toolbar)
                .withDrawerLayout(R.layout.crossfade_drawer)
                .withHasStableIds(true)
                .withGenerateMiniDrawer(true)
                .withDrawerWidthDp(72)
                .withCloseOnClick(true)
                .withShowDrawerOnFirstLaunch(true)
                .withActionBarDrawerToggleAnimated(true)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Home").withIcon(FontAwesome.Icon.faw_home),
                        new PrimaryDrawerItem().withName("Finance").withIcon(FontAwesome.Icon.faw_credit_card),
                        new PrimaryDrawerItem().withName("Tasks").withIcon(FontAwesome.Icon.faw_sticky_note_o)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        loadSelection(position - 1);
                        result.closeDrawer();
                        return true;
                    }
                })
                .build();



        //get the CrossfadeDrawerLayout which will be used as alternative DrawerLayout for the Drawer
        //the CrossfadeDrawerLayout library can be found here: https://github.com/mikepenz/CrossfadeDrawerLayout
        crossfadeDrawerLayout = (CrossfadeDrawerLayout) result.getDrawerLayout();

        //define maxDrawerWidth
        crossfadeDrawerLayout.setMaxWidthPx(DrawerUIUtils.getOptimalDrawerWidth(this));
        //add second view (which is the miniDrawer)
        final MiniDrawer miniResult = result.getMiniDrawer();
        //build the view for the MiniDrawer
        View view = miniResult.build(this);
        //set the background of the MiniDrawer as this would be transparent
        view.setBackgroundColor(UIUtils.getThemeColorFromAttrOrRes(this, com.mikepenz.materialdrawer.R.attr.material_drawer_background, com.mikepenz.materialdrawer.R.color.material_drawer_background));
        //we do not have the MiniDrawer view during CrossfadeDrawerLayout creation so we will add it here
        crossfadeDrawerLayout.getSmallView().addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        //define the crossfader to be used with the miniDrawer. This is required to be able to automatically toggle open / close
        miniResult.withCrossFader(new ICrossfader() {
            @Override
            public void crossfade() {
                boolean isFaded = isCrossfaded();
                crossfadeDrawerLayout.crossfade(400);

                //only close the drawer if we were already faded and want to close it now
                if (isFaded) {
                    result.getDrawerLayout().closeDrawer(GravityCompat.START);
                }
            }

            @Override
            public boolean isCrossfaded() {
                return crossfadeDrawerLayout.isCrossfaded();
            }
        });



        Palette.from(b).generate(new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette p) {


                if (p.getDarkVibrantColor(getResources().getColor(R.color.colorPrimary)) == getResources().getColor(R.color.colorPrimary)) {

                    bc.eraseColor(p.getDarkVibrantColor(getResources().getColor(R.color.colorPrimary)));
                    ImageView view = headerResult.getHeaderBackgroundView();
                    view.setImageBitmap(bc);

                } else if (p.getMutedColor(getResources().getColor(R.color.colorPrimary)) == getResources().getColor(R.color.colorPrimary)) {

                    bc.eraseColor(p.getMutedColor(getResources().getColor(R.color.colorPrimary)));
                    ImageView view = headerResult.getHeaderBackgroundView();
                    view.setImageBitmap(bc);

                } else {
                    bc.eraseColor(p.getVibrantColor(getResources().getColor(R.color.colorPrimary)));
                    ImageView view = headerResult.getHeaderBackgroundView();
                    view.setImageBitmap(bc);
                }



            }
        });

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
}