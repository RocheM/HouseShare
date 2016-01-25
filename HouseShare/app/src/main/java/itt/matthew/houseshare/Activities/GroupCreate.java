package itt.matthew.houseshare.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.Profile;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.TableOperationCallback;
import com.microsoft.windowsazure.mobileservices.table.TableQueryCallback;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.Models.House;
import itt.matthew.houseshare.R;


public class GroupCreate extends AppCompatActivity {


    private Button button;
    private EditText editText;
    private MobileServiceClient mClient;
    private House toUp;
    private Account workingAccount;
    private MobileServiceTable<House> mHouseTable;
    private MobileServiceTable<Account> mAccountTable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_create);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        try {
            mClient = new MobileServiceClient(
                    "https://backendhs.azurewebsites.net",
                    this
            );

        } catch (Exception e) {


        }

        mHouseTable = mClient.getTable(House.class);
        mAccountTable = mClient.getTable(Account.class);

        button = (Button) findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editText = (EditText) findViewById(R.id.editText);
                String name = editText.getText().toString();


                editText = (EditText) findViewById(R.id.editText2);
                String description = editText.getText().toString();


                toUp = new House(name, description);


                Account current = new Account(Profile.getCurrentProfile().getId(), Profile.getCurrentProfile().getName());

                ArrayList<Account> members = new ArrayList<Account>();
                members.add(current);
                toUp.setMembers(members);

                setID(toUp);

            }
        });



    }

    public void setID(final House item){

        mHouseTable.select("id").execute(new TableQueryCallback<House>() {
            @Override
            public void onCompleted(List<House> result, int count, Exception exception, ServiceFilterResponse response) {
               if (exception == null) {
                       item.setID(count + 1);
                       getAccount(item);
               }
                else
                   exception.printStackTrace();
            }
        });
    }

    public void getAccount(final House item){

        mAccountTable.where().field("facebookID").eq(Profile.getCurrentProfile().getId()).execute(new TableQueryCallback<Account>() {
            @Override
            public void onCompleted(List<Account> result, int count, Exception exception, ServiceFilterResponse response) {
                workingAccount = result.get(0);
                workingAccount.setHouseID(item.getHouseID());
                mAccountTable.update(workingAccount);
                upload(item);
            }
        });

    }


    public void upload(House item) {



        mClient.getTable(House.class).insert(item, new TableOperationCallback<House>() {
            public void onCompleted(House entity, Exception exception, ServiceFilterResponse response) {
                if (exception == null) {

                  Intent i = new Intent(getApplicationContext(), MainActivity.class);
                  startActivity(i);

                } else {

                    exception.printStackTrace();

                }
            }
        });

    }


}
