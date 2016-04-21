package itt.matthew.houseshare.Events;

import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.Models.House;

/**
 * Created by Matthew on 22/01/2016.
 */
public class AccountEvent {

    public final Account account;
    public final House house;

    public AccountEvent(Account account, House house){

        this.account = account;
        this.house = house;

    }

}
