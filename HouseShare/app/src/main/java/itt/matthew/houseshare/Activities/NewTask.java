package itt.matthew.houseshare.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.notifications.NotificationsManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import itt.matthew.houseshare.Events.CostEvent;
import itt.matthew.houseshare.Events.MyHandler;
import itt.matthew.houseshare.Events.RequestTaskEvent;
import itt.matthew.houseshare.Events.TaskEvent;
import itt.matthew.houseshare.Fragments.ArchivedCostFragment;
import itt.matthew.houseshare.Fragments.CreateTaskFragment;
import itt.matthew.houseshare.Fragments.color_dialog;
import itt.matthew.houseshare.Fragments.memberReorderFragment;
import itt.matthew.houseshare.Fragments.membersFragment;
import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.Models.Cost;
import itt.matthew.houseshare.Models.CostCategory;
import itt.matthew.houseshare.Models.CostSplit;
import itt.matthew.houseshare.Models.House;
import itt.matthew.houseshare.Models.Interval;
import itt.matthew.houseshare.Models.Task;
import itt.matthew.houseshare.Models.TaskArea;
import itt.matthew.houseshare.R;

public class NewTask extends AppCompatActivity implements ColorChooserDialog.ColorCallback {


    public static MobileServiceClient mClient;
    private MobileServiceTable<Account> mAccountTable;
    private MobileServiceTable<House> mHouseTable;


    private Account current;
    private House house;
    private Task newTask;
    private ArrayList<String> CategoryStrings = new ArrayList<String>();
    private ArrayAdapter<String> categoryAdapter;

    private Spinner category;
    private FloatingActionButton fab;
    private MaterialDialog dialog;

    private int categorySelectedColor = 0;
    private CircleImageView colorPreview;
    private EditText categoryName, categoryDescription;


    public static final String SHAREDPREFFILE = "temp";
    public static final String USERIDPREF = "uid";
    public static final String TOKENPREF = "tkn";
    public static final String FBTOKENPREF = "fbt";
    public static final String SENDER_ID = "18540511502";

    private android.support.v7.widget.Toolbar toolbar;
    private Window window;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        setupAzure();
        setupData();
        setupUI();

    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    private void setupData() {
        Bundle extras = this.getIntent().getBundleExtra("extra");
        current = extras.getParcelable("account");
        house = extras.getParcelable("house");
    }

    @Subscribe
    public void onTaskEvent(TaskEvent taskEvent){
        newTask = taskEvent.getTask();
        Toast.makeText(this, "Test", Toast.LENGTH_SHORT).show();
    }


