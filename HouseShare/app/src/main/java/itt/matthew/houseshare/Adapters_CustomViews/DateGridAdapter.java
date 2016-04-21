package itt.matthew.houseshare.Adapters_CustomViews;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import itt.matthew.houseshare.Fragments.CostOverviewFragment;
import itt.matthew.houseshare.Fragments.PersonalCostOverview;
import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.Models.Cost;
import itt.matthew.houseshare.Models.CostInstance;
import itt.matthew.houseshare.Models.House;
import itt.matthew.houseshare.R;

/**
 * Created by Matthew on 30/03/2016.
 */
public class DateGridAdapter extends RecyclerView.Adapter<DateGridAdapter.DateViewHolder> {




    private House currentHouse;
    private Account current;
    private Account selected;
    private Cost cost;
    private Context ctx;
    private Double amount;
    private PersonalCostOverview.onItemInteractionInterface listener;

    public DateGridAdapter (House currentHouse, Account current, Account selected, Cost cost, Context ctx, PersonalCostOverview.onItemInteractionInterface listener){

        this.currentHouse = currentHouse;
        this.current = current;
        this.selected = selected;
        this.cost = cost;
        this.ctx = ctx;
        this.listener = listener;

        for(int i = 0; i < cost.getSplit().size(); i++){
            if(cost.getSplit().get(i).getUserFacebookID().equals(selected.getFacebookID())){
                amount = cost.getSplit().get(i).getAmount();
            }
        }

    }

    @Override
    public DateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.date_instance,parent,false); //Inflating the layout

        return new DateViewHolder(v);

    }

    private Boolean afterDate(CostInstance instance){

        Calendar c = Calendar.getInstance();
        return c.after(instance.getDate());

    }

    @Override
    public void onBindViewHolder(DateViewHolder holder, int position) {

        DateFormat formatter = DateFormat.getDateInstance(DateFormat.SHORT);
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.ITALY);

        holder.date.setText(formatter.format(cost.getIntervals().get(position).getDate().getTime()));
        holder.amount.setText(format.format(amount));
        Boolean paid = true;
        Boolean dateExceeded = afterDate(cost.getIntervals().get(position));


        for (int i = 0; i < cost.getIntervals().get(position).paidList().size(); i++){
            if (cost.getIntervals().get(position).paidList().get(i).first.equals(selected.getFacebookID())){
                paid = cost.getIntervals().get(position).paidList().get(i).second;
            }
        }

        if (paid){
            holder.color.setBackgroundColor(ctx.getResources().getColor(R.color.md_green_400));
            holder.status.setText(R.string.paid);
        }
        else if (!paid && dateExceeded) {
            holder.color.setBackgroundColor(ctx.getResources().getColor(R.color.md_red_400));
            holder.status.setText(R.string.overdue);
        }
        else{
            holder.color.setBackgroundColor(ctx.getResources().getColor(R.color.md_amber_400));
            holder.status.setText(R.string.unpaid);
        }

        holder.bind(holder.card, position, listener);

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
        CardView card;

        private void bind(final CardView item, final int position,  final PersonalCostOverview.onItemInteractionInterface listener) {

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onCardViewTouch(item, position, current);
                }
            });
        }

        public DateViewHolder(View itemView) {
            super(itemView);

            date = (TextView) itemView.findViewById(R.id.date_date);
            status = (TextView) itemView.findViewById(R.id.date_status);
            color = (ImageView) itemView.findViewById(R.id.date_color);
            amount = (TextView) itemView.findViewById(R.id.date_amount);
            card = (CardView) itemView.findViewById(R.id.date_cv);
        }
    }
}
