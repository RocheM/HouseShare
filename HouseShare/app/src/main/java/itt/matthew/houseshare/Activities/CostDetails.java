package itt.matthew.houseshare.Activities;

import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import itt.matthew.houseshare.Events.OverviewEvent;
import itt.matthew.houseshare.Fragments.CostOverviewFragment;
import itt.matthew.houseshare.Fragments.PersonalCostOverview;
import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.Models.Cost;
import itt.matthew.houseshare.Models.CostSplit;
import itt.matthew.houseshare.Models.House;
import itt.matthew.houseshare.R;

import org.greenrobot.eventbus.Subscribe;

public class CostDetails extends AppCompatActivity {

    private House house;
    private Account current;
    private Cost cost;
    private CostSplit curr;
    private int selectedUser = -1;
    private Toolbar toolbar;


    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cost_details);

        setupData();
        setupUI();

        fragmentManager = getSupportFragmentManager();

        loadSelection(0);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        onBackPressed();

        return super.onOptionsItemSelected(item);
    }

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


    @Subscribe
    public void onEvent(OverviewEvent event){

        selectedUser = event.getIndex();
        loadSelection(1);

    }

    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();


        if (count == 1) {
            finish();
        } else if (count == 2){
            toolbar.setTitle("Cost Overview");
            getSupportFragmentManager().popBackStackImmediate();
        }
        else {
            getSupportFragmentManager().popBackStackImmediate();
        }
    }


    private void setupUI(){


        AppBarLayout appbar = (AppBarLayout) findViewById(R.id.details_appbar);
        assert appbar != null;
        appbar.setBackgroundColor(cost.getCategory().getColor());


        toolbar = (Toolbar) findViewById(R.id.details_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_backspace_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setTitle(current.getName());

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(darker(cost.getCategory().getColor()));

        toolbar.setBackgroundColor(cost.getCategory().getColor());

    }


    public static int darker (int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = 1.0f - 0.8f * (1.0f - hsv[2]);
        color = Color.HSVToColor(hsv);
        return color;
    }


    private void setupData(){


        Bundle b = getIntent().getBundleExtra("extra");
        house = b.getParcelable("house");
        int costLocation = b.getInt("cost");
        current = b.getParcelable("account");
        cost = house.getCost().get(costLocation);

    }


    private void loadSelection(int i){
        switch (i) {
            case 0:
                CostOverviewFragment overviewFragment = new CostOverviewFragment();
                toolbar.setTitle("Cost Overview");
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putParcelable("house", house);
                bundle.putParcelable("account", current);
                bundle.putInt("cost", getIntent().getBundleExtra("extra").getInt("cost"));
                getIntent().putExtra("extra", bundle);
                fragmentTransaction.replace(R.id.detailsFragmentHolder, overviewFragment);
                fragmentTransaction.addToBackStack(overviewFragment.getTag());
                fragmentTransaction.commit();
                break;
            case 1:
                PersonalCostOverview personalCostOverview = new PersonalCostOverview();
                toolbar.setTitle(cost.getSplit().get(selectedUser).getName()+"'s payments");
                fragmentTransaction = fragmentManager.beginTransaction();
                bundle = new Bundle();
                bundle.putParcelable("house", house);
                bundle.putParcelable("account", current);
                bundle.putInt("cost", getIntent().getBundleExtra("extra").getInt("cost"));
                bundle.putInt("selected", selectedUser);
                getIntent().putExtra("extra", bundle);
                fragmentTransaction.replace(R.id.detailsFragmentHolder, personalCostOverview);
                fragmentTransaction.addToBackStack(personalCostOverview.getTag());
                fragmentTransaction.commit();
                break;
        }
    }
}
