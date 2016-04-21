package itt.matthew.houseshare.Events;

import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.Models.House;

/**
 * Created by Matthew on 24/01/2016.
 */
public class ReplyEvent {


    private House house;
    private Account account;

    public ReplyEvent(House house, Account account) {

        this.account = account;
        this.house = house;
    }

    public House getHouse() {
        return house;
    }

    public void setHouse(House house) {
        this.house = house;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }
}
