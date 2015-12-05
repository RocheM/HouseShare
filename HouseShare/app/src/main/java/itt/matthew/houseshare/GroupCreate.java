package itt.matthew.houseshare;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.Profile;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.TableOperationCallback;


public class GroupCreate extends AppCompatActivity {


    private Button button;
    private EditText editText;
    private MobileServiceClient mClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_create);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        try {
            mClient = new MobileServiceClient(
                    "https://houseshareproject.azure-mobile.net/",
                    "iuqOtKPRNqrMfasRrLARUYNrihSzwh94",
                    this
            );

        } catch (Exception e) {


        }

        button = (Button) findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editText = (EditText) findViewById(R.id.editText);
                String name = editText.getText().toString();


                editText = (EditText) findViewById(R.id.editText2);
                String description = editText.getText().toString();


                House toUp = new House(name, description);

                upload(toUp);

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
