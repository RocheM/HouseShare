package itt.matthew.houseshare.Models;

import android.net.Uri;

import java.util.ArrayList;

/**
 * Created by Matthew on 05/02/2016.
 */
public class CostSplit {

    private String userFacebookID;
    private String name;
    private double amount;
    private boolean custom;

    public CostSplit(String userFacebookID, String name, double amount, boolean custom)
    {
        this.userFacebookID = userFacebookID;
        this.name = name;
        this.amount = amount;
        this.custom = custom;

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

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCustom(boolean custom) {
        this.custom = custom;
    }

    public Boolean getCustom(){
        return custom;
    }

}
