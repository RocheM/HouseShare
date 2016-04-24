package itt.matthew.houseshare.Fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
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
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.paypal.android.MEP.CheckoutButton;
import com.paypal.android.MEP.PayPal;
import com.paypal.android.MEP.PayPalActivity;
import com.paypal.android.MEP.PayPalInvoiceData;
import com.paypal.android.MEP.PayPalInvoiceItem;
import com.paypal.android.MEP.PayPalPayment;
import com.paypal.android.MEP.PayPalResultDelegate;

import org.greenrobot.eventbus.Subscribe;
import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

import itt.matthew.houseshare.Adapters_CustomViews.DateGridAdapter;
import itt.matthew.houseshare.Events.OverviewEvent;
import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.Models.Cost;
import itt.matthew.houseshare.Models.House;
import itt.matthew.houseshare.R;

public class PersonalCostOverview extends Fragment implements View.OnClickListener {


    private House house;
    private Account current;
    private Account selected;
    private Cost cost;
    private int costLocation;
    private RecyclerView rv;
    private int selectedCost = -1;
    private PersonalCostOverview.onItemInteractionInterface interactionInterface;
    private TextView personName, billName, numPaid, numMissed;
    private boolean _paypalLibraryInit;
    private CheckoutButton launchPayPalButton;
    private final int REQUEST_PAYPAL_CHECKOUT = 2;


    private MobileServiceClient mClient;
    private MobileServiceTable<Account> mAccountTable;
    private MobileServiceTable<House> mHouseTable;


