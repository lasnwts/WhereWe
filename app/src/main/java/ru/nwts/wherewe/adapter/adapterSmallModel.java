package ru.nwts.wherewe.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;

import java.util.List;

import ru.nwts.wherewe.R;
import ru.nwts.wherewe.database.DataManager;
import ru.nwts.wherewe.model.SmallModel;
import ru.nwts.wherewe.fragments.dialog.DialogFragmentOneItem;
import ru.nwts.wherewe.fragments.dialog.DialogFragmentYesNo;
import ru.nwts.wherewe.settings.Constants;

/**
 * Created by пользователь on 16.02.2017.
 */

public class adapterSmallModel extends RecyclerView.Adapter<adapterSmallModel.ViewHolder> {

    FragmentManager manager;

    //LOG
    public static final String TAG = "MyLogs";

    private List<SmallModel> smallModels;
    private Context context;
    private adapterClickListener mAdapterClickListener;
    private Activity activity;

    public adapterSmallModel(List<SmallModel> smallModels, Context context,
                             adapterClickListener mAdapterClickListener) {
        this.smallModels = smallModels;
        this.context = context;
        this.mAdapterClickListener = mAdapterClickListener;
    }

    @Override
    public adapterSmallModel.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_item, parent, false);
        context = parent.getContext();
        return new ViewHolder(v,mAdapterClickListener);
    }

    @Override
    public void onBindViewHolder(final adapterSmallModel.ViewHolder holder, final int position) {
        final SmallModel smallModel = smallModels.get(position);
        holder.textViewHead.setText(smallModel.getName());
        holder.textViewDesc.setText(smallModel.getEmail());
        if (smallModel.getImgView() != null && !TextUtils.isEmpty(smallModel.getImgView())) {
            picassoGetImages(holder, Uri.parse(smallModel.getImgView()));
        }
        //View One element
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentActivity activity = (FragmentActivity)(context);
                FragmentManager fm = activity.getSupportFragmentManager();
                Log.v(TAG, "adapterSmallModel:item:"+"One setOnClickListener:"+ holder.textViewHead.getText().toString());
                holder.mAdapterClickListener.adapterOnClickListener(position);
//                DialogFragment dialogFragmentOneItem = DialogFragmentOneItem.newInstance(smallModels.get(position),position);
//                dialogFragmentOneItem.show(fm,"NewW");
            }
        });


        holder.textViewOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(context, holder.relativeLayout);
                popup.inflate(R.menu.options_menu);
                FragmentActivity activity = (FragmentActivity)(context);
                final FragmentManager fm = activity.getSupportFragmentManager();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu1:
                                Log.v(TAG, "adapterSmallModel:item:"+item+"smallModel.getName():"+ holder.textViewHead.getText().toString());
                                Log.v(TAG, "adapterSmallModel:item:"+"One setOnClickListener:"+ holder.textViewHead.getText().toString());
                                DialogFragment dialogFragmentOneItem = DialogFragmentOneItem.newInstance(smallModels.get(position),position);
                                dialogFragmentOneItem.show(fm,"NewW");
                                break;
                            case R.id.menu2:
                                DialogFragmentYesNo dialogFragmentYesNo = DialogFragmentYesNo.newInstance(smallModel.getId(), position,context.getResources().getString(R.string.dialog_title_yes_no));
                                Log.v(TAG, "adapterSmallModel:item:"+item);
                                dialogFragmentYesNo.show(fm, "dialog");
                                break;
                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();
            }
        });

        //Menu Options
        holder.relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                PopupMenu popup = new PopupMenu(context, holder.relativeLayout);
                popup.inflate(R.menu.options_menu);
                FragmentActivity activity = (FragmentActivity)(context);
                final FragmentManager fm = activity.getSupportFragmentManager();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu1:
                                Log.v(TAG, "adapterSmallModel:item:"+item+"smallModel.getName():"+ holder.textViewHead.getText().toString());
                                Log.v(TAG, "adapterSmallModel:item:"+"One setOnClickListener:"+ holder.textViewHead.getText().toString());
                                DialogFragment dialogFragmentOneItem = DialogFragmentOneItem.newInstance(smallModels.get(position),position);
                                dialogFragmentOneItem.show(fm,"NewW");
                                break;
                            case R.id.menu2:
                                Log.v(TAG, "adapterSmallModel:item:"+item);
                                DialogFragmentYesNo dialogFragmentYesNo = DialogFragmentYesNo.newInstance(smallModel.getId(),position, context.getResources().getString(R.string.dialog_title_yes_no));
                                Log.v(TAG, "adapterSmallModel:item:"+item);
                                dialogFragmentYesNo.show(fm, "dialog");
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

    }

    @Override
    public int getItemCount() {
        if (smallModels == null) {
            return 0;
        } else {
            return smallModels.size();
        }
    }

    public void setNotifyItemRemoved(int position){
        Log.d(TAG,"onDialogPositiveClick:smallModels:"+smallModels.size());
        Log.d(TAG,"onDialogPositiveClick:adapter:"+getItemCount());
        smallModels.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
    }

    public void setNotifyDataChange(String name,int position){
        smallModels.get(position).setName(name);
        notifyDataSetChanged();
        Log.d(TAG,"onDialogPositiveClick:setNotifyDataChange:position:"+position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewHead;
        public TextView textViewDesc;
        public TextView textViewOption;
        public RelativeLayout relativeLayout;
        public ImageView mImageView;
        adapterClickListener mAdapterClickListener;
      //  public Lay

        public ViewHolder(View itemView, adapterClickListener madapterClickListener) {
            super(itemView);
            textViewHead = (TextView) itemView.findViewById(R.id.textViewHead);
            textViewDesc = (TextView) itemView.findViewById(R.id.textViewDesc);
            textViewOption = (TextView) itemView.findViewById(R.id.textViewOptions);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.recyclerViewLayout);
            mImageView = (ImageView) itemView.findViewById(R.id.imgRecyclerViewAbonent);
            mAdapterClickListener = madapterClickListener;
        }
    }

    /**
     * Picasso
     */
    private void picassoGetImages(final ViewHolder holder, final Uri imageUri) {
        DataManager.getInstance().getPicasso()
                .load(imageUri)
                .centerInside()
                .resize(200, 150)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .error(R.drawable.error_load_image)
                .placeholder(R.drawable.error_load_image)
                .into(holder.mImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d(Constants.TAG, "load from cache");
                    }

                    @Override
                    public void onError() {
                        DataManager.getInstance().getPicasso()
                                .load(imageUri)
                                //  .fit()
                                .centerCrop()
                                .resize(200, 150)
                                .error(R.drawable.error_load_image)
                                .placeholder(R.drawable.error_load_image)
                                .into(holder.mImageView, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        Log.d(Constants.TAG, "Save from network - fetch image");
                                    }

                                    @Override
                                    public void onError() {
                                        Log.d(Constants.TAG, "Could not fetch image");
                                    }
                                });
                    }
                });
    }
}
