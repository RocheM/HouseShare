package itt.matthew.houseshare.Models;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Matthew on 11/25/2015.
 */
public class House implements Parcelable {

    private String ID;
    private int HouseID;
    private String members;
    private String costs;
    private String costCategories;
    private String name;
    private String description;
    private String operators;
    private long createdOn;
    private String founder;
    private String archivedCosts;
    private String tasks;
    private String taskAreas;
    private String archivedTasks;

    public House (String name, String description){

        this.name = name;
        this.description = description;

        if (costCategories == null){
            setUpCostCategories();
        }
        createdOn = Calendar.getInstance().getTimeInMillis();
    }

    public House (House toSet){

        this.setID(toSet.getID());
        this.name = toSet.getName();
        this.description = toSet.getDescription();
        this.HouseID = toSet.getHouseID();
        setCosts(toSet.getCost());
        setMembers(toSet.getMembers());
        ArrayList<Account> members = toSet.getMembers();
        setMembers(members);
        setCostCategories(toSet.getCostCategory());
        setOperators(toSet.getOperators());
        setCreatedOn(toSet.getCreatedOn());
        setFounder(toSet.getFounder());
        setArchivedCosts(toSet.getArchivedCosts());
        setTasks(toSet.getTask());
        setTaskAreas(toSet.getTaskArea());
        setArchivedTasks(toSet.getArchivedTasks());
    }


    private void setUpCostCategories(){

        CostCategory Rent = new CostCategory("Rent", Color.rgb(26, 63, 28));
        CostCategory Internet = new CostCategory("Internet", Color.rgb(11, 91, 71));
        CostCategory Gas = new CostCategory("Gas", Color.rgb(49, 30, 100));
        CostCategory Electricity = new CostCategory("Electricity", Color.rgb(100, 9, 27));

        ArrayList<CostCategory> categories = new ArrayList<CostCategory>();
        categories.add(Rent);
        categories.add(Internet);
        categories.add(Gas);
        categories.add(Electricity);


        TaskArea  Kitchen= new TaskArea ("Kitchen", "Clean the Kitchen", Color.rgb(26, 63, 28));
        TaskArea LivingRoom = new TaskArea ("Living Room", "Clean the Living Room",  Color.rgb(11, 91, 71));
        TaskArea Bathroom = new TaskArea ("Bathroom","Clean the Bathroom", Color.rgb(49, 30, 100));
        TaskArea Garden = new TaskArea ("Garden", "Tidy the Garden", Color.rgb(100, 9, 27));

        ArrayList<TaskArea > taskAreas = new ArrayList<TaskArea>();
        taskAreas.add(Kitchen);
        taskAreas.add(LivingRoom);
        taskAreas.add(Bathroom);
        taskAreas.add(Garden);



        ArrayList<Cost> costs = new ArrayList<Cost>();
        ArrayList<Cost> archivedCosts = new ArrayList<Cost>();
        ArrayList<Task> tasks = new ArrayList<>();
        ArrayList<Task> archivedTasks = new ArrayList<Task>();

        setCosts(costs);
        setArchivedCosts(archivedCosts);
        setCostCategories(categories);
        setTasks(tasks);
        setTaskAreas(taskAreas);
        setArchivedTasks(archivedTasks);


    }


    public ArrayList<String> getOperators() {

        final Gson gson = new Gson();

        JsonParser parser = new JsonParser();
        JsonArray o = parser.parse(operators).getAsJsonArray();

        ArrayList<String> toReturn = new ArrayList<String>();


        for (int i = 0; i < o.size(); i++) {
            toReturn.add(gson.fromJson(o.get(i), String.class));
        }


        return toReturn;
    }

    public void setOperators(ArrayList<String> operatorsList) {
        final Gson gson = new Gson();

        Type listOfTestObject = new TypeToken<ArrayList<String>>(){}.getType();
       operators = gson.toJson(operatorsList, listOfTestObject);
    }

    public Calendar getCreatedOn() {

        Calendar toReturn = new GregorianCalendar();
        toReturn.setTimeInMillis(createdOn);

        return toReturn;
    }

    public void setCreatedOn(Calendar createdOn) {
        this.createdOn = createdOn.getTimeInMillis();
    }

    public String getFounder() {
        return founder;
    }

    public void setFounder(String founder) {
        this.founder = founder;
    }

