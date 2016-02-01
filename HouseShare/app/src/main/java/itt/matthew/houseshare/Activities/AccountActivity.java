package itt.matthew.houseshare.Activities;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import itt.matthew.houseshare.Fragments.FinanceFragment;
import itt.matthew.houseshare.Fragments.TasksFragment;
import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.Models.House;
import itt.matthew.houseshare.R;

public class AccountActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    private Account account;
    private House house;
    private ImageView backdrop;
    private ImageView profile;
    private TextView name;
    private String imageLocation;
    private TextView subtitle;
    private android.support.v7.widget.Toolbar toolbar;
    private int mMaxScrollSize;
    private static final int PERCENTAGE_TO_ANIMATE_AVATAR = 20;
    private boolean mIsAvatarShown = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);


        setupData();
        setupUI();


        ViewPager viewPager  = (ViewPager) findViewById(R.id.viewpager);
        AppBarLayout appbarLayout = (AppBarLayout) findViewById(R.id.appbar);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);


        appbarLayout.addOnOffsetChangedListener(this);
        mMaxScrollSize = appbarLayout.getTotalScrollRange();

        viewPager.setAdapter(new TabsAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }


    private void setupData() {
        Bundle extras = this.getIntent().getBundleExtra("extra");
        account = extras.getParcelable("account");
        house = extras.getParcelable("house");
    }

    private void setupUI() {
        backdrop = (ImageView) findViewById(R.id.profile_backdrop);
        profile = (ImageView) findViewById(R.id.profile_image);
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        name = (TextView) findViewById(R.id.account_name);
        subtitle = (TextView) findViewById(R.id.account_subtitle);

        name.setText(account.getName());
        subtitle.setText("Member of " + house.getName() + " house");

       // getBackgroundPicture();
        Picasso.with(this).load("https://graph.facebook.com/" + account.getFacebookID() + "/picture?type=large").into(backdrop);
        Picasso.with(this).load("https://graph.facebook.com/" + account.getFacebookID() + "/picture?type=large").into(profile);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        if (mMaxScrollSize == 0)
            mMaxScrollSize = appBarLayout.getTotalScrollRange();

        int percentage = (Math.abs(i)) * 100 / mMaxScrollSize;

        if (percentage >= PERCENTAGE_TO_ANIMATE_AVATAR && mIsAvatarShown) {
            mIsAvatarShown = false;
            profile.animate().scaleY(0).scaleX(0).setDuration(200).start();
        }

        if (percentage <= PERCENTAGE_TO_ANIMATE_AVATAR && !mIsAvatarShown) {
            mIsAvatarShown = true;

            profile.animate()
                    .scaleY(1).scaleX(1)
                    .start();
        }
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
                case 0: return TasksFragment.newInstance("Test", "Test");
                case 1: return TasksFragment.newInstance("Test", "Test");
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch(position) {
                case 0: return "Finances";
                case 1: return "Tasks";
            }
            return "";
        }
    }


    private Void getBackgroundPicture(){

        new AsyncTask<Void, Void, Void>() {

            JSONObject JSONresponse;

            @Override
            protected Void doInBackground(Void... params) {


                GraphRequest request = GraphRequest.newMeRequest(
                        AccessToken.getCurrentAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            ;
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {

                                JSONresponse = object;

                                try {
                                   JSONObject obj =  object.getJSONObject("cover");
                                    imageLocation = obj.getString("source");
                                    populateImage();


                                }catch (Exception ex){
                                    ex.printStackTrace();
                                }
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "cover");
                request.setParameters(parameters);
                request.executeAsync();



                return null;
            }

        }.execute();
        return null;
    }

    private void populateImage(){

        Picasso.with(this).load(imageLocation).into(backdrop);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
