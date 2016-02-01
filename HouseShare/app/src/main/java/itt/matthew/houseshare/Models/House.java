package itt.matthew.houseshare.Models;

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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
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

    public House (String name, String description){

        this.name = name;
        this.description = description;

        if (costCategories == null){
            setUpCostCategories();
        }
    }


    private void setUpCostCategories(){

        CostCategory Rent = new CostCategory("Rent", -65536);
        CostCategory Internet = new CostCategory("Internet", -16776961);
        CostCategory Gas = new CostCategory("Gas", -7829368);
        CostCategory Electricity = new CostCategory("Electricity", -256);

        ArrayList<CostCategory> categories = new ArrayList<CostCategory>();
        categories.add(Rent);
        categories.add(Internet);
        categories.add(Gas);
        categories.add(Electricity);

        ArrayList<Cost> costs = new ArrayList<Cost>();
//        Calendar test = new GregorianCalendar();
//        test = Calendar.getInstance();
//        costs.add(new Cost(0, categories.get(0), 1.0, test, test));

        setCosts(costs);
        setCostCategories(categories);

    }

    public House(Parcel in){
        readFromParcel(in);
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

    public void setID(int HouseID) {this.HouseID = HouseID; }


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
    }


    private void readFromParcel(Parcel in) {

        ID = in.readString();
        HouseID = in.readInt();
        members = in.readString();
        costs = in.readString();
        costCategories = in.readString();
        name = in.readString();
        description = in.readString();
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
