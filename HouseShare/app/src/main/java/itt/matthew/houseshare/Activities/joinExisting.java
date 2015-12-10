package itt.matthew.houseshare.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.Profile;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_existing);

        setupUI();
        setupAzure();

    }

    public void setupAzure() {

        try {
            mClient = new MobileServiceClient(
                    "https://houseshareproject.azure-mobile.net/",
                    "iuqOtKPRNqrMfasRrLARUYNrihSzwh94",
                    this
            );

        } catch (Exception e) {


        }

        mHouseTable = mClient.getTable(House.class);
        mAccountTable = mClient.getTable(Account.class);
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
        enterHouseIDText = (TextView) findViewById(R.id.enterHouseID);


    }

    public void checkIfExists() {

        String toCheck = enterHouseID.getText().toString();

        mHouseTable.where().field("HouseID").eq(toCheck).execute(new TableQueryCallback<House>() {
            @Override
            public void onCompleted(List<House> result, int count, Exception exception, ServiceFilterResponse response) {
                if (count == 0) {
                    enterHouseIDText.setHint("Does Not exist, try again");
                } else {
                    UpdateProfile(result.get(0));
                }

            }
        });

    }

    public void UpdateProfile(final House item) {

        mAccountTable.where().field("facebookID").eq(Profile.getCurrentProfile().getId()).execute(new TableQueryCallback<Account>() {
            @Override
            public void onCompleted(List<Account> result, int count, Exception exception, ServiceFilterResponse response) {
                if (exception == null) {
                    currentUser = result.get(0);
                    currentUser.setHouseID(item.getHouseID());
                    mAccountTable.update(currentUser);

                    ArrayList<Account> updateHouse = item.getMembers();
                    updateHouse.add(currentUser);
                    item.setMembers(updateHouse);
                    mHouseTable.update(item);

                    startMainActivity();
                }
            }
        });

    }


    public void startMainActivity() {

        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);

    }


}
