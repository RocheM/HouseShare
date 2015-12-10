package itt.matthew.houseshare.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.Profile;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.TableQueryCallback;

import java.io.InputStream;
import java.util.List;

import itt.matthew.houseshare.Activities.GroupCreate;
import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.R;

public class MainActivity extends AppCompatActivity {

    private TextView welcomeMessage;
    private MobileServiceClient mClient;
    private MobileServiceTable<Account> mAccountTable;
    private Account current;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Profile profile = Profile.getCurrentProfile();

        if(profile != null) {
           new DownloadImageTask((ImageView) findViewById(R.id.imageView2))
                   .execute(profile.getProfilePictureUri(500, 500).toString());

            welcomeMessage = (TextView) findViewById(R.id.details);
            welcomeMessage.setText("Welcome " + Profile.getCurrentProfile().getName());
        }

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent(getApplicationContext(),GroupCreate.class);
                startActivity(i);
            }
        });



        try {
            mClient = new MobileServiceClient(
                    "https://houseshareproject.azure-mobile.net/",
                    "iuqOtKPRNqrMfasRrLARUYNrihSzwh94",
                    this
            );

        } catch (Exception e) {


        }

        mAccountTable = mClient.getTable(Account.class);
        lookupAccount(Profile.getCurrentProfile().getId());


    }


    public void lookupAccount(final String facebookID) {


        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                        mAccountTable.where()
                                .field("facebookID")
                                .eq(facebookID)
                                .execute(new TableQueryCallback<Account>() {
                                             @Override
                                             public void onCompleted(List<Account> result, int count, Exception exception, ServiceFilterResponse response) {
                                                 if (exception == null) {
                                                     current = result.get(0);
                                                     welcomeMessage.append("\nGroup ID: " + current.getHouseID());

                                                 } else
                                                     exception.printStackTrace();
                                             }
                                         }
                                );
                } catch (Exception exception) {
                    exception.printStackTrace();
                }


                return null;
            }

        }.execute();


    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

}