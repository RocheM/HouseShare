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
import java.util.List;

/**
 * Created by Matthew on 11/25/2015.
 */
public class House implements Parcelable {

    private String ID;
    private int HouseID;
    private String members;
    private String name;
    private String description;

    public House (String name, String description){

        this.name = name;
        this.description = description;
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

      //  Account[] deserializedMembers = gson.fromJson(o, Account[].class);
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {

        out.writeString(ID);
        out.writeInt(HouseID);
        out.writeString(members);
        out.writeString(name);
        out.writeString(description);
    }


    private void readFromParcel(Parcel in) {

        ID = in.readString();
        HouseID = in.readInt();
        members = in.readString();
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
