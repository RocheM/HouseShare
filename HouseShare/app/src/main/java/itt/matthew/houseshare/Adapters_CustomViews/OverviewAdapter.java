package itt.matthew.houseshare.Adapters_CustomViews;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import itt.matthew.houseshare.Fragments.CostOverviewFragment;
import itt.matthew.houseshare.Fragments.CostSplitFragment;
import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.Models.Cost;
import itt.matthew.houseshare.Models.House;
import itt.matthew.houseshare.R;

/**
 * Created by Matthew on 31/03/2016.
 */
public class OverviewAdapter extends RecyclerView.Adapter<OverviewAdapter.OverViewholder> {


    private House house;
    private Account current;
    private Context ctx;
    private Cost cost;
    private CostOverviewFragment.OnItemTouchListener listener;

    public OverviewAdapter(House house, Account current, Context ctx, Cost cost, CostOverviewFragment.OnItemTouchListener listener){

        this.house = house;
        this.current = current;
        this.ctx = ctx;
        this.cost = cost;
        this.listener = listener;

    }


    @Override
    public OverViewholder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.overview_item, parent, false); //Inflating the layout

        OverViewholder vhItem = new OverViewholder(v); //Creating ViewHolder and passing the object of type view

        return vhItem; // Returning the created object
    }
    @Override
    public void onBindViewHolder(OverViewholder holder, int position) {

        DateFormat formatter = DateFormat.getDateInstance(DateFormat.SHORT);

        holder.name.setText(cost.getSplit().get(position).getName());
        holder.date.setText("Next Payment:" + formatter.format(getNextDate(cost).getTime()));
        Picasso.with(ctx).load("http://graph.facebook.com/"+cost.getSplit().get(position).getUserFacebookID()+"/picture?type=large").placeholder(R.mipmap.placeholder_person).into(holder.picture);
        holder.bind(holder.card, position, listener);
    }



    private Date getNextDate(Cost c){

        ArrayList<Integer> results = new ArrayList<>();

        for (int i = 0; i <c.getIntervals().size(); i++){
            if(c.getIntervals().get(i).getDate() == Calendar.getInstance()){
                return c.getIntervals().get(i).getDate().getTime();
            }else{
                results.add(c.daysBetween(c.getIntervals().get(i).getDate().getTime(), Calendar.getInstance().getTime()));
            }
        }

        int small = results.get(0);
        int index = 0;

        for (int i = 0; i < results.size(); i++) {
            if (results.get(i) < small) {
                small = results.get(i);
                index = i;
            }
        }


        return c.getIntervals().get(index).getDate().getTime();
    }


    @Override
    public int getItemCount() {
        return cost.getSplit().size();
    }

    public static class OverViewholder extends RecyclerView.ViewHolder {

        TextView name;
        TextView date;
        CardView card;
        CircleImageView picture;


        private void bind(final CardView item, final int position,  final CostOverviewFragment.OnItemTouchListener  listener) {

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onCardViewTouch(item, position);
                }
            });
        }


        public OverViewholder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.overview_profile_personName);
            date = (TextView) itemView.findViewById(R.id.overview_profile_date);
            picture = (CircleImageView) itemView.findViewById(R.id.overview_profile_image);
            card = (CardView) itemView.findViewById(R.id.overview_cv);


        }


    }
}
