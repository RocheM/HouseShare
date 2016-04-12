package itt.matthew.houseshare.Adapters_CustomViews;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.w3c.dom.Text;

import java.security.AccessControlContext;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import itt.matthew.houseshare.Activities.CostDetails;
import itt.matthew.houseshare.Events.LongPressEvent;
import itt.matthew.houseshare.Fragments.CostOverviewFragment;
import itt.matthew.houseshare.Fragments.FinanceFragment;
import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.Models.Cost;
import itt.matthew.houseshare.Models.CostSplit;
import itt.matthew.houseshare.Models.House;
import itt.matthew.houseshare.R;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.PersonViewHolder>  {


    private House persons;
    private Account account;
    private boolean personal;
    private boolean archive;
    private ArrayList<Cost> personalCosts;
    private ArrayList<Cost> costs;
    private FinanceFragment.OnItemTouchListener touchListener;

    public static class PersonViewHolder extends RecyclerView.ViewHolder {
        private CardView cv;
        private TextView CostCategory;
        private TextView CostDate;
        private TextView CostInterval;
        private TextView CostAmount;
        private ImageView CostColor;



        private void bind(final View item, final int position,  final FinanceFragment.OnItemTouchListener  listener) {

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemViewTouch(item, position);
                }
            });

            item.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onItemHeld(item, position);
                    return true;
                }
            });
        }




        PersonViewHolder(View itemView) {
            super(itemView);

            cv = (CardView)itemView.findViewById(R.id.cv);
            CostCategory = (TextView)itemView.findViewById(R.id.category_name);
            CostDate = (TextView)itemView.findViewById(R.id.cost_dates);
            CostInterval = (TextView)itemView.findViewById(R.id.costs_interval);
            CostAmount = (TextView) itemView.findViewById(R.id.cost_amount);
            CostColor = (ImageView)itemView.findViewById(R.id.color_code);



//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    Intent i = new Intent(v.getContext(), CostDetails.class);
//                    Bundle b = new Bundle();
//                    b.putParcelable("house", );
//                    b.putParcelable("account", acc);
//                    b.putInt("cost", currentItem);
//                    i.putExtra("extra", b);
//                    v.getContext().startActivity(i);
//                }
//            });
//
//            itemView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    EventBus.getDefault().post(new LongPressEvent(acc, house.getCost().get(currentItem), currentItem));
//                    return true;
//                }
//            });
        }
    }


    public RVAdapter(House persons, Account account, boolean personal, FinanceFragment.OnItemTouchListener touchListener){
        this.persons = new House(persons);
        this.account = account;
        this.personal = personal;
        this.costs = persons.getCost();
        this.touchListener = touchListener;



        if(personal){
            personalCosts = new ArrayList<>();


            for (int i = 0; i < costs.size(); i++){
                ArrayList<CostSplit> costSplits = costs.get(i).getSplit();
                for(int j = 0; j < costSplits.size(); j++){
                    if (account.getFacebookID().equals(costSplits.get(j).getUserFacebookID())){
                        personalCosts.add(costs.get(i));
                    }
                }
            }
        }

    }




    @Override
    public int getItemCount() {

        if (personal ) {
            return personalCosts.size();
        } else
            return costs.size();
    }




    @Override
    public PersonViewHolder onCreateViewHolder(final ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_item, viewGroup, false);
        return new PersonViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, int i) {


        if (!personal){

            DateFormat formatter = DateFormat.getDateInstance(DateFormat.SHORT);
            NumberFormat format = NumberFormat.getCurrencyInstance(Locale.GERMANY);


            personViewHolder.CostCategory.setText(costs.get(i).getCategory().getName());
            personViewHolder.CostCategory.setTextColor(costs.get(i).getCategory().getColor());
            personViewHolder.CostDate.setText(formatter.format(costs.get(i).getStartDate().getTime()) + " to " + formatter.format(costs.get(i).getEndDate().getTime()));
            personViewHolder.CostInterval.setText("Next payment: " + formatter.format(getNextDate(costs.get(i)).getTime()));
            personViewHolder.CostAmount.setText(format.format(costs.get(i).getAmount()));
            personViewHolder.CostColor.setBackgroundColor(costs.get(i).getCategory().getColor());


            personViewHolder.bind(personViewHolder.cv, i, touchListener);
        }else{


            DateFormat formatter = DateFormat.getDateInstance(DateFormat.SHORT);
            NumberFormat format = NumberFormat.getCurrencyInstance(Locale.GERMANY);


            personViewHolder.CostCategory.setText(personalCosts.get(i).getCategory().getName());
            personViewHolder.CostCategory.setTextColor(personalCosts.get(i).getCategory().getColor());
            personViewHolder.CostDate.setText(formatter.format(personalCosts.get(i).getStartDate().getTime()) + " to " + formatter.format(personalCosts.get(i).getEndDate().getTime()));
            personViewHolder.CostInterval.setText("Next payment: " + formatter.format(getNextDate(personalCosts.get(i)).getTime()));
            personViewHolder.CostAmount.setText(format.format(personalCosts.get(i).getAmount()));
            personViewHolder.CostColor.setBackgroundColor(personalCosts.get(i).getCategory().getColor());

            personViewHolder.bind(personViewHolder.cv, i, touchListener);

        }
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

        int small = 0;
        if (results.size() == 0) {

            Calendar d = Calendar.getInstance();
            d.add(Calendar.DATE, c.getInterval());


            return d.getTime();

        }
        else {

            small = results.get(0);
            int index = 0;

            for (int i = 0; i < results.size(); i++) {
                if (results.get(i) < small) {
                    small = results.get(i);
                    index = i;
                }
            }
            return c.getIntervals().get(index).getDate().getTime();

        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }



}


