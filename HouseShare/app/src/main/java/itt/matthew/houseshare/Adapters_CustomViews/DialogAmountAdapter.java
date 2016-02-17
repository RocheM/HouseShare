package itt.matthew.houseshare.Adapters_CustomViews;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

import itt.matthew.houseshare.Fragments.CostSplitFragment;
import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.Models.CostSplit;
import itt.matthew.houseshare.Models.House;
import itt.matthew.houseshare.R;

/**
 * Created by Matthew on 16/02/2016.
 */
public class DialogAmountAdapter extends BaseAdapter implements MaterialDialog.ListCallback {

    private ArrayList<CostSplit> splits;
    private ArrayList<Account> members;
    private final LayoutInflater mInflater;
    private int person;
    private CostSplitFragment.onDialogSeekBarChanged seekBarChanged;

    public DialogAmountAdapter(Activity context, CostSplitFragment.onDialogSeekBarChanged seekBarChanged,int person, ArrayList<CostSplit> costSplits, ArrayList<Account> members){

        mInflater  = LayoutInflater.from(context);
        this.person = person;
        this.splits = costSplits;
        this.members = members;
        this.seekBarChanged = seekBarChanged;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public CostSplit getItemAtPosition(int position){
        return splits.get(position);
    }


    private void bind(final SeekBar item, final TextView label, final int position, final CostSplitFragment.onDialogSeekBarChanged  listener) {

        item.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                label.setText(Integer.toString(progress));
                listener.onSeekBarChanged(item, position, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private double calculateAmount(){

        double amount = 0, total =0;


        for (int i = 0; i < splits.size(); i++){
            total += splits.get(i).getAmount();
        }

        amount = total;
        for (int i = 0; i < splits.size(); i++){
            amount -= splits.get(i).getAmount();

        }


        return amount;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View v = view;
        ImageView picture;
        TextView name;
        TextView amount;
        SeekBar seekBar;

        if (v == null) {
            v = mInflater.inflate(R.layout.dialog_amount, viewGroup, false);
        }

        picture = (ImageView) v.findViewById(R.id.card_profile_image);
        name = (TextView) v.findViewById(R.id.card_profile_name);
        name.setText(members.get(person).getName());
        amount = (TextView) v.findViewById(R.id.card_profile_amount);
        amount.setText(Double.toString(splits.get(person).getAmount()));

        seekBar = (SeekBar) v.findViewById(R.id.card_profile_seekbar);
        seekBar.setMax((int) (splits.get(person).getAmount() + 0.5d));
        bind(seekBar, amount, person, seekBarChanged);


        Picasso.with(mInflater.getContext()).load("http://graph.facebook.com/" + splits.get(person).getUserFacebookID() + "/picture?type=large").into(picture);
        return v;

    }

    @Override
    public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {

    }
}