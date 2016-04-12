package itt.matthew.houseshare.Fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.*;

import com.afollestad.materialdialogs.MaterialDialog;
import com.paypal.android.MEP.CheckoutButton;
import com.paypal.android.MEP.PayPal;
import com.paypal.android.MEP.PayPalActivity;
import com.paypal.android.MEP.PayPalInvoiceData;
import com.paypal.android.MEP.PayPalPayment;
import com.paypal.android.MEP.PayPalResultDelegate;

import org.greenrobot.eventbus.Subscribe;

import java.math.BigDecimal;

import itt.matthew.houseshare.Adapters_CustomViews.DateGridAdapter;
import itt.matthew.houseshare.Events.OverviewEvent;
import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.Models.Cost;
import itt.matthew.houseshare.Models.House;
import itt.matthew.houseshare.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalCostOverview extends Fragment implements View.OnClickListener {


    private House house;
    private Account current;
    private Account selected;
    private Cost cost;
    private RecyclerView rv;
    private PersonalCostOverview.onItemInteractionInterface interactionInterface;
    private TextView personName, billName, numPaid, numMissed;
    private boolean _paypalLibraryInit;
    private CheckoutButton launchPayPalButton;
    private final int REQUEST_PAYPAL_CHECKOUT = 2;
    Intent checkoutIntent;

    public PersonalCostOverview() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_personal_cost_overview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupData();
        setupUI();
    }



    private void setupData(){

        Bundle extras = this.getActivity().getIntent().getBundleExtra("extra");
        house = extras.getParcelable("house");
        int costLocation = extras.getInt("cost");
        int selectedLoc = extras.getInt("selected");
        current = extras.getParcelable("account");
        cost = house.getCost().get(costLocation);

        selected = house.getMembers().get(selectedLoc);
        initLibrary();

    }
    private void showPayPalButton(View v) {

        // Generate the PayPal checkout button and save it for later use
        PayPal pp = PayPal.getInstance();
        launchPayPalButton = pp.getCheckoutButton(this.getContext(), PayPal.BUTTON_278x43, CheckoutButton.TEXT_PAY);

        // The OnClick listener for the checkout button
        launchPayPalButton.setOnClickListener(this);

        // Add the listener to the layout
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams (RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.bottomMargin = 10;
        launchPayPalButton.setLayoutParams(params);
        launchPayPalButton.setId(launchPayPalButton.getId());
        ((RelativeLayout) v.findViewById(R.id.payment_layout)).addView(launchPayPalButton);
        ((RelativeLayout) v.findViewById(R.id.payment_layout)).setGravity(Gravity.CENTER_HORIZONTAL);
    }

    public void PayPalButtonClick(View arg0) {
        // Create a basic PayPal payment
        PayPalPayment payment = new PayPalPayment();

        // Set the currency type
        payment.setCurrencyType("EUR");

        // Set the recipient for the payment (can be a phone number)
        payment.setRecipient("House Share");

        // Set the payment amount, excluding tax and shipping costs
        payment.setSubtotal(new BigDecimal(cost.getAmount()));

        // Set the payment type--his can be PAYMENT_TYPE_GOODS,
        // PAYMENT_TYPE_SERVICE, PAYMENT_TYPE_PERSONAL, or PAYMENT_TYPE_NONE
        payment.setPaymentType(PayPal.PAYMENT_TYPE_GOODS);

        // PayPalInvoiceData can contain tax and shipping amounts, and an
        // ArrayList of PayPalInvoiceItem that you can fill out.
        // These are not required for any transaction.
        PayPalInvoiceData invoice = new PayPalInvoiceData();

        // Set the tax amount
        invoice.setTax(new BigDecimal(0));


        checkoutIntent = PayPal.getInstance().checkout(payment, this.getContext());
        checkoutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(checkoutIntent, REQUEST_PAYPAL_CHECKOUT);
    }

    public void initLibrary() {
        PayPal pp = PayPal.getInstance();

        if (pp == null) {  // Test to see if the library is already initialized

            // This main initialization call takes your Context, AppID, and target server
            pp = PayPal.initWithAppID(getContext(), "APP-80W284485P519543T", PayPal.ENV_NONE);

            // Required settings:

            // Set the language for the library
            pp.setLanguage("en_US");

            // Some Optional settings:

            // Sets who pays any transaction fees. Possible values are:
            // FEEPAYER_SENDER, FEEPAYER_PRIMARYRECEIVER, FEEPAYER_EACHRECEIVER, and FEEPAYER_SECONDARYONLY
            pp.setFeesPayer(PayPal.FEEPAYER_EACHRECEIVER);

            // true = transaction requires shipping
            pp.setShippingEnabled(true);

            _paypalLibraryInit = true;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Toast.makeText(getContext(),Integer.toString(requestCode), Toast.LENGTH_SHORT).show();
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


    private void paymentSucceeded(String payKey){
        Toast.makeText(this.getContext(), payKey, Toast.LENGTH_SHORT).show();
    }
    private void paymentCanceled(){
        Toast.makeText(this.getContext(), "User Cancelled", Toast.LENGTH_SHORT).show();
    }
    private void paymentFailed(String errorID, String errorMessage){
        Toast.makeText(this.getContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onClick(View v){
        PayPalButtonClick(v);
        Toast.makeText(this.getContext(), "Test", Toast.LENGTH_SHORT).show();
    }


    private void setupUI(){



        interactionInterface = new onItemInteractionInterface() {
            @Override
            public void onCardViewTouch(View item, int position, Account account) {

                Toast.makeText(getActivity().getApplicationContext(), current.getName(), Toast.LENGTH_SHORT).show();
                boolean wrapInScrollView = true;

                MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                        .title("Make Payment")
                        .customView(R.layout.dialog_payment, wrapInScrollView)
                        .positiveText("Confirm")
                        .show();

                showPayPalButton(dialog.getView());



            }
        };

        rv = (RecyclerView) getView().findViewById(R.id.cost_personal_rv);
        rv.setLayoutManager(new GridLayoutManager(this.getContext(), 2));
        rv.setAdapter(new DateGridAdapter(house, current, selected, cost, this.getContext(), interactionInterface));

//        personName = (TextView) getView().findViewById(R.id.cost_personal_header_name);
//        billName = (TextView) getView().findViewById(R.id.cost_personal_header_bill);
//
//        personName.setText(selected.getName());
//        billName.setText(cost.getCategory().getName());

    }



    public interface onItemInteractionInterface{
        void onCardViewTouch(View item, int position, Account account);
    }


    @Subscribe
    public void onEvent(OverviewEvent event){

    }



    @Override
    public void onStart() {
        super.onStart();
        org.greenrobot.eventbus.EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        org.greenrobot.eventbus.EventBus.getDefault().unregister(this);
    }
}
