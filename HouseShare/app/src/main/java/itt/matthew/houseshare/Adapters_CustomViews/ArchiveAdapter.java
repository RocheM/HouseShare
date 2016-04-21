package itt.matthew.houseshare.Adapters_CustomViews;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import itt.matthew.houseshare.Fragments.ArchivedCostFragment;
import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.Models.Cost;
import itt.matthew.houseshare.Models.House;
import itt.matthew.houseshare.R;

/**
 * Created by Matthew on 09/04/2016.
 */
public class ArchiveAdapter  extends RecyclerView.Adapter<ArchiveAdapter.PersonViewHolder> {


    private House house;
    private Account current;
    private ArrayList<Cost> archived;
    private ArchivedCostFragment.OnItemTouchListener listener;

    public ArchiveAdapter(House house, Account current, ArchivedCostFragment.OnItemTouchListener listener){

        this.house = house;
        this.current = current;
        this.listener = listener;

        archived = house.getArchivedCosts();

    }

    @Override
    public ArchiveAdapter.PersonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        return new ArchiveAdapter.PersonViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ArchiveAdapter.PersonViewHolder holder, int position) {



        DateFormat formatter = DateFormat.getDateInstance(DateFormat.SHORT);
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.GERMANY);


        holder.name.setText(archived.get(position).getCategory().getName());
        holder.date.setText(formatter.format(archived.get(position).getStartDate().getTime()) + " to " + formatter.format(archived.get(position).getEndDate().getTime()));
        holder.intervals.setVisibility(View.GONE);
        holder.amount.setText(format.format(archived.get(position).getAmount()));
        holder.colorCode.setBackgroundColor(archived.get(position).getCategory().getColor());

        holder.bind(holder.cv, position, listener);



    }

    @Override
    public int getItemCount() {
        return house.getArchivedCosts().size();
    }

    public class PersonViewHolder extends RecyclerView.ViewHolder {

        ImageView colorCode;
        TextView name, date, intervals, amount;
        CardView cv;

        public PersonViewHolder(View itemView) {
            super(itemView);

            colorCode = (ImageView) itemView.findViewById(R.id.color_code);
            name = (TextView) itemView.findViewById(R.id.category_name);
            date = (TextView) itemView.findViewById(R.id.cost_dates);
            intervals = (TextView) itemView.findViewById(R.id.costs_interval);
            amount = (TextView) itemView.findViewById(R.id.cost_amount);
            cv = (CardView) itemView.findViewById(R.id.cv);

        }


        private void bind(final View item, final int position,  final ArchivedCostFragment.OnItemTouchListener  listener) {

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onCardViewTouch(item, position);
                }
            });

            item.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onCardViewHeld(item, position);
                    return true;
                }
            });
        }


    }
}


