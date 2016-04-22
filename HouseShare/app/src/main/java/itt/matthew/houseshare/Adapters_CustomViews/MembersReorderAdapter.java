package itt.matthew.houseshare.Adapters_CustomViews;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.thesurix.gesturerecycler.GestureAdapter;
import com.thesurix.gesturerecycler.GestureViewHolder;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;
import itt.matthew.houseshare.Fragments.membersFragment;
import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.Models.Cost;
import itt.matthew.houseshare.Models.House;
import itt.matthew.houseshare.R;

/**
 * Created by Matthew on 13/04/2016.
 */
public class MembersReorderAdapter extends RecyclerView.Adapter<MembersReorderAdapter.membersHolder>
        implements ItemTouchHelperAdapter {
    private Context currentContext;
    private ArrayList<Account> members;

    public ArrayList<Account> getMembers(){
        return members;
    }

    public interface OnDragStartListener {
        void onDragStarted(RecyclerView.ViewHolder viewHolder);
    }

    private final OnDragStartListener mDragStartListener;

    public MembersReorderAdapter (ArrayList<Account> members, Context currentContext, OnDragStartListener mDragStartListener){
        this.currentContext = currentContext;
        this.members = members;
        this.mDragStartListener = mDragStartListener;
    }


    @Override
    public MembersReorderAdapter.membersHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.house_member_card, viewGroup, false);
        return new membersHolder(v);
    }

    @Override
    public void onBindViewHolder(final membersHolder holder, int position) {

        holder.profileName.setText(members.get(position).getName());
        Picasso.with(currentContext).load("http://graph.facebook.com/" + members.get(position).getFacebookID() + "/picture?type=large").placeholder(R.mipmap.placeholder_person).into(holder.profilePic);
        holder.handle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onDragStarted(holder);
                }
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {


        Account prev = members.remove(fromPosition);
        members.add(toPosition > fromPosition ? toPosition - 1 : toPosition, prev);
        notifyItemMoved(fromPosition, toPosition);

        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        members.remove(position);
        notifyItemRemoved(position);
    }


    public static class membersHolder extends  GestureViewHolder{


        CardView cardView;
        CircleImageView profilePic;
        ImageView handle;
        TextView profileName;
        Account currentItem;


        public membersHolder(View itemView) {
            super(itemView);


            cardView = (CardView)itemView.findViewById(R.id.house_settings_cv);
            profilePic = (CircleImageView)itemView.findViewById(R.id.house_profile_image);
            profileName = (TextView)itemView.findViewById(R.id.house_profile_name);
            handle = (ImageView) itemView.findViewById(R.id.handle);
        }

        @Override
        public boolean canDrag() {
            return false;
        }

        @Override
        public boolean canSwipe() {
            return true;
        }

        @Override
        public void onItemSelect() {
            cardView.setCardBackgroundColor(Color.LTGRAY);
            Snackbar.make(cardView.getRootView(), "position", Snackbar.LENGTH_SHORT).show();

        }

        @Override
        public void onItemClear() {
            cardView.setCardBackgroundColor(0);
        }
    }
}
