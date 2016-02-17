package itt.matthew.houseshare.Models;

import java.util.ArrayList;
import java.util.Calendar;

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

    public Cost(int interval, CostCategory Category, double amount, Calendar StartDate, Calendar EndDate, ArrayList<CostSplit> split ){

        this.interval = interval;
        this.Category = Category;
        this.amount = amount;
        this.split = split;
        this.StartDate = StartDate;
        this.EndDate = EndDate;
    }


    public Cost(){
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
