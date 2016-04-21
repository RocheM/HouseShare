package itt.matthew.houseshare.Events;

import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.Models.Cost;

/**
 * Created by Matthew on 06/04/2016.
 */
public class LongPressEvent {

    private Account current;
    private Cost cost;
    private Integer location;

    public LongPressEvent(Account current, Cost cost, Integer location){

        this.current = current;
        this.cost = cost;
        this.location = location;

    }



    public Account getCurrent() {
        return current;
    }

    public void setCurrent(Account current) {
        this.current = current;
    }

    public Cost getCost() {
        return cost;
    }

    public void setCost(Cost cost) {
        this.cost = cost;
    }

    public Integer getLocation() {
        return location;
    }

    public void setLocation(Integer location) {
        this.location = location;
    }


}
