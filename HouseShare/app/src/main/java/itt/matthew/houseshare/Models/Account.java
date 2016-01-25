package itt.matthew.houseshare.Models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.facebook.Profile;

/**
 * Created by Matthew on 11/23/2015.
 */
public class Account implements Parcelable {

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

    public Account(Parcel in){
        readFromParcel(in);
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {

        out.writeString(Id);
        out.writeString(facebookID);
        out.writeString(name);
        out.writeString(birthday);
        out.writeString(location);
        out.writeString(about);
        out.writeInt(houseID);
    }
    private void readFromParcel(Parcel in) {

        Id = in.readString();
        facebookID = in.readString();
        name = in.readString();
        birthday = in.readString();
        location = in.readString();
        about = in.readString();
        houseID = in.readInt();

    }

        public static final Parcelable.Creator<Account> CREATOR = new Parcelable.Creator<Account>() {

        public Account createFromParcel(Parcel in) {
            return new Account(in);
        }

        public Account[] newArray(int size) {
            return new Account[size];
        }

    };

}
