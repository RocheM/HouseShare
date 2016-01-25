package itt.matthew.houseshare.Events;

import java.util.Date;

/**
 * Created by Matthew on 24/01/2016.
 */
public class DateMessage {

    private int  day;
    private int month;
    private int year;
    private char type;

    public DateMessage(int day, int month, int year, char type){

        this.day = day;
        this.month = month;
        this.year = year;
        this.type = type;
    }

    public DateMessage(int day, int month, int year){

        this.day = day;
        this.month = month;
        this.year = year;
        type = 'n';
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }
}
