package itt.matthew.houseshare.Adapters_CustomViews;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import itt.matthew.houseshare.Fragments.CostOverviewFragment;
import itt.matthew.houseshare.Fragments.TaskOverviewFragment;
import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.Models.Cost;
import itt.matthew.houseshare.Models.CostInstance;
import itt.matthew.houseshare.Models.House;
import itt.matthew.houseshare.Models.Task;
import itt.matthew.houseshare.Models.TaskInstance;
import itt.matthew.houseshare.R;

/**
 * Created by Matth on 23/04/2016.
 */
public class OverviewTaskAdapter extends RecyclerView.Adapter<OverviewTaskAdapter.OverViewholder> {


    private House house;
    private Account current;
    private Context ctx;
    private Task task;
    private TaskOverviewFragment.OnItemTouchListener listener;

    public OverviewTaskAdapter(House house, Account current, Context ctx, Task task, TaskOverviewFragment.OnItemTouchListener listener) {

        this.house = house;
        this.current = current;
        this.ctx = ctx;
        this.task = task;
        this.listener = listener;

    }


    @Override
    public OverViewholder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_instance, parent, false); //Inflating the layout

        OverViewholder vhItem = new OverViewholder(v); //Creating ViewHolder and passing the object of type view

        return vhItem; // Returning the created object
    }


    private Boolean afterDate(TaskInstance instance){

        Calendar c = Calendar.getInstance();
        return c.after(instance.getDate());

    }

    @Override
    public void onBindViewHolder(OverViewholder holder, int position) {

        DateFormat formatter = DateFormat.getDateInstance(DateFormat.SHORT);

        for (int i =0; i < house.getMembers().size(); i++){
            if (house.getMembers().get(i).getFacebookID().equals(task.getTaskInstances().get(position).getAccount())){
                holder.person.setText(house.getMembers().get(i).getName());
            }
        }
        holder.date.setText("Date: " + formatter.format(task.getTaskInstances().get(position).getDate().getTime()));

        Boolean paid = true;
        Boolean dateExceeded = afterDate(task.getTaskInstances().get(position));


        paid = task.getTaskInstances().get(position).getPaid();

        if (paid){
            holder.status.setBackgroundColor(ctx.getResources().getColor(R.color.md_green_400));
            holder.statusType.setText(R.string.done);
        }
        else if (!paid && dateExceeded) {
            holder.status.setBackgroundColor(ctx.getResources().getColor(R.color.md_red_400));
            holder.statusType.setText(R.string.overdue);
        }
        else{
            holder.status.setBackgroundColor(ctx.getResources().getColor(R.color.md_amber_400));
            holder.statusType.setText(R.string.notDone);
        }

        holder.bind(holder.card, position, listener);
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
    public int getItemCount() {
        return task.getTaskInstances().size();
    }

    public static class OverViewholder extends RecyclerView.ViewHolder {

        TextView person;
        ImageView status;
        TextView statusType;
        TextView date;
        CardView card;


        private void bind(final CardView item, final int position, final TaskOverviewFragment.OnItemTouchListener listener) {

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onCardViewTouch(item, position);
                }
            });
        }


        public OverViewholder(View itemView) {
            super(itemView);

            person = (TextView) itemView.findViewById(R.id.task_owner);
            statusType = (TextView) itemView.findViewById(R.id.task_status);
            status = (ImageView) itemView.findViewById(R.id.task_color);
            date = (TextView) itemView.findViewById(R.id.task_date);
            card = (CardView) itemView.findViewById(R.id.task_cv);



        }


    }
}
