package itt.matthew.houseshare.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.R;

public class JoinHouse extends AppCompatActivity {

    private Account current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_house);


        setupData();
        setupUI();
    }

    private void setupData(){

        Bundle b = getIntent().getBundleExtra("Bundle");
        current = b.getParcelable("Account");

    }

    private void setupUI(){

        TextView welcomeMessage = (TextView) findViewById(R.id.joinHouseWelcome);
        Button createHouseButton = (Button) findViewById(R.id.createHouseButton);
        assert createHouseButton != null;
        createHouseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), GroupCreate.class);
                Bundle b = new Bundle();
                b.putParcelable("Account", current);
                i.putExtra("Bundle", b);
                startActivity(i);
                finish();
            }
        });

        Button joinHouseButton = (Button) findViewById(R.id.joinExistingHouseButton);
        assert joinHouseButton != null;
        joinHouseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), joinExisting.class);
                Bundle b = new Bundle();
                b.putParcelable("Account", current);
                i.putExtra("Bundle", b);
                startActivity(i);
                finish();
            }
        });

        assert welcomeMessage != null;
        welcomeMessage.append(" " + current.getName() + "\nYou aren't a member of any houses, please select an option to join one");

    }
}
