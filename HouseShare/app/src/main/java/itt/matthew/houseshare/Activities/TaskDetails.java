package itt.matthew.houseshare.Activities;

import android.content.Intent;
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
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Locale;

import itt.matthew.houseshare.Events.OverviewEvent;
import itt.matthew.houseshare.Fragments.CostOverviewFragment;
import itt.matthew.houseshare.Fragments.PersonalCostOverview;
import itt.matthew.houseshare.Fragments.TaskOverviewFragment;
import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.Models.Cost;
import itt.matthew.houseshare.Models.CostSplit;
import itt.matthew.houseshare.Models.House;
import itt.matthew.houseshare.Models.Task;
import itt.matthew.houseshare.R;

public class TaskDetails extends AppCompatActivity {


    private House house;
    private Account current;
    private Task task;
    private CostSplit curr;
    private int selectedUser = -1;
    private Toolbar toolbar;

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);


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
        super.onBackPressed();

        startMainActivity(current);
    }

    private void setupUI(){


        AppBarLayout appbar = (AppBarLayout) findViewById(R.id.task_details_appbar);
        assert appbar != null;
        appbar.setBackgroundColor(task.getArea().getColor());


        toolbar = (Toolbar) findViewById(R.id.task_details_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_backspace_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setTitle(current.getName());


        TextView taskName = (TextView) findViewById(R.id.detailsTaskName);
        TextView taskDates = (TextView) findViewById(R.id.detailsTaskDates);
        TextView taskMembers = (TextView) findViewById(R.id.detailsTaskMembers);

        if (taskName != null) {
            taskName.setText("Area: " + task.getArea().getName());
        }
        if (taskDates != null) {

            DateFormat formatter = DateFormat.getDateInstance(DateFormat.SHORT);
            taskDates.setText("From " + formatter.format(task.getStartDate().getTime()) + " To " + formatter.format(task.getEndDate().getTime()));
        }
        if (taskMembers != null) {
            if(task.getInterval() == 1){
                taskMembers.setText("Occurs Every Day");
            }else
                taskMembers.setText("Occurs Every " + task.getInterval() + " Days");
        }


        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(darker(task.getArea().getColor()));

        toolbar.setBackgroundColor(task.getArea().getColor());

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
        int taskLocation = b.getInt("task");
        current = b.getParcelable("account");
        task = house.getTask().get(taskLocation);

    }


    public void startMainActivity(Account acc) {

        Intent i = new Intent(this, MainActivity.class);
        Bundle b = new Bundle();
        b.putParcelable("Account", acc);
        i.putExtra("Bundle", b);
        startActivity(i);
        finish();

    }


    private void loadSelection(int i){
        switch (i) {
            case 0:
                TaskOverviewFragment overviewFragment = new TaskOverviewFragment();
                toolbar.setTitle("Task Overview");
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putParcelable("house", house);
                bundle.putParcelable("account", current);
                bundle.putInt("task", getIntent().getBundleExtra("extra").getInt("task"));
                getIntent().putExtra("extra", bundle);
                fragmentTransaction.replace(R.id.taskDetailsFragmentHolder, overviewFragment);
                fragmentTransaction.addToBackStack(overviewFragment.getTag());
                fragmentTransaction.commit();
                break;
            case 1:
//                PersonalCostOverview personalCostOverview = new PersonalCostOverview();
//                toolbar.setTitle(cost.getSplit().get(selectedUser).getName()+"'s payments");
//                fragmentTransaction = fragmentManager.beginTransaction();
//                bundle = new Bundle();
//                bundle.putParcelable("house", house);
//                bundle.putParcelable("account", current);
//                bundle.putInt("cost", getIntent().getBundleExtra("extra").getInt("cost"));
//                bundle.putInt("selected", selectedUser);
//                getIntent().putExtra("extra", bundle);
//                fragmentTransaction.replace(R.id.detailsFragmentHolder, personalCostOverview);
//                fragmentTransaction.addToBackStack(personalCostOverview.getTag());
//                fragmentTransaction.commit();
                break;
        }
    }

}
