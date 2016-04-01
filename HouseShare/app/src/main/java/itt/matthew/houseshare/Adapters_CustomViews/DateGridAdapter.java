package itt.matthew.houseshare.Adapters_CustomViews;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.Models.Cost;
import itt.matthew.houseshare.Models.House;
import itt.matthew.houseshare.R;

/**
 * Created by Matthew on 30/03/2016.
 */
public class DateGridAdapter extends RecyclerView.Adapter<DateGridAdapter.DateViewHolder> {




    private House currentHouse;
    private Account current;
    private Cost cost;
    private Context ctx;
    private Double amount;

    public DateGridAdapter (House currentHouse, Account current, Cost cost, Context ctx){

        this.currentHouse = currentHouse;
        this.current = current;
        this.cost = cost;
        this.ctx = ctx;

        for(int i = 0; i < cost.getSplit().size(); i++){
            if(cost.getSplit().get(i).getUserFacebookID().equals(current.getFacebookID())){
                amount = cost.getSplit().get(i).getAmount();
            }
        }
    }

    @Override
    public DateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.date_instance,parent,false); //Inflating the layout

        return new DateViewHolder(v);

    }

    @Override
    public void onBindViewHolder(DateViewHolder holder, int position) {

        DateFormat formatter = DateFormat.getDateInstance(DateFormat.SHORT);
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.ITALY);


        holder.date.setText(formatter.format(cost.getIntervals().get(position).getDate().getTime()));
        holder.amount.setText(format.format(amount));


//
//        if(cost.getIntervals().get(position).isPaid()){
//
//            holder.color.setBackgroundColor(ctx.getResources().getColor(R.color.md_green_400));
//            holder.status.setText("Paid");
//
//        }else{
//
//            holder.color.setBackgroundColor(ctx.getResources().getColor(R.color.md_red_400));
//            holder.status.setText("Unpaid");
//        }

    }

    @Override
    public int getItemCount() {
        return cost.getIntervals().size();
    }

    public class DateViewHolder extends RecyclerView.ViewHolder {

        TextView date;
        TextView status;
        TextView amount;
        ImageView color;


        public DateViewHolder(View itemView) {
            super(itemView);

            date = (TextView) itemView.findViewById(R.id.date_date);
            status = (TextView) itemView.findViewById(R.id.date_status);
            color = (ImageView) itemView.findViewById(R.id.date_color);
            amount = (TextView) itemView.findViewById(R.id.date_amount);

        }
    }
}
