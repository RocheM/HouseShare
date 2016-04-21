package itt.matthew.houseshare.Models;

/**
 * Created by Matthew on 27/01/2016.
 */
public class CostCategory {


    private String ID;
    private String name;
    private int color;

    public CostCategory(String name, int color){
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }


}
