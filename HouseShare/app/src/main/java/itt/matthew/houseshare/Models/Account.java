package itt.matthew.houseshare.Models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Matthew on 11/23/2015.
 */
public class Account implements Parcelable {

    private String Id;
    private String facebookID;
    private String name;
    private String email;
    private String coverPhotoURL;
    private int houseID = -1;

    public Account(String facebookID, String name, String email, String coverPhotoURL){

        this.facebookID = facebookID;
        this.name = name;
        this.email = email;
        this.coverPhotoURL = coverPhotoURL;

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


    public String getEmail(){
        return email;
    }

    public void setEmail(String location){
        this.email = location;
    }
    public String getFacebookID(){
        return facebookID;
    }

    public int getHouseID(){return houseID;}

    public void setHouseID(int houseID) {this.houseID = houseID; }

    public String getCoverPhotoURL() {
        return coverPhotoURL;
    }

    public void setCoverPhotoURL(String coverPhotoURL) {
        this.coverPhotoURL = coverPhotoURL;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {

        out.writeString(Id);
        out.writeString(facebookID);
        out.writeString(name);
        out.writeString(email);
        out.writeString(coverPhotoURL);
        out.writeInt(houseID);
    }
    private void readFromParcel(Parcel in) {

        Id = in.readString();
        facebookID = in.readString();
        name = in.readString();
        email = in.readString();
        coverPhotoURL = in.readString();
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
