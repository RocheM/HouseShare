package itt.matthew.houseshare;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

public class MainActivity extends AppCompatActivity {

    JSONObject JSONresponse;
    TextView details;
    AccessToken CurrentToken = AccessToken.getCurrentAccessToken();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        new DownloadImageTask((ImageView) findViewById(R.id.imageView2))
                .execute(Profile.getCurrentProfile().getProfilePictureUri(500, 500).toString());

        details = (TextView) findViewById(R.id.details);
        details.setText("Welcome " + Profile.getCurrentProfile().getName());

        GraphRequest request = GraphRequest.newMeRequest(
                CurrentToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        JSONresponse = object;

                        try {


                            String detailsToAdd = JSONresponse.getString("birthday");
                            details.append("\n" + detailsToAdd);

                            detailsToAdd = JSONresponse.getString("bio");
                            details.append("\n" +detailsToAdd);

                            Log.d("Facebook", CurrentToken.toString());

                        }catch(JSONException ex){
                            ex.printStackTrace();
                        }
                        catch (NullPointerException ex){
                            ex.printStackTrace();
                        }
                    }

                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,bio,birthday");
        request.setParameters(parameters);
        request.executeAsync();



//        new PopulateProfile().execute();





    }
//
//    private class PopulateProfile extends AsyncTask<Void, Void, TextView> {
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            new GraphRequest(
//                    AccessToken.getCurrentAccessToken(),
//                    "/me",
//                    null,
//                    HttpMethod.GET,
//                    new GraphRequest.Callback() {
//                        public void onCompleted(GraphResponse response) {
//                            JSONresponse = response.getJSONObject();
//
//                        }
//                    }
//            ).executeAsync();

//
//            return null;
//        }
//        protected void onPostExecute(TextView toSet) {
//            toSet.setText(JSONresponse.toString());
//        }
//    }


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

}