    public void setupAzure(){


        try {
            mClient = new MobileServiceClient(
                    "https://backendhs.azurewebsites.net",
                    this);

        } catch (Exception e) {


        }

        loadUserTokenCache(mClient);
        mAccountTable = mClient.getTable(Account.class);
        mHouseTable = mClient.getTable(House.class);
        NotificationsManager.handleNotifications(this, SENDER_ID, MyHandler.class);

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


    private void setupUI() {


        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.task_toolbar);
        ViewPager viewPager = (ViewPager) findViewById(R.id.task_viewpager);
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.task_tabs);
        category = (Spinner) findViewById(R.id.task_CategorySpinner);
        com.getbase.floatingactionbutton.FloatingActionButton fab1 = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.fab_task_actionA);
        com.getbase.floatingactionbutton.FloatingActionButton fab2 = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.fab_task_actionB);

        fab1.setIconDrawable(getResources().getDrawable(R.drawable.ic_grade_white_18dp));
        fab2.setIconDrawable(getResources().getDrawable(R.drawable.ic_group_add_white_18dp));


        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_backspace_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        assert toolbar != null;
        toolbar.setTitle(R.string.createNewTask);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));


        if (viewPager != null && tabLayout != null) {
            viewPager.setAdapter(new TabsAdapter(getSupportFragmentManager()));
            tabLayout.setupWithViewPager(viewPager);
        }



        boolean wrapInScrollView = true;
        final MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                .title(R.string.createNewArea)
                .customView(R.layout.dialog_category, wrapInScrollView)
                .positiveText(R.string.create)
                .negativeText(R.string.cancel)
                .autoDismiss(false);


        for (int i = 0; i < house.getTaskArea().size(); i++) {
            CategoryStrings.add(i, house.getTaskArea().get(i).getName());
        }

        categoryAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_title, CategoryStrings);
        category.setAdapter(categoryAdapter);
        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {


                TaskArea taskArea = house.getTaskArea().get(category.getSelectedItemPosition());
                changeBackgroundColor(house.getTaskArea().get(category.getSelectedItemPosition()).getColor());
                newTask.setArea(taskArea);
                org.greenrobot.eventbus.EventBus.getDefault().post(new TaskEvent(newTask));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


        if (fab1 != null) {
            fab1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog = builder
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                    View dialogView = dialog.getView();
                                    boolean error = false;
                                    ColorDrawable drawable = new ColorDrawable(getResources().getColor(R.color.white));
                                    colorPreview.setImageDrawable(drawable);



                                    String name = categoryName.getText().toString();
                                    String description = categoryDescription.getText().toString();
                                    if (name.length() <= 0 || name.length() >= 50) {
                                        categoryName.setError("Invalid Name");
                                        error = true;
                                    } else if (description.length() <= 0 || description.length() >= 150) {
                                        categoryDescription.setError("Invalid Description");
                                        error = true;

                                    } else
                                        error = false;


                                    if (categorySelectedColor == 0) {
                                        error = true;
                                        categoryName.setError("Must Select A Color");
                                        Toast.makeText(getApplicationContext(), "Must Select A Color", Toast.LENGTH_SHORT).show();
                                    }


                                    if (!error) {
                                        categoryName.setText("");
                                        categoryName.setError(null);
                                        categoryDescription.setText("");
                                        categoryDescription.setError(null);
                                        drawable = new ColorDrawable(getResources().getColor(R.color.white));
                                        colorPreview.setImageDrawable(drawable);


                                        TaskArea newArea = new TaskArea(name, description, categorySelectedColor);
                                        categorySelectedColor = 0;
                                        ArrayList<TaskArea> cat = house.getTaskArea();
                                        cat.add(newArea);
                                        house.setTaskAreas(cat);
                                        UpdateUI();
                                        newTask.setArea(newArea);
                                        EventBus.getDefault().post(new TaskEvent(newTask));
                                        dialog.dismiss();
                                    }

                                }
                            })
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    dialog.dismiss();

                                    categoryName.setText("");
                                    categoryName.setError(null);
                                    ColorDrawable drawable = new ColorDrawable(getResources().getColor(R.color.white));
                                    colorPreview.setImageDrawable(drawable);
                                    categorySelectedColor = 0;


                                }
                            }).build();
                    dialog.show();
                    View dialogView = dialog.getCustomView();



                    colorPreview = (CircleImageView) dialogView.findViewById(R.id.color_preview);
                    colorPreview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            buildColor();
                        }
                    });

                    categoryName = (EditText) dialogView.findViewById(R.id.categoryNameInput);
                    categoryDescription  = (EditText) dialogView.findViewById(R.id.categoryDescriptionInput);

                    ColorDrawable drawable = new ColorDrawable(getResources().getColor(R.color.white));
                    colorPreview.setImageDrawable(drawable);



                }
            });
        }
    }



    private void buildColor() {

        showColorChooserPrimary();

    }


    public void showColorChooserPrimary() {

        new ColorChooserDialog.Builder(this, R.string.color_palette)
                .titleSub(R.string.color_palette)  // title of dialog when viewing shades of a color
                .doneButton(R.string.md_done_label)  // changes label of the done button
                .cancelButton(R.string.md_cancel_label)  // changes label of the cancel button
                .backButton(R.string.md_back_label)  // changes label of the back button
                .dynamicButtonColor(false)  // defaults to true, false will disable changing action buttons' color to currently selected color
                .allowUserColorInput(false)
                .show();

    }

    @Override
    public void onColorSelection(ColorChooserDialog dialog, int selectedColor) {

        categorySelectedColor = selectedColor;
        ColorDrawable drawable = new ColorDrawable();
        drawable.setColor(selectedColor);


        colorPreview.setImageDrawable(drawable);

    }






    private void UpdateUI() {


        final ArrayList<TaskArea> taskAreas  = house.getTaskArea();
        CategoryStrings.clear();
        for (int i = 0; i < taskAreas.size(); i++) {
            CategoryStrings.add(i, taskAreas.get(i).getName());
        }

        categoryAdapter.notifyDataSetChanged();
        category.setSelection(CategoryStrings.size() - 1);
        changeBackgroundColor(categorySelectedColor);

    }


    @Override
    public void onStart() {
        super.onStart();
        org.greenrobot.eventbus.EventBus.getDefault().register(this);

    }

    @Override
    public void onStop() {
        super.onStop();
        org.greenrobot.eventbus.EventBus.getDefault().unregister(this);

    }



    private void changeBackgroundColor(int color) {
        LinearLayout background = (LinearLayout) findViewById(R.id.task_title_container);
        Toolbar toolbar = (Toolbar) findViewById(R.id.task_toolbar);
        TabLayout tabs = (TabLayout) findViewById(R.id.task_tabs);

        if (background != null && toolbar != null && tabs != null) {
            background.setBackgroundColor(color);
            toolbar.setBackgroundColor(color);
            tabs.setBackgroundColor(color);
        }

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(darker(color));


    }

    public static int darker (int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = 1.0f - 0.8f * (1.0f - hsv[2]);
        color = Color.HSVToColor(hsv);
        return color;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            CreateNewTask();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.cost_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    private void CreateNewTask(){

        EventBus.getDefault().post(new RequestTaskEvent(1));




        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        String Content = "Area: " + newTask.getArea().getName() + "\nDescription:\n" + newTask.getArea().getDescription() + "\nInterval: " + Integer.toString(newTask.getInterval()) + "Day(s)\nDate Range:\n" + formatter.format(newTask.getStartDate().getTime()) + " to " + formatter.format(newTask.getEndDate().getTime());
        for (int i = 0; i < newTask.getUsers().size(); i++) {
            for (int j = 0; j < house.getMembers().size(); j++){
                if (newTask.getUsers().get(i). equals(house.getMembers().get(j).getFacebookID()))
                    Content = Content.concat("\n" + house.getMembers().get(j).getName() + " Takes Shift #" + Integer.toString(i + 1));
            }
        }


        new MaterialDialog.Builder(this)
                .title(R.string.confirm)
                .content(Content)
                .positiveText(R.string.confirm)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        ArrayList<Task> tasks = house.getTask();
                        newTask.initalizeIntervals();
                        tasks.add(newTask);
                        house.setTasks(tasks);
                        updateItem(house);
                    }
                })
                .negativeText(R.string.cancel)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }




    private void updateItem(final House item) {
        if (mClient == null) {
            return;
        }

        final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle(getString(R.string.updating));
        progress.setMessage(getString(R.string.updatingHouse));
        progress.show();


        final MaterialDialog error = new MaterialDialog.Builder(getApplicationContext()).build();

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    mHouseTable.update(item).get();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            finish();
                        }
                    });
                } catch (Exception exception) {

                    exception.printStackTrace();
                }
                return null;
            }
        }.execute();
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
                case 0: return CreateTaskFragment.newInstance("Test", "Test");
                case 1: return memberReorderFragment.newInstance("Test", "Test");

            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch(position) {
                case 0: return "New Task";
                case 1: return "Rotation";
            }
            return "";
        }
    }

}
