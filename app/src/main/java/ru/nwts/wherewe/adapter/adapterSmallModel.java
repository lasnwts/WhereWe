package ru.nwts.wherewe.adapter;

import android.app.Application;
import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ru.nwts.wherewe.R;
import ru.nwts.wherewe.aux_ui.RecyclerViews;
import ru.nwts.wherewe.model.SmallModel;
import ru.nwts.wherewe.util.DialogFragmentOneItem;

import static ru.nwts.wherewe.R.id.recyclerViewLayout;
import static ru.nwts.wherewe.R.id.textViewHead;

/**
 * Created by пользователь on 16.02.2017.
 */

public class adapterSmallModel extends RecyclerView.Adapter<adapterSmallModel.ViewHolder> {

    //LOG
    public static final String TAG = "MyLogs";

    private List<SmallModel> smallModels;
    private Context context;

    public adapterSmallModel(List<SmallModel> smallModels, Context context) {
        this.smallModels = smallModels;
        this.context = context;
    }

    @Override
    public adapterSmallModel.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final adapterSmallModel.ViewHolder holder, final int position) {
        final SmallModel smallModel = smallModels.get(position);
        holder.textViewHead.setText(smallModel.getName());
        holder.textViewDesc.setText(smallModel.getEmail());
        holder.textViewOption.setText(smallModel.getName());

        //View One element
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentActivity activity = (FragmentActivity)(context);
                FragmentManager fm = activity.getSupportFragmentManager();

                Log.v(TAG, "adapterSmallModel:item:"+"One setOnClickListener:"+ holder.textViewHead.getText().toString());
                DialogFragment dialogFragmentOneItem = DialogFragmentOneItem.newInstance(smallModels.get(position));
                //adapterSmallModel.this
                dialogFragmentOneItem.show(fm,"NewW");
               // dialogFragmentOneItem.show(adapterSmallModel.this.context.getApplicationContext(),"");
            }
        });


        //Menu Options
        holder.relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                PopupMenu popup = new PopupMenu(context, holder.relativeLayout);
                popup.inflate(R.menu.options_menu);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu1:
                                Log.v(TAG, "adapterSmallModel:item:"+item+"smallModel.getName():"+ holder.textViewHead.getText().toString());

                                break;
                            case R.id.menu2:
                                Log.v(TAG, "adapterSmallModel:item:"+item);
                                break;
                            case R.id.menu3:
                                Log.v(TAG, "adapterSmallModel:item:"+item);
                                break;
                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();
                return false;
            }
        });




//        holder.textViewHead.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //creating a popup menu
//                PopupMenu popup = new PopupMenu(context, holder.textViewHead);
//                //inflating menu from xml resource
//                popup.inflate(R.menu.options_menu);
//                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        switch (item.getItemId()) {
//                            case R.id.menu1:
//                                //handle menu1 click
//                                break;
//                            case R.id.menu2:
//                                //handle menu2 click
//                                break;
//                            case R.id.menu3:
//                                //handle menu3 click
//                                break;
//                        }
//                        return false;
//                    }
//                });
//                //displaying the popup
//                popup.show();
//            }
//        });


    }

    @Override
    public int getItemCount() {
        return smallModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewHead;
        public TextView textViewDesc;
        public TextView textViewOption;
        public RelativeLayout relativeLayout;
      //  public Lay

        public ViewHolder(View itemView) {
            super(itemView);
            textViewHead = (TextView) itemView.findViewById(R.id.textViewHead);
            textViewDesc = (TextView) itemView.findViewById(R.id.textViewDesc);
            textViewOption = (TextView) itemView.findViewById(R.id.textViewOptions);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.recyclerViewLayout);
        }
    }
}
