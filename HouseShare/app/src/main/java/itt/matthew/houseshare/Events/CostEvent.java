package itt.matthew.houseshare.Events;

import itt.matthew.houseshare.Models.Cost;

/**
 * Created by Matthew on 09/02/2016.
 */
public class CostEvent {

    private Cost cost;

    public CostEvent(Cost cost){
        this.cost = cost;
    }

    public void setCost(Cost cost) {
        this.cost = cost;
    }

    public Cost getCost() {
        return cost;
    }
}
