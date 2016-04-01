package itt.matthew.houseshare.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import itt.matthew.houseshare.Adapters_CustomViews.DateGridAdapter;
import itt.matthew.houseshare.Adapters_CustomViews.RVAccountAdapter;
import itt.matthew.houseshare.Adapters_CustomViews.RVAdapter;
import itt.matthew.houseshare.Events.OverviewEvent;
import itt.matthew.houseshare.Fragments.CostOverviewFragment;
import itt.matthew.houseshare.Fragments.CostSplitFragment;
import itt.matthew.houseshare.Fragments.DetailsFragment;
import itt.matthew.houseshare.Fragments.FinanceFragment;
import itt.matthew.houseshare.Fragments.PersonalCostOverview;
import itt.matthew.houseshare.Fragments.TasksFragment;
import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.Models.Cost;
import itt.matthew.houseshare.Models.CostSplit;
import itt.matthew.houseshare.Models.House;
import itt.matthew.houseshare.R;
import com.paypal.android.MEP.CheckoutButton;
import com.paypal.android.MEP.PayPal;
import com.paypal.android.MEP.PayPalActivity;
import com.paypal.android.MEP.PayPalInvoiceData;
import com.paypal.android.MEP.PayPalInvoiceItem;
import com.paypal.android.MEP.PayPalPayment;

import org.greenrobot.eventbus.Subscribe;
import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.util.ArrayList;

public class CostDetails extends AppCompatActivity {

    private House house;
    private Account current;
    private Cost cost;
    private CostSplit curr;
    private int selectedUser = -1;


    private FragmentTransaction fragmentTransaction;
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

    private void setupUI(){

//        RecyclerView rv = (RecyclerView) findViewById(R.id.cost_detail_rv);
//        rv.setLayoutManager(new GridLayoutManager(this, 2));
//        rv.setAdapter(new DateGridAdapter(house, current, cost, getApplicationContext()));


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
                setTitle("Cost Overview");
                fragmentTransaction = fragmentManager.beginTransaction();
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
                PersonalCostOverview personalCostOverview= new PersonalCostOverview();
                setTitle("Cost Overview");
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
