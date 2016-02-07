package itt.matthew.houseshare.Models;

import java.util.ArrayList;

/**
 * Created by Matthew on 05/02/2016.
 */
public class CostSplit {

    private String userFacebookID;
    private double amount;

    public CostSplit(String userFacebookID, double amount)
    {
        this.userFacebookID = userFacebookID;
        this.amount = amount;


    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getUserFacebookID() {
        return userFacebookID;
    }

    public void setUserFacebookID(String userFacebookID) {
        this.userFacebookID = userFacebookID;
    }
}