    public House(Parcel in){
        readFromParcel(in);
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getHouseID() { return HouseID; }

    public void setHouseID(int HouseID) {this.HouseID = HouseID; }


    public ArrayList<Account> getMembers() { return getMembersFromJSON();}


    private ArrayList<Account> getMembersFromJSON(){

        final Gson gson = new Gson();

        JsonParser parser = new JsonParser();
        JsonArray o = parser.parse(members).getAsJsonArray();

        ArrayList<Account> toReturn = new ArrayList<Account>();

        for (int i = 0; i < o.size(); i++) {
           toReturn.add(gson.fromJson(o.get(i), Account.class));
        }


        return toReturn;
    }


    public void setMembers(ArrayList<Account> toUpload){

        final Gson gson = new Gson();

        Type listOfTestObject = new TypeToken<ArrayList<Account>>(){}.getType();
        members = gson.toJson(toUpload, listOfTestObject);

    }

    public ArrayList<Cost> getCost() { return getCostFromJSON();}


    private ArrayList<Cost> getCostFromJSON(){

        final Gson gson = new Gson();

        JsonParser parser = new JsonParser();
        JsonArray o = parser.parse(costs).getAsJsonArray();

        ArrayList<Cost> toReturn = new ArrayList<Cost>();


        for (int i = 0; i < o.size(); i++) {
            toReturn.add(gson.fromJson(o.get(i), Cost.class));
        }


        return toReturn;
    }


    public void setCosts(ArrayList<Cost> toUpload){

        final Gson gson = new Gson();

        Type listOfTestObject = new TypeToken<ArrayList<Cost>>(){}.getType();
        costs = gson.toJson(toUpload, listOfTestObject);
    }




    public ArrayList<Task> getTask() { return getTaskFromJSON();}


    private ArrayList<Task> getTaskFromJSON(){

        final Gson gson = new Gson();

        JsonParser parser = new JsonParser();
        JsonArray o = parser.parse(tasks).getAsJsonArray();

        ArrayList<Task> toReturn = new ArrayList<Task>();


        for (int i = 0; i < o.size(); i++) {
            toReturn.add(gson.fromJson(o.get(i), Task.class));
        }


        return toReturn;
    }


    public void setTasks(ArrayList<Task> toUpload){

        final Gson gson = new Gson();

        Type listOfTestObject = new TypeToken<ArrayList<Cost>>(){}.getType();
        tasks = gson.toJson(toUpload, listOfTestObject);
    }


    public ArrayList<Cost> getArchivedCosts() { return getArchivedCostsFromJSON();}

    private ArrayList<Cost> getArchivedCostsFromJSON(){

        final Gson gson = new Gson();

        JsonParser parser = new JsonParser();
        JsonArray o = parser.parse(archivedCosts).getAsJsonArray();

        ArrayList<Cost> toReturn = new ArrayList<Cost>();


        for (int i = 0; i < o.size(); i++) {
            toReturn.add(gson.fromJson(o.get(i), Cost.class));
        }


        return toReturn;
    }


    public void setArchivedCosts(ArrayList<Cost> toUpload){

        final Gson gson = new Gson();

        Type listOfTestObject = new TypeToken<ArrayList<Cost>>(){}.getType();
        archivedCosts = gson.toJson(toUpload, listOfTestObject);
    }



    public ArrayList<Task> getArchivedTasks() { return getArchivedTasksFromJSON();}

    private ArrayList<Task> getArchivedTasksFromJSON(){

        final Gson gson = new Gson();

        JsonParser parser = new JsonParser();
        JsonArray o = parser.parse(archivedTasks).getAsJsonArray();

        ArrayList<Task> toReturn = new ArrayList<Task>();


        for (int i = 0; i < o.size(); i++) {
            toReturn.add(gson.fromJson(o.get(i), Task.class));
        }


        return toReturn;
    }


    public void setArchivedTasks(ArrayList<Task> toUpload){

        final Gson gson = new Gson();

        Type listOfTestObject = new TypeToken<ArrayList<Task>>(){}.getType();
        archivedTasks = gson.toJson(toUpload, listOfTestObject);
    }

    public ArrayList<CostCategory> getCostCategory() { return getCostCategoryFromJSON();}


    private ArrayList<CostCategory> getCostCategoryFromJSON(){

        final Gson gson = new Gson();

        JsonParser parser = new JsonParser();
        JsonArray o = parser.parse(costCategories).getAsJsonArray();

        ArrayList<CostCategory> toReturn = new ArrayList<CostCategory>();


        for (int i = 0; i < o.size(); i++) {
            toReturn.add(gson.fromJson(o.get(i), CostCategory.class));
        }


        return toReturn;
    }


    public void setCostCategories(ArrayList<CostCategory> toUpload){

        final Gson gson = new Gson();

        Type listOfTestObject = new TypeToken<ArrayList<CostCategory>>(){}.getType();
        costCategories = gson.toJson(toUpload, listOfTestObject);

    }


    public ArrayList<TaskArea> getTaskArea() { return getTaskAreaFromJSON();}


    private ArrayList<TaskArea> getTaskAreaFromJSON(){

        final Gson gson = new Gson();

        JsonParser parser = new JsonParser();
        JsonArray o = parser.parse(taskAreas).getAsJsonArray();

        ArrayList<TaskArea> toReturn = new ArrayList<TaskArea>();


        for (int i = 0; i < o.size(); i++) {
            toReturn.add(gson.fromJson(o.get(i), TaskArea.class));
        }


        return toReturn;
    }


    public void setTaskAreas(ArrayList<TaskArea> toUpload){

        final Gson gson = new Gson();

        Type listOfTestObject = new TypeToken<ArrayList<TaskArea>>(){}.getType();
        taskAreas = gson.toJson(toUpload, listOfTestObject);

    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {


        out.writeString(ID);
        out.writeInt(HouseID);
        out.writeString(members);
        out.writeString(costs);
        out.writeString(costCategories);
        out.writeString(name);
        out.writeString(description);
        out.writeString(operators);
        out.writeLong(createdOn);
        out.writeString(founder);
        out.writeString(archivedCosts);
        out.writeString(tasks);
        out.writeString(taskAreas);
        out.writeString(archivedTasks);

    }


    private void readFromParcel(Parcel in) {


        ID = in.readString();
        HouseID = in.readInt();
        members = in.readString();
        costs = in.readString();
        costCategories = in.readString();
        name = in.readString();
        description = in.readString();
        operators = in.readString();
        createdOn = in.readLong();
        founder = in.readString();
        archivedCosts = in.readString();
        tasks = in.readString();
        taskAreas = in.readString();
        archivedTasks = in.readString();
    }

    public static final Parcelable.Creator<House> CREATOR = new Parcelable.Creator<House>() {

        public House createFromParcel(Parcel in) {
            return new House(in);
        }

        public House[] newArray(int size) {
            return new House[size];
        }

    };

}
