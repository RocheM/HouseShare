package itt.matthew.houseshare.Models;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Matthew on 12/04/2016.
 */
public class Task {


    private int ID;
    private int TaskID;
    private ArrayList<TaskInstance> taskInstances;
    private TaskArea area;
    private int interval;
    private Calendar StartDate;
    private Calendar EndDate;
    private ArrayList<String> users;

    public Task(ArrayList<String> users, ArrayList<TaskInstance> instances, TaskArea area, int interval) {
        this.users = users;
        this.taskInstances = instances;
        this.area = area;
        this.interval = interval;

        initalizeIntervals();
    }

    public Task() {

    }



    public int getTaskID() {
        return TaskID;
    }

    public void setTaskID(int taskID) {
        TaskID = taskID;
    }

    public void  initalizeIntervals(){


        int daysBetween = daysBetween(StartDate.getTime(), EndDate.getTime());
        taskInstances= new ArrayList<TaskInstance>();
        Date temp = StartDate.getTime();

        int count = 0;
        boolean cont = false;
        do{


            Calendar cal = Calendar.getInstance();
            cal.setTime(temp);
            cal.add(Calendar.DATE, interval); // add interval days

            ArrayList<Pair<String, Boolean>> instances;
            instances = new ArrayList<>();

            for (int i = 0; i < users.size(); i++){

                Pair<String, Boolean>  instance = new Pair<>(users.get(i), false);

                instances.add(instance);
            }




            count ++;
            if(cal.after(EndDate)) {
                cont = true;
            }
            else
                taskInstances.add(new TaskInstance(cal, instances));



            temp = cal.getTime();

        }while(!cont);
    }


    public int daysBetween(Date d1, Date d2){
        return (int)( ( d1.getTime() - d2.getTime()) / (1000 * 60 * 60 * 24));
    }


    public ArrayList<String> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<String> users) {
        this.users = users;
    }

    public ArrayList<TaskInstance> getTaskInstances() {
        return taskInstances;
    }

    public void setTaskInstances(ArrayList<TaskInstance> taskInstances) {
        this.taskInstances = taskInstances;
    }

    public TaskArea getArea() {
        return area;
    }

    public void setArea(TaskArea area) {
        this.area = area;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public Calendar getStartDate() {
        return StartDate;
    }

    public void setStartDate(Calendar startDate) {
        StartDate = startDate;
    }

    public Calendar getEndDate() {
        return EndDate;
    }

    public void setEndDate(Calendar endDate) {
        EndDate = endDate;
    }

}
