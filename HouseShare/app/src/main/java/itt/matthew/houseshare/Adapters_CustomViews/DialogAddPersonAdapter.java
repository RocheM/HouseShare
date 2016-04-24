package itt.matthew.houseshare.Adapters_CustomViews;

import android.app.Activity;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import itt.matthew.houseshare.Fragments.CostSplitFragment;
import itt.matthew.houseshare.Models.Account;
import itt.matthew.houseshare.Models.CostSplit;
import itt.matthew.houseshare.R;

public class DialogAddPersonAdapter extends BaseAdapter implements MaterialDialog.ListCallback {

    private ArrayList<Account> members;
    private final LayoutInflater mInflater;

    public DialogAddPersonAdapter(Activity context, ArrayList<Account> members){

        mInflater  = LayoutInflater.from(context);
        this.members = members;

    }

    public void setMembers(ArrayList<Account> members){
        this.members = members;
    }

    @Override
    public int getCount() {
        return members.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public Account getItemAtPosition(int position){
        return members.get(position);
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View v = view;
        ImageView picture;
        TextView name;
        CardView cardView;
        SeekBar seekBar;

        if (v == null) {
            v = mInflater.inflate(R.layout.dialog_amount, viewGroup, false);
        }

        cardView = (CardView) v.findViewById(R.id.account_cv);
        cardView.setPadding(10,10,10,10);
        seekBar = (SeekBar) v.findViewById(R.id.card_profile_seekbar);
        seekBar.setVisibility(View.GONE);
        picture = (ImageView) v.findViewById(R.id.card_profile_image);
        name = (TextView) v.findViewById(R.id.card_profile_name);
        name.setText(members.get(i).getName());
        name.setPadding(0, 80, 0, 0);


        Picasso.with(mInflater.getContext()).load("http://graph.facebook.com/" + members.get(i).getFacebookID() + "/picture?type=large").into(picture);
        return v;

    }

    @Override
    public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {


    }
}
