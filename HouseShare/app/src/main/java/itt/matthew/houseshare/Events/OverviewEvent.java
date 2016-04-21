package itt.matthew.houseshare.Events;

import itt.matthew.houseshare.Adapters_CustomViews.OverviewAdapter;

/**
 * Created by Matthew on 01/04/2016.
 */
public class OverviewEvent {


    private int index;

    public OverviewEvent(int index){
        this.index = index;
    }


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }


}
