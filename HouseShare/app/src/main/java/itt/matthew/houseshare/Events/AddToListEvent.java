package itt.matthew.houseshare.Events;

import itt.matthew.houseshare.Models.Account;

/**
 * Created by Matth on 24/04/2016.
 */
public class AddToListEvent {

    private Account toAdd;

    public AddToListEvent(Account toAdd) {
        this.toAdd = toAdd;
    }

    public Account getToAdd() {
        return toAdd;
    }

    public void setToAdd(Account toAdd) {
        this.toAdd = toAdd;
    }
}
