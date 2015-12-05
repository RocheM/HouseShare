package itt.matthew.houseshare;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matthew on 11/25/2015.
 */
public class House {

//    private List<String> members;
    private String ID;
    private String name;
    private String description;

    public House (String name, String description){

        this.name = name;
        this.description = description;
//        members = new ArrayList<String>();
    }


//
//    public List<String> getMembers() {
//        return members;
//    }
//
//    public void setMembers(List<String> members) {
//        this.members = members;
//    }


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

}
