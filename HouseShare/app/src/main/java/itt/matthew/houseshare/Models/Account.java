package itt.matthew.houseshare.Models;

import android.graphics.Bitmap;

import com.facebook.Profile;

/**
 * Created by Matthew on 11/23/2015.
 */
public class Account {

    private String Id;
    private String facebookID;
    private String name;
    private String birthday;
    private String location;
    private String about;
    private int houseID;

    public Account(String facebookID, String name, String birthday, String location, String about){

        this.facebookID = facebookID;
        this.name = name;
        this.birthday = birthday;
        this.location = location;
        this.about = about;

    }


    public Account(String facebookID, String name) {

        this.facebookID = facebookID;
        this.name = name;
    }



    public String getId(){
        return Id;
    }
    public String getName(){
        return name;
    }


    public String getBirthday(){
        return birthday;
    }
    public void setBirthday(String birthday){
        this.birthday = birthday;
    }

    public String getLocation(){
        return location;
    }

    public void setLocation(String location){
        this.location = location;
    }

    public String getAbout(){
        return about;
    }

    public void setAbout(String about){
        this.about = about;
    }

    public String getFacebookID(){
        return facebookID;
    }

    public int getHouseID(){return houseID;}

    public void setHouseID(int houseID) {this.houseID = houseID; }

}
