package itt.matthew.houseshare.Adapters_CustomViews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import itt.matthew.houseshare.R;

/**
 * Created by Matthew on 21/01/2016.
 */
public final class GridAdapter extends BaseAdapter {
    private final List<GridItem> mItems = new ArrayList<GridItem>();
    private final LayoutInflater mInflater;


    public GridAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }


    public GridAdapter(Context context, List<GridItem> itemsToAdd) {
        mInflater = LayoutInflater.from(context);


        for (int i = 0; i < itemsToAdd.size(); i++){
                mItems.add(itemsToAdd.get(i));

        }

    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public GridItem getItem(int i) {
        return mItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mItems.get(i).imageHolder.getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        ImageView picture;
        TextView name;

        if (v == null) {
            v = mInflater.inflate(R.layout.grid_item, viewGroup, false);
            v.setTag(R.id.picture, v.findViewById(R.id.picture));
            v.setTag(R.id.text, v.findViewById(R.id.text));
        }

        picture = (ImageView) v.getTag(R.id.picture);
        name = (TextView) v.getTag(R.id.text);

        GridItem item = getItem(i);

        Picasso.with(v.getContext()).load(item.url).placeholder(R.mipmap.placeholder_person).into(picture);
        name.setText(item.name);

        return v;
    }
}


