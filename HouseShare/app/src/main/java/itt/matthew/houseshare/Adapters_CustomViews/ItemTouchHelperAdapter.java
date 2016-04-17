package itt.matthew.houseshare.Adapters_CustomViews;

/**
 * Created by Matthew on 13/04/2016.
 */
public interface ItemTouchHelperAdapter {


    boolean onItemMove(int fromPosition, int toPosition);
    void onItemDismiss(int position);
}
