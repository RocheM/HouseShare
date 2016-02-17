package itt.matthew.houseshare.Activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import itt.matthew.houseshare.Adapters_CustomViews.RVAccountAdapter;
import itt.matthew.houseshare.Adapters_CustomViews.RVAdapter;
import itt.matthew.houseshare.Fragments.CostSplitFragment;
import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.Models.Cost;
import itt.matthew.houseshare.Models.House;
import itt.matthew.houseshare.R;

public class CostDetails extends AppCompatActivity {

    private House house;
    private Cost cost;

    private TextView name;
    private RecyclerView owners;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cost_details);

        setupData();

        owners = (RecyclerView) findViewById(R.id.cost_detail_rv);
        owners.setLayoutManager(new LinearLayoutManager(this));

        CostSplitFragment.OnItemTouchListener itemTouchListener = new CostSplitFragment.OnItemTouchListener() {
            @Override
            public void onCardViewTouch(View view, int position) {

            }
        };

        owners.setAdapter(new RVAccountAdapter(house, cost, cost.getSplit(), itemTouchListener));
        owners.setClickable(true);

        setupUI();

    }


    private void setupUI(){

        name = (TextView) findViewById(R.id.cost_detail_name);
        name.setText(cost.getCategory().getName());

    }

    private void setupData(){

        Bundle b = getIntent().getBundleExtra("extra");
        house = b.getParcelable("house");
        int costLocation = b.getInt("cost");
        cost = house.getCost().get(costLocation);

    }



}
