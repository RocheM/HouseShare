package itt.matthew.houseshare.Models;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Matthew on 12/04/2016.
 */
public class TaskInstance {


    private Calendar date;

    public ArrayList<Pair<String, Boolean>> completedList(){
        return completed;
    }

    public boolean isCompleted(int i) {

        return (completed.get(i).second);
    }

    public void setCompleted(int i, boolean setCompleted) {

        String temp = completed.get(i).first;
        Pair<String, Boolean> toSet = new Pair<>(temp, setCompleted);

        completed.set(i, toSet);

    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public ArrayList<Pair<String, Boolean>> completed;

    public TaskInstance(Calendar date, ArrayList<Pair<String, Boolean>> completed){
        this.date = date;
        this.completed = completed;
    }

}
