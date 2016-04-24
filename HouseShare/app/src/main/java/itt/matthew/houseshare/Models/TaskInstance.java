package itt.matthew.houseshare.Models;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Matthew on 12/04/2016.
 */
public class TaskInstance {


    private Calendar date;
    private String account;
    private Boolean paid;
    private String notes;
    private Calendar paidOn;


    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }


    public TaskInstance(Calendar date, String account, Boolean paid){
        this.date = date;
        this.account= account;
        this.paid = paid;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }


    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public Calendar getPaidOn() {
        return paidOn;
    }

    public void setPaidOn(Calendar paidOn) {
        this.paidOn = paidOn;
    }
}
