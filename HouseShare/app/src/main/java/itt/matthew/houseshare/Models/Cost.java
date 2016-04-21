package itt.matthew.houseshare.Models;

import android.util.Log;
import android.util.Pair;
import android.widget.ArrayAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Matthew on 24/01/2016.
 */
public class Cost {

    private int ID;
    private int CostID;
    private int interval;
    private CostCategory Category;
    private double amount;
    private ArrayList<CostSplit> split;
    private Calendar StartDate;
    private Calendar EndDate;
    private ArrayList<CostInstance> intervals;
    private int daysBetween;

    public Cost(int interval, CostCategory Category, double amount, Calendar StartDate, Calendar EndDate, ArrayList<CostSplit> split ){


        this.interval = interval;
        this.Category = Category;
        this.amount = amount;
        this.split = split;
        this.StartDate = StartDate;
        this.EndDate = EndDate;
        initalizeIntervals();

    }

    public void  initalizeIntervals(){


        this.daysBetween = daysBetween(StartDate.getTime(), EndDate.getTime());
        intervals = new ArrayList<CostInstance>();
        Date temp = StartDate.getTime();

        int count = 0;
        boolean cont = false;
        do{


            Calendar cal = Calendar.getInstance();
            cal.setTime(temp);
            cal.add(Calendar.DATE, interval); // add interval days

            ArrayList<Pair<String, Boolean>> instances;
            instances = new ArrayList<>();

            for (int i = 0; i < split.size(); i++){

                Pair<String, Boolean>  instance = new Pair<>(split.get(i).getUserFacebookID(), false);

                instances.add(instance);
            }




            count ++;
            if(cal.after(EndDate)) {
                cont = true;
            }
            else
                intervals.add(new CostInstance(cal, instances));



            temp = cal.getTime();

        }while(!cont);
    }


    public Cost(){
    }

    public int daysBetween(Date d1, Date d2){
        return (int)( ( d1.getTime() - d2.getTime()) / (1000 * 60 * 60 * 24));
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getInterval() {
        return interval;
    }

    public ArrayList<CostInstance> getIntervals(){
        return intervals;
    }

    public void setIntervals(ArrayList<CostInstance> intervals){

        this.intervals = intervals;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public ArrayList<CostSplit> getSplit() {
        return split;
    }

    public void setSplit(ArrayList<CostSplit> split) {
        this.split = split;
    }

    public Calendar getEndDate() {
        return EndDate;
    }

    public void setEndDate(Calendar endDate) {
        EndDate = endDate;
    }

    public Calendar getStartDate() {
        return StartDate;
    }

    public void setStartDate(Calendar startDate) {
        StartDate = startDate;
    }

    public CostCategory getCategory() {
        return Category;
    }

    public void setCategory(CostCategory category) {
        Category = category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setCostID(int costID) {
        CostID = costID;
    }

    public int getCostID() {
        return CostID;
    }
}
