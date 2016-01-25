package itt.matthew.houseshare.Adapters_CustomViews;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.widget.ImageView;

import java.net.URL;

/**
 * Created by Matthew on 21/01/2016.
 */
public class GridItem {
    public final String name;
    public final String url;
    public final ImageView imageHolder;

    public GridItem(String name, ImageView imageHolder, String url) {
        this.name = name;
        this.imageHolder = imageHolder;
        this.url = url;

    }
}
