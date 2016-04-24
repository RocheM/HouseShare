package itt.matthew.houseshare.Models;

import android.util.Pair;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Matthew on 30/03/2016.
 */
public class CostInstance {

    private Calendar date;
    private Calendar paidOn;
    public ArrayList<Pair<String, Boolean>> paidList(){
        return paid;
    }

    public boolean isPaid(int i) {

        return (paid.get(i).second);
    }

    public void setPaid(int i, boolean setPaid) {

        String temp = paid.get(i).first;
        Pair<String, Boolean> toSet = new Pair<>(temp, setPaid);

        paid.set(i, toSet);

    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }


    public Calendar getPaidOn() {
        return paidOn;
    }

    public void setPaidOn(Calendar paidOn) {
        this.paidOn = paidOn;
    }

    public ArrayList<Pair<String, Boolean>> paid;



    public CostInstance(Calendar date, ArrayList<Pair<String, Boolean>> paid){
        this.date = date;
        this.paid = paid;
    }





}
