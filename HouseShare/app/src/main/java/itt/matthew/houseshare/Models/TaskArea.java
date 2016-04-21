package itt.matthew.houseshare.Models;

/**
 * Created by Matthew on 12/04/2016.
 */
public class TaskArea {

    private String ID;
    private String name;
    private String description;
    private int color;

    public TaskArea(String name, String description, int color) {
        this.name = name;
        this.description = description;
        this.color = color;
    }

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

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

}
