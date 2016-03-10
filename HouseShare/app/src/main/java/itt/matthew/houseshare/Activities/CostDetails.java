package itt.matthew.houseshare.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import itt.matthew.houseshare.Adapters_CustomViews.RVAccountAdapter;
import itt.matthew.houseshare.Adapters_CustomViews.RVAdapter;
import itt.matthew.houseshare.Fragments.CostSplitFragment;
import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.Models.Cost;
import itt.matthew.houseshare.Models.House;
import itt.matthew.houseshare.R;
import com.paypal.android.MEP.CheckoutButton;
import com.paypal.android.MEP.PayPal;
import com.paypal.android.MEP.PayPalActivity;
import com.paypal.android.MEP.PayPalInvoiceData;
import com.paypal.android.MEP.PayPalInvoiceItem;
import com.paypal.android.MEP.PayPalPayment;

import java.math.BigDecimal;
import java.util.ArrayList;

public class CostDetails extends AppCompatActivity implements View.OnClickListener {

    private House house;
    private Cost cost;
    private boolean _paypalLibraryInit;
    private CheckoutButton launchPayPalButton;
    private final int REQUEST_PAYPAL_CHECKOUT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cost_details);

        setupData();
        setupUI();

    }


    private void setupUI(){

        initLibrary();
        showPayPalButton();
    }

    private void setupData(){

        Bundle b = getIntent().getBundleExtra("extra");
        house = b.getParcelable("house");
        int costLocation = b.getInt("cost");
        cost = house.getCost().get(costLocation);

    }

    public void initLibrary() {
        PayPal pp = PayPal.getInstance();

        if (pp == null) {  // Test to see if the library is already initialized

            // This main initialization call takes your Context, AppID, and target server
            pp = PayPal.initWithAppID(this, "APP-80W284485P519543T", PayPal.ENV_NONE);


            // Required settings:

            // Set the language for the library
            pp.setLanguage("en_US");

            // Some Optional settings:

            // Sets who pays any transaction fees. Possible values are:
            // FEEPAYER_SENDER, FEEPAYER_PRIMARYRECEIVER, FEEPAYER_EACHRECEIVER, and FEEPAYER_SECONDARYONLY
            pp.setFeesPayer(PayPal.FEEPAYER_EACHRECEIVER);

            // true = transaction requires shipping
            pp.setShippingEnabled(false);

            _paypalLibraryInit = true;
        }
    }

    private void showPayPalButton() {

        // Generate the PayPal checkout button and save it for later use
        PayPal pp = PayPal.getInstance();
        launchPayPalButton = pp.getCheckoutButton(this, PayPal.BUTTON_278x43, CheckoutButton.TEXT_PAY);


        // The OnClick listener for the checkout button
        launchPayPalButton.setOnClickListener(this);

        // Add the listener to the layout
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams (AbsListView.LayoutParams.WRAP_CONTENT,
                AbsListView.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.bottomMargin = 10;
        launchPayPalButton.setLayoutParams(params);
        launchPayPalButton.setId(View.generateViewId());
        ((RelativeLayout) findViewById(R.id.RelativeLayout01)).addView(launchPayPalButton);
        ((RelativeLayout) findViewById(R.id.RelativeLayout01)).setGravity(Gravity.CENTER_HORIZONTAL);
    }



    public void PayPalButtonClick(View arg0) {
        // Create a basic PayPal payment
        PayPalPayment payment = new PayPalPayment();

        // Set the currency type
        payment.setCurrencyType("USD");

        // Set the recipient for the payment (can be a phone number)
        payment.setRecipient("matthew.the.roche@gmail.com");

        // Set the payment amount, excluding tax and shipping costs
        payment.setSubtotal(new BigDecimal(20));

        // Set the payment type--his can be PAYMENT_TYPE_GOODS,
        // PAYMENT_TYPE_SERVICE, PAYMENT_TYPE_PERSONAL, or PAYMENT_TYPE_NONE
        payment.setPaymentType(PayPal.PAYMENT_TYPE_GOODS);

        // PayPalInvoiceData can contain tax and shipping amounts, and an
        // ArrayList of PayPalInvoiceItem that you can fill out.
        // These are not required for any transaction.
        PayPalInvoiceData invoice = new PayPalInvoiceData();

        // Set the tax amount
        invoice.setTax(new BigDecimal(2));
        PayPalInvoiceItem item = new PayPalInvoiceItem();
        item.setID("1");
        item.setName("House");
        item.setTotalPrice(BigDecimal.valueOf(20.00));
        item.setQuantity(1);

        ArrayList<PayPalInvoiceItem> items = new ArrayList<>();
        items.add(item);
        invoice.setInvoiceItems(items);
        payment.setMerchantName("House Share");
        payment.setInvoiceData(invoice);


        Intent checkoutIntent = PayPal.getInstance().checkout(payment, this);
        startActivityForResult(checkoutIntent, REQUEST_PAYPAL_CHECKOUT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            PayPalActivityResult(requestCode, resultCode, data);
    }

    public void PayPalActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (resultCode) {
            // The payment succeeded
            case Activity.RESULT_OK:
                String payKey = intent.getStringExtra(PayPalActivity.EXTRA_PAY_KEY);
                this.paymentSucceeded(payKey);
                break;

            // The payment was canceled
            case Activity.RESULT_CANCELED:
                this.paymentCanceled();
                break;

            // The payment failed, get the error from the EXTRA_ERROR_ID and EXTRA_ERROR_MESSAGE
            case PayPalActivity.RESULT_FAILURE:
                String errorID = intent.getStringExtra(PayPalActivity.EXTRA_ERROR_ID);
                String errorMessage = intent.getStringExtra(PayPalActivity.EXTRA_ERROR_MESSAGE);
                this.paymentFailed(errorID, errorMessage);
        }
    }

    private void paymentSucceeded(String paykey){

        Toast.makeText(this, "SUCCESS " + paykey, Toast.LENGTH_SHORT).show();
    }

    private void paymentCanceled(){

        Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
    }

    private void paymentFailed(String errorID, String errorMessage){


        Toast.makeText(this, "FAILURE: " + errorID + " : " + errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        PayPalButtonClick(v);
    }
}
