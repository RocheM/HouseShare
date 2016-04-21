package itt.matthew.houseshare.Events;

import itt.matthew.houseshare.Models.CostCategory;

/**
 * Created by Matthew on 27/01/2016.
 */
public class CategoryEvent {

    CostCategory newCategory;

    public CategoryEvent(CostCategory newCategory){
        this.newCategory = newCategory;
    }

}
