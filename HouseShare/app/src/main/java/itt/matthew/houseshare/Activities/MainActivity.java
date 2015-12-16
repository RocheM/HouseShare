package itt.matthew.houseshare.Activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.facebook.Profile;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.TableQueryCallback;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.R;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private MobileServiceClient mClient;
    private MobileServiceTable<Account> mAccountTable;
    private Account current;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout drawerLayout;
    private ListView navList;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupAzure();
        setContentView(R.layout.activity_main);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        navList = (ListView)findViewById(R.id.navList);
        ArrayList<String> navArray = new ArrayList<String>();
        navArray.add("My House");
        navArray.add("Finance");
        navArray.add("Tasks");
        navList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_activated_1,navArray);
        navList.setAdapter(adapter);
        navList.setOnItemClickListener(this);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,R.string.open_drawer, R.string.close_drawer);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        fragmentManager = getSupportFragmentManager();

        loadSelection(0);
    }

    private void loadSelection(int i){
        navList.setItemChecked(i,true);
        switch (i) {
            case 0:
                DetailsFragment detailsFragment = new DetailsFragment();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentHolder, detailsFragment);
                fragmentTransaction.commit();
                break;
            case 1:
                FinanceFragment financeFragment = new FinanceFragment();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentHolder, financeFragment);
                fragmentTransaction.commit();
                break;
            case 2:
                TasksFragment tasksFragment = new TasksFragment();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentHolder, tasksFragment);
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

    public void setupAzure(){


        try {
            mClient = new MobileServiceClient(
                    "https://houseshareproject.azure-mobile.net/",
                    "iuqOtKPRNqrMfasRrLARUYNrihSzwh94",
                    this
            );

        } catch (Exception e) {


        }

        mAccountTable = mClient.getTable(Account.class);
    }


    public void lookupAccount(final String facebookID) {


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

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }



    public void onFragmentInteraction(){


    }
}