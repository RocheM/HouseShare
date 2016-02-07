package itt.matthew.houseshare.Adapters_CustomViews;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import itt.matthew.houseshare.Models.Cost;
import itt.matthew.houseshare.Models.House;
import itt.matthew.houseshare.R;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.PersonViewHolder>  {


    private House persons;
    private RecyclerView recyclerView;

    public static class PersonViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView CostCategory;
        TextView CostDate;
        TextView CostInterval;
        TextView CostAmount;
        ImageView CostColor;
        Cost currentItem;


        PersonViewHolder(View itemView) {
            super(itemView);

            cv = (CardView)itemView.findViewById(R.id.cv);
            CostCategory = (TextView)itemView.findViewById(R.id.category_name);
            CostDate = (TextView)itemView.findViewById(R.id.cost_dates);
            CostInterval = (TextView)itemView.findViewById(R.id.costs_interval);
            CostAmount = (TextView) itemView.findViewById(R.id.cost_amount);
            CostColor = (ImageView)itemView.findViewById(R.id.color_code);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), currentItem.getCategory().getName(), Toast.LENGTH_LONG).show();

                }
            });
        }
    }

    public RVAdapter(House persons){
        this.persons = persons;
    }


    @Override
    public int getItemCount() {
        return persons.getCost().size();
    }

    @Override
    public PersonViewHolder onCreateViewHolder(final ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_item, viewGroup, false);
        return new PersonViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, int i) {

        ArrayList<Cost> costs = persons.getCost();

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MMM/yyyy");



        personViewHolder.CostCategory.setText(costs.get(i).getCategory().getName());
        personViewHolder.CostCategory.setTextColor(costs.get(i).getCategory().getColor());
        personViewHolder.CostDate.setText(formatter.format(costs.get(i).getStartDate().getTime()) + " to " + formatter.format(costs.get(i).getEndDate().getTime()));
        personViewHolder.CostInterval.setText(Integer.toString(costs.get(i).getInterval()));
        personViewHolder.CostAmount.setText(Double.toString(costs.get(i).getAmount()));
        personViewHolder.CostColor.setBackgroundColor(costs.get(i).getCategory().getColor());
//        personViewHolder.CostColor.setImageResource(R.drawable.ic_person_24dp);

        personViewHolder.currentItem = costs.get(i);

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }



}


