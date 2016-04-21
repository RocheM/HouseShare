package itt.matthew.houseshare.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.TableQueryCallback;

import java.util.ArrayList;
import java.util.List;

import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.Models.House;
import itt.matthew.houseshare.R;

public class joinExisting extends AppCompatActivity {

    private Button submitButton;
    private EditText enterHouseID;
    private TextView enterHouseIDText;
    private MobileServiceClient mClient;
    private MobileServiceTable<House> mHouseTable;
    private MobileServiceTable<Account> mAccountTable;
    private Account currentUser;
    private House currentHouse;



    public static final String SHAREDPREFFILE = "temp";
    public static final String USERIDPREF = "uid";
    public static final String TOKENPREF = "tkn";
    public static final String FBTOKENPREF = "fbt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_existing);

        setupAzure();
        setupData();
        setupUI();
    }

    public void setupAzure() {

        try {

            mClient = new MobileServiceClient(
                    "https://backendhs.azurewebsites.net",
                    this
            );

        } catch (Exception e) {


        }


        loadUserTokenCache(mClient);
        mHouseTable = mClient.getTable(House.class);
        mAccountTable = mClient.getTable(Account.class);
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



    private void setupData(){

        Bundle b = getIntent().getBundleExtra("Bundle");
        currentUser = b.getParcelable("Account");

    }

    public void setupUI() {

        submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                checkIfExists();
                                            }
                                        });


        enterHouseID = (EditText) findViewById(R.id.EnterHouseIDField);


    }

    public void checkIfExists() {

        final String toCheck = enterHouseID.getText().toString();

        mHouseTable.where().field("HouseID").eq(toCheck).execute(new TableQueryCallback<House>() {
            @Override
            public void onCompleted(List<House> result, int count, Exception exception, ServiceFilterResponse response) {
                if (count == 0) {
                    Toast.makeText(getApplicationContext(), "Does not exist please try again", Toast.LENGTH_SHORT).show();
                } else {
                    UpdateProfile(result.get(0));
                }

            }
        });

    }

    public void UpdateProfile(final House item) {

        mAccountTable.where().field("facebookID").eq(currentUser.getFacebookID()).execute(new TableQueryCallback<Account>() {
            @Override
            public void onCompleted(List<Account> result, int count, Exception exception, ServiceFilterResponse response) {
                if (exception == null) {
                    currentUser = result.get(0);
                    currentUser.setHouseID(item.getHouseID());
                    mAccountTable.update(currentUser);

                    ArrayList<Account> updateHouse = item.getMembers();
                    updateHouse.add(currentUser);
                    item.setMembers(updateHouse);
                    currentHouse = new House(item);
                    mHouseTable.update(item);

                    startMainActivity();
                }
            }
        });

    }


    public void startMainActivity() {

        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        Bundle b = new Bundle();
        b.putParcelable("Account", currentUser);
        b.putParcelable("House", currentHouse);
        i.putExtra("Bundle", b);

        startActivity(i);
        finish();

    }


}
