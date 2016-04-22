package itt.matthew.houseshare.Events;

import itt.matthew.houseshare.Models.Cost;
import itt.matthew.houseshare.Models.Task;

/**
 * Created by Matthew on 09/02/2016.
 */
public class TaskEvent {

    private Task task;

    public TaskEvent(Task task){
        this.task = task;
    }

    public void setTask(Task task) {
        this.task= task;
    }

    public Task getTask() {
        return task;
    }
}
