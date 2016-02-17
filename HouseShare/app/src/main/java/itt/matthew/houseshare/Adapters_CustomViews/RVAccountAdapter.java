package itt.matthew.houseshare.Adapters_CustomViews;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.lang.reflect.Member;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import itt.matthew.houseshare.Fragments.CostSplitFragment;
import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.Models.Cost;
import itt.matthew.houseshare.Models.CostSplit;
import itt.matthew.houseshare.Models.House;
import itt.matthew.houseshare.R;

public class RVAccountAdapter extends RecyclerView.Adapter<RVAccountAdapter.AccountViewHolder>  {


    private House persons;
    private Cost cost;
    private ArrayList<Member> members;
    private RecyclerView recyclerView;
    private Context currentContext;
    private CostSplitFragment.OnItemTouchListener onItemTouchListener;
    private ArrayList<CostSplit> splits;

    public static class AccountViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        CircleImageView profilePic;
        TextView profileName;
        TextView profileAmount;
        SeekBar profileSeekBar;
        Account currentItem;
        MaterialDialog splitDialog;
        Cost currentCost;


        private void bind(final CardView item, final int position, final CostSplitFragment.OnItemTouchListener  listener) {

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onCardViewTouch(item, position);
                }
            });
        }


        AccountViewHolder(final View itemView) {
            super(itemView);


            cardView = (CardView)itemView.findViewById(R.id.account_cv);
            profilePic = (CircleImageView)itemView.findViewById(R.id.card_profile_image);
            profileName = (TextView)itemView.findViewById(R.id.card_profile_name);
            profileAmount = (TextView)itemView.findViewById(R.id.card_profile_amount);
            profileSeekBar = (SeekBar)itemView.findViewById(R.id.card_profile_seekbar);


        }
    }

    public RVAccountAdapter (House persons, Cost cost, ArrayList<CostSplit> splits, CostSplitFragment.OnItemTouchListener listener){
        this.persons = persons;
        this.cost = cost;
        this.splits = splits;
        this.onItemTouchListener = listener;

    }

    @Override
    public int getItemCount() {
        return persons.getMembers().size();
    }

    @Override
    public AccountViewHolder onCreateViewHolder(final ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.member_card, viewGroup, false);
        currentContext = v.getContext();
        return new AccountViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final AccountViewHolder accountViewHolder, int i) {


        double split = 0;
        ArrayList<Account> members = persons.getMembers();
        if (splits == null){
         split = 0;
        }
        else {
            split = splits.get(i).getAmount();
        }

        accountViewHolder.profileName.setText(members.get(i).getName());
        accountViewHolder.profileAmount.setText(Double.toString(split));
        accountViewHolder.bind(accountViewHolder.cardView, accountViewHolder.getAdapterPosition(), onItemTouchListener);
        Picasso.with(currentContext).load("http://graph.facebook.com/"+members.get(i).getFacebookID()+"/picture?type=large").into(accountViewHolder.profilePic);


        accountViewHolder.currentItem = members.get(i);
        accountViewHolder.currentCost = cost;

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }



}


