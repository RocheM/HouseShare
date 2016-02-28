package itt.matthew.houseshare.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;
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


    public static final String SHAREDPREFFILE = "temp";
    public static final String USERIDPREF = "uid";
    public static final String TOKENPREF = "tkn";
    public static final String FBTOKENPREF = "fbt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_create);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupData();


        try {
            mClient = new MobileServiceClient(
                    "https://backendhs.azurewebsites.net",
                    this

            );

        } catch (Exception e) {


        }

        mHouseTable = mClient.getTable(House.class);
        mAccountTable = mClient.getTable(Account.class);
        loadUserTokenCache(mClient);


        button = (Button) findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editText = (EditText) findViewById(R.id.editText);
                String name = editText.getText().toString();


                editText = (EditText) findViewById(R.id.editText2);
                String description = editText.getText().toString();


                toUp = new House(name, description);


                ArrayList<Account> members = new ArrayList<Account>();
                members.add(workingAccount);
                toUp.setMembers(members);

                setID(toUp);

            }
        });



    }

    private void setupData(){

        Bundle b = getIntent().getBundleExtra("Bundle");
        workingAccount = b.getParcelable("Account");

    }

    public void setID(final House item){

        mHouseTable.select("id").execute(new TableQueryCallback<House>() {
            @Override
            public void onCompleted(List<House> result, int count, Exception exception, ServiceFilterResponse response) {
               if (exception == null) {
                       item.setHouseID(count + 1);
                       getAccount(item);
               }
                else
                   exception.printStackTrace();
            }
        });
    }

    public void getAccount(final House item){

        mAccountTable.where().field("facebookID").eq(workingAccount.getFacebookID()).execute(new TableQueryCallback<Account>() {
            @Override
            public void onCompleted(List<Account> result, int count, Exception exception, ServiceFilterResponse response) {
                workingAccount = result.get(0);
                workingAccount.setHouseID(item.getHouseID());
                mAccountTable.update(workingAccount);
                upload(item);
            }
        });

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


    public void upload(House item) {

        toUp = new House(item);

        mClient.getTable(House.class).insert(item, new TableOperationCallback<House>() {
            public void onCompleted(House entity, Exception exception, ServiceFilterResponse response) {
                if (exception == null) {

                  Intent i = new Intent(getApplicationContext(), MainActivity.class);
                  Bundle b = new Bundle();
                  b.putParcelable("House", toUp);
                  b.putParcelable("Account", workingAccount);
                  i.putExtra("Bundle", b);
                  startActivity(i);

                } else {

                    exception.printStackTrace();

                }
            }
        });

    }


}
