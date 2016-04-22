package itt.matthew.houseshare.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import itt.matthew.houseshare.Fragments.FBLoginFragment;
import itt.matthew.houseshare.R;

public class FBLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fblogin);

        getSupportActionBar().setTitle(R.string.welcome);

        }



}
