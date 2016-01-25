package itt.matthew.houseshare.Models;

/**
 * Created by Matthew on 24/01/2016.
 */
public enum Interval {
    
    DAY(0),
    WEEK(1),
    MONTH(2),
    YEAR(3);

    private String stringValue;

    Interval(int value) {

        if (value == 0)
            stringValue = "Day";
        else if (value == 1)
            stringValue = "Week";
        else if (value == 2)
            stringValue = "Month";
        else
            stringValue = "Year";
    }

    @Override
    public String toString() {
        return stringValue;
    }
}