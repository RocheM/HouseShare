package itt.matthew.houseshare.Adapters_CustomViews;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Member;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import itt.matthew.houseshare.Activities.HouseSettingsActivity;
import itt.matthew.houseshare.Fragments.CostSplitFragment;
import itt.matthew.houseshare.Fragments.membersFragment;
import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.Models.Cost;
import itt.matthew.houseshare.Models.CostSplit;
import itt.matthew.houseshare.Models.House;
import itt.matthew.houseshare.R;

/**
 * Created by Matthew on 07/04/2016.
 */
public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.MemberViewHolder> {

    private House persons;
    private Cost cost;
    private ArrayList<Member> members;
    private RecyclerView recyclerView;
    private Context currentContext;
    private membersFragment.OnItemTouchListener onItemTouchListener;
    private ArrayList<CostSplit> splits;
    private ArrayList<Integer> moderators;
    private boolean showMods;

    public static class MemberViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        ImageView handle;
        CircleImageView profilePic;
        TextView profileName;
        Account currentItem;


        private void bind(final View item, final int position, final membersFragment.OnItemTouchListener  listener) {

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

            MemberViewHolder(final View itemView) {
                super(itemView);


                cardView = (CardView)itemView.findViewById(R.id.house_settings_cv);
                handle = (ImageView)itemView.findViewById(R.id.handle);
                profilePic = (CircleImageView)itemView.findViewById(R.id.house_profile_image);
                profileName = (TextView)itemView.findViewById(R.id.house_profile_name);

        }
    }

    public MembersAdapter (House persons, ArrayList<Integer> moderators,  membersFragment.OnItemTouchListener listener, boolean showMods ){
        this.persons = persons;
        this.moderators = moderators;
        this.onItemTouchListener = listener;
        this.showMods = showMods;




    }

    @Override
    public int getItemCount() {
        return persons.getMembers().size();
    }

    @Override
    public MemberViewHolder onCreateViewHolder(final ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.house_member_card, viewGroup, false);
        currentContext = v.getContext();
        return new MemberViewHolder(v);
    }



    @Override
    public void onBindViewHolder(final MemberViewHolder memberViewHolder, int i) {

        memberViewHolder.profileName.setText(persons.getMembers().get(i).getName());

        if(moderators.get(i).equals(1) && showMods
                ){
            memberViewHolder.profileName.append("\nAdministrator");
        }

        Picasso.with(currentContext).load("http://graph.facebook.com/" + persons.getMembers().get(i).getFacebookID() + "/picture?type=large").placeholder(R.mipmap.placeholder_person).into(memberViewHolder.profilePic);
        memberViewHolder.bind(memberViewHolder.cardView, i, onItemTouchListener);
        memberViewHolder.handle.setVisibility(View.GONE);

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


}