    public static final String SHAREDPREFFILE = "temp";
    public static final String USERIDPREF = "uid";
    public static final String TOKENPREF = "tkn";
    public static final String FBTOKENPREF = "fbt";
    private MaterialDialog dialog;


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
        setupAzure();
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
        this.costLocation = costLocation;
        initLibrary();
    }



    private void setupAzure(){


        try {
            mClient = new MobileServiceClient(
                    "https://backendhs.azurewebsites.net",
                    this.getActivity()
            );


        } catch (Exception e) {
            new MaterialDialog.Builder(this.getActivity())
                    .title("Error")
                    .content(e.getMessage())
                    .positiveText("Ok")
                    .show();

        }

        loadUserTokenCache(mClient);
        mAccountTable = mClient.getTable(Account.class);
        mHouseTable = mClient.getTable(House.class);
    }


    private boolean loadUserTokenCache(MobileServiceClient client)
    {
        SharedPreferences prefs = getActivity().getSharedPreferences(SHAREDPREFFILE, Context.MODE_PRIVATE);
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
        dialog.hide();
        // Create a basic PayPal payment
        PayPalPayment payment = new PayPalPayment();


        // Set the currency type
        payment.setCurrencyType("USD");

        // Set the recipient for the payment (can be a phone number)
        payment.setRecipient("admin@youhouse.com");

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
        PayPalInvoiceItem item = new PayPalInvoiceItem();
        item.setID("1");
        item.setName(cost.getCategory().getName());
        item.setTotalPrice(new BigDecimal(cost.getAmount()));
        item.setQuantity(1);
        ArrayList<PayPalInvoiceItem> items = new ArrayList<>();
        items.add(item);
        invoice.setInvoiceItems(items);
        payment.setMerchantName("House Share");
        payment.setInvoiceData(invoice);

        Intent checkoutIntent = PayPal.getInstance().checkout(payment, this.getContext());
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


    private void updatePaid(){

        for (int i = 0; i < cost.getIntervals().get(selectedCost).paid.size(); i++){
            if(selected.getFacebookID().equals(cost.getIntervals().get(selectedCost).paid.get(i).first)){
                cost.getIntervals().get(selectedCost).setPaid(i, true);
                cost.getIntervals().get(selectedCost).setPaidOn(Calendar.getInstance());
            }
        }

        ArrayList<Cost> costs = house.getCost();
        costs.set(costLocation, cost);
        house.setCosts(costs);

        updateItemBG(house);

    }


    private void updateItemBG(final House item) {
        if (mClient == null) {
            return;
        }

        final ProgressDialog dialog = new ProgressDialog(this.getContext());
        dialog.setTitle("Updating Costs...");
        dialog.setMessage("Updating payment");
        dialog.show();

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    mHouseTable.update(item).get();
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {

                            dialog.hide();
                            MaterialDialog successDialog = new MaterialDialog.Builder(getContext())
                                    .title("Success")
                                    .content("Payment Successfully made.")
                                    .positiveText("Okay")
                                    .show();

                            updateRV();
                        }
                    });
                } catch (Exception exception) {


                    exception.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    private void updateRV(){

        rv.setLayoutManager(new GridLayoutManager(this.getContext(), 2));
        rv.setAdapter(new DateGridAdapter(house, current, selected, cost, this.getContext(), interactionInterface));
    }

    private void paymentSucceeded(String payKey){
        updatePaid();

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
    }


    private void setupUI(){



        interactionInterface = new onItemInteractionInterface() {
            @Override
            public void onCardViewTouch(View item, int position, Account account) {

                selectedCost = position;

                boolean ownsCost = false;
                boolean paid = false;

                if (current.getFacebookID().equals(selected.getFacebookID())){
                    ownsCost = true;
                }

                Double costAmount = 0.0;
                Date datePaid = Calendar.getInstance().getTime();

                for (int i = 0; i < cost.getIntervals().get(position).paid.size(); i++){
                    if(selected.getFacebookID().equals(cost.getIntervals().get(position).paid.get(i).first)){
                        paid = cost.getIntervals().get(position).paid.get(i).second;
                        if (cost.getIntervals().get(position).getPaidOn() !=  null){
                            datePaid = cost.getIntervals().get(position).getPaidOn().getTime();
                        }
                    }
                }

                for (int i = 0; i < cost.getSplit().size(); i++){
                    if (cost.getSplit().get(i).getUserFacebookID().equals(account.getFacebookID())){
                        costAmount = cost.getSplit().get(i).getAmount();
                    }
                }

                if (ownsCost && !paid) {
                    dialog = new MaterialDialog.Builder(getContext())
                            .title("Payment Details")
                            .customView(R.layout.dialog_payment, true)
                            .positiveText("Confirm")
                            .show();

                    NumberFormat format = NumberFormat.getCurrencyInstance(Locale.GERMANY);
                    TextView category = (TextView) dialog.getView().findViewById(R.id.dialog_payment_category);
                    TextView amount = (TextView) dialog.getView().findViewById(R.id.dialog_payment_amount);
                    category.setText(getString(R.string.category) + ":\t" +  cost.getCategory().getName());
                    amount.setText("Amount:\t" + format.format(costAmount));
                    amount.setPadding(0, 0, 0, 150);


                    showPayPalButton(dialog.getView());
                }

                if (paid){

                    dialog = new MaterialDialog.Builder(getContext())
                            .title("Make Payment")
                            .customView(R.layout.dialog_payment, true)
                            .positiveText("Confirm")
                            .show();

                    NumberFormat format = NumberFormat.getCurrencyInstance(Locale.GERMANY);
                    DateFormat formatter = DateFormat.getDateInstance(DateFormat.SHORT);

                    TextView category = (TextView) dialog.getView().findViewById(R.id.dialog_payment_category);
                    TextView amount = (TextView) dialog.getView().findViewById(R.id.dialog_payment_amount);
                    TextView datePaidText = (TextView) dialog.getView().findViewById(R.id.dialog_payment_datePaid);
                    category.setText(getString(R.string.category) + ":\t" +  cost.getCategory().getName());
                    amount.setText("Amount:\t" + format.format(costAmount));
                    datePaidText.setText("Payment Made: " + formatter.format(datePaid));
                    datePaidText.setVisibility(View.VISIBLE);

                }


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
