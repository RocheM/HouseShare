package itt.matthew.houseshare.Activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import itt.matthew.houseshare.Fragments.FinanceFragment;
import itt.matthew.houseshare.Fragments.TasksFragment;
import itt.matthew.houseshare.Fragments.color_dialog;
import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.Models.House;
import itt.matthew.houseshare.R;

public class AccountActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener{

    private Account account;
    private House house;
    private ImageView backdrop;
    private ImageView profile;
    private TextView name;
    private String imageLocation;
    private TextView subtitle;
    private android.support.v7.widget.Toolbar toolbar;
    private AppBarLayout appbar;
    private int mMaxScrollSize;
    private static final int PERCENTAGE_TO_ANIMATE_AVATAR = 20;
    private boolean mIsAvatarShown = true;
    private Target loadtarget;
    private Window window;



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

        Bundle b = new Bundle();
        b.putParcelable("house", house);
        b.putParcelable("account", account);
        b.putBoolean("personal", true);
        getIntent().putExtra("extra", b);
    }

    private void setupUI() {


        backdrop = (ImageView) findViewById(R.id.profile_backdrop);
        profile = (ImageView) findViewById(R.id.profile_image);
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        name = (TextView) findViewById(R.id.account_name);
        subtitle = (TextView) findViewById(R.id.account_subtitle);
        appbar = (AppBarLayout) findViewById(R.id.appbar);

        window = this.getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        appbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

        name.setText(account.getName());
        subtitle.setText("Member of " + house.getName() + " house");

        Picasso.with(getApplicationContext()).load("https://graph.facebook.com/" + account.getFacebookID() + "/picture?type=large").into(profile);
        loadBitmap(account.getCoverPhotoURL());

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




        window = this.getWindow();

        Palette.from(b).generate(new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette p) {

                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);


                if (p.getDarkVibrantColor(getResources().getColor(R.color.colorPrimary)) == getResources().getColor(R.color.colorPrimary)) {

                    toolbar.setBackgroundColor(p.getMutedColor(getResources().getColor(R.color.colorPrimary)));
                    appbar.setBackgroundColor(p.getMutedColor(getResources().getColor(R.color.colorPrimary)));
                    window.setStatusBarColor(darker(p.getMutedColor(getResources().getColor(R.color.colorPrimary))));


                } else if (p.getMutedColor(getResources().getColor(R.color.colorPrimary)) == getResources().getColor(R.color.colorPrimary)) {
                    toolbar.setBackgroundColor(p.getDarkVibrantColor(getResources().getColor(R.color.colorPrimary)));
                    appbar.setBackgroundColor(p.getDarkVibrantColor(getResources().getColor(R.color.colorPrimary)));
                    window.setStatusBarColor(darker(p.getMutedColor(getResources().getColor(R.color.colorPrimary))));
                } else {
                    toolbar.setBackgroundColor(p.getVibrantColor(getResources().getColor(R.color.colorPrimary)));
                    appbar.setBackgroundColor(p.getVibrantColor(getResources().getColor(R.color.colorPrimary)));
                    window.setStatusBarColor(darker(p.getMutedColor(getResources().getColor(R.color.colorPrimary))));
                }


                Picasso.with(getApplicationContext()).load(account.getCoverPhotoURL()).into(backdrop);

            }
        });

    }


    public static int darker (int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = 1.0f - 0.8f * (1.0f - hsv[2]);
        color = Color.HSVToColor(hsv);
        return color;
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
                case 0: return FinanceFragment.newInstance("Test", "Test");
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
