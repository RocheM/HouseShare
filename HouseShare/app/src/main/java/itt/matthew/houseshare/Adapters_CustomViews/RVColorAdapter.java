package itt.matthew.houseshare.Adapters_CustomViews;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import itt.matthew.houseshare.Models.Cost;
import itt.matthew.houseshare.Models.House;
import itt.matthew.houseshare.Models.Interval;
import itt.matthew.houseshare.R;

/**
 * Created by Matthew on 08/02/2016.
 */
public class RVColorAdapter extends RecyclerView.Adapter<RVColorAdapter.ColorViewHolder> {

    private ArrayList<Integer> colors;

    public static class ColorViewHolder extends RecyclerView.ViewHolder {

        CircleImageView circleImageView;
        RelativeLayout relativeLayout;
        int currentItem;


        ColorViewHolder(View itemView) {
            super(itemView);

            circleImageView = (CircleImageView) itemView.findViewById(R.id.color_image);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.color_layout);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), Integer.toString(currentItem), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public RVColorAdapter(ArrayList<Integer> colorList){

        this.colors = colorList;
    }


    @Override
    public int getItemCount() {

        return colors.size();
    }

    @Override
    public ColorViewHolder onCreateViewHolder(final ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.color_layout, viewGroup, false);
        return new ColorViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ColorViewHolder colorViewHolder, int i) {

        ColorDrawable drawable = new ColorDrawable();
        drawable.setColor(colors.get(i));
        colorViewHolder.circleImageView.setImageDrawable(drawable);
        colorViewHolder.currentItem = colors.get(i);


    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }



}
