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
import itt.matthew.houseshare.Models.Task;
import itt.matthew.houseshare.R;

public class RVTasksAdapter extends RecyclerView.Adapter<RVTasksAdapter.PersonViewHolder>  {


    private House persons;
    private Account account;
    private boolean personal;
    private boolean archive;
    private ArrayList<Task> personalTask;
    private ArrayList<Task> tasks;
    private FinanceFragment.OnItemTouchListener touchListener;

    public static class PersonViewHolder extends RecyclerView.ViewHolder {
        private CardView cv;
        private TextView TaskCategory;
        private TextView TaskDate;
        private TextView TaskInterval;
        private TextView TaskAmount;
        private ImageView TaskColor;



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
            TaskCategory = (TextView)itemView.findViewById(R.id.category_name);
            TaskDate = (TextView)itemView.findViewById(R.id.cost_dates);
            TaskInterval = (TextView)itemView.findViewById(R.id.costs_interval);
            TaskAmount = (TextView) itemView.findViewById(R.id.cost_amount);
            TaskColor = (ImageView)itemView.findViewById(R.id.color_code);



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


    public RVTasksAdapter(House persons, Account account, boolean personal, FinanceFragment.OnItemTouchListener touchListener){
        this.persons = new House(persons);
        this.account = account;
        this.personal = personal;
        this.tasks = persons.getTask();
        this.touchListener = touchListener;



        if(personal){
            personalTask = new ArrayList<>();


            for (int i = 0; i < tasks.size(); i++){
                for(int j = 0; j < tasks.get(i).getUsers().size(); j++){
                    if (account.getFacebookID().equals(tasks.get(i).getUsers().get(j))){
                        personalTask.add(tasks.get(i));
                    }
                }
            }
        }

    }




    @Override
    public int getItemCount() {

        if (personal ) {
            return personalTask.size();
        } else
            return tasks.size();
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


            personViewHolder.TaskCategory.setText(tasks.get(i).getArea().getName());
            personViewHolder.TaskCategory.setTextColor(tasks.get(i).getArea().getColor());
            personViewHolder.TaskDate.setText(formatter.format(tasks.get(i).getStartDate().getTime()) + " to " + formatter.format(tasks.get(i).getEndDate().getTime()));
            personViewHolder.TaskInterval.setText("Next Due Date: " + formatter.format(getNextDate(tasks.get(i)).getTime()));
            personViewHolder.TaskAmount.setVisibility(View.GONE);
            personViewHolder.TaskColor.setBackgroundColor(tasks.get(i).getArea().getColor());


            personViewHolder.bind(personViewHolder.cv, i, touchListener);
        }else{


            DateFormat formatter = DateFormat.getDateInstance(DateFormat.SHORT);
            NumberFormat format = NumberFormat.getCurrencyInstance(Locale.GERMANY);


            personViewHolder.TaskCategory.setText(personalTask.get(i).getArea().getName());
            personViewHolder.TaskCategory.setTextColor(personalTask.get(i).getArea().getColor());
            personViewHolder.TaskDate.setText(formatter.format(personalTask.get(i).getStartDate().getTime()) + " to " + formatter.format(personalTask.get(i).getEndDate().getTime()));
            personViewHolder.TaskInterval.setText("Next Due Date: " + formatter.format(getNextDate(personalTask.get(i)).getTime()));
            personViewHolder.TaskAmount.setVisibility(View.GONE);
            personViewHolder.TaskColor.setBackgroundColor(personalTask.get(i).getArea().getColor());

            personViewHolder.bind(personViewHolder.cv, i, touchListener);

        }
    }

    private Date getNextDate(Task t){

        ArrayList<Integer> results = new ArrayList<>();


        for (int i = 0; i <t.getTaskInstances().size(); i++){
            if(t.getTaskInstances().get(i).getDate() == Calendar.getInstance()){
                return t.getTaskInstances().get(i).getDate().getTime();
            }else{
                results.add(t.daysBetween(t.getTaskInstances().get(i).getDate().getTime(), Calendar.getInstance().getTime()));
            }
        }

        int small = 0;
        if (results.size() == 0) {

            Calendar d = Calendar.getInstance();
            d.add(Calendar.DATE, t.getInterval());


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
            return t.getTaskInstances().get(index).getDate().getTime();

        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }



}


