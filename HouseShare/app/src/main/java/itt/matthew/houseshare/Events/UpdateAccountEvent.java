package itt.matthew.houseshare.Events;

import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.Models.House;

/**
 * Created by Matthew on 09/02/2016.
 */
public class UpdateAccountEvent {

    Account account;
    House house;

    public UpdateAccountEvent(Account account, House house){
        this.account = account;
        this.house = house;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }

    public void setHouse(House house) {
        this.house = house;
    }

    public House getHouse() {
        return house;
    }
}
