package ru.nwts.wherewe.aux_ui;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ru.nwts.wherewe.R;
import ru.nwts.wherewe.TODOApplication;
import ru.nwts.wherewe.adapter.adapterSmallModel;
import ru.nwts.wherewe.database.DBHelper;
import ru.nwts.wherewe.model.SmallModel;
import ru.nwts.wherewe.util.DialogFragmentOneItem;
import ru.nwts.wherewe.util.DialogFragmentYesNo;

import static android.content.Intent.FLAG_RECEIVER_FOREGROUND;
import static android.os.Build.ID;
import static android.provider.Contacts.SettingsColumns.KEY;
import static ru.nwts.wherewe.database.DBConstant.KEY_ID;
import static ru.nwts.wherewe.database.DBConstant.KEY_LATTITUDE;
import static ru.nwts.wherewe.database.DBConstant.KEY_LONGTITUDE;
import static ru.nwts.wherewe.database.DBConstant.KEY_MODE;
import static ru.nwts.wherewe.database.DBConstant.KEY_NAME;

public class RecyclerViews extends AppCompatActivity
        implements DialogFragmentOneItem.EditingTaskListener,DialogFragmentYesNo.DialogFragmentYesNoListener{

    //LOG
    public static final String TAG = "MyLogs";

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
   // private RecyclerView.Adapter adapter;
    private adapterSmallModel adapter;

    private List<SmallModel> smallModels;
    private final String ACTION_EDIT_ABONENT = "ru.nwts.wherewe.edit";
    private final int ACTION_DELETE = 0;
    private final int ACTION_EDIT_NAME = 1;
    public DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_views);

        dbHelper = TODOApplication.getInstance().dbHelper;

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        smallModels = dbHelper.getListSmallModel();
        loadRecyclerViewItem();
    }

    private void loadRecyclerViewItem(){

        adapter = new adapterSmallModel(smallModels,this);
        recyclerView.setAdapter(adapter);

    }

    private void sendBroadCastEditAbonent(int id, String name, int action){
        Intent intent = new Intent();
        intent.setAction(ACTION_EDIT_ABONENT);
        intent.putExtra(KEY_ID, id);
        intent.putExtra(KEY_NAME,name);
        intent.putExtra(KEY_MODE,action);
        //intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        Log.d(TAG,"sendMessage:sendBroadCastEditAbonent:send from Recycler");
        sendBroadcast(intent);
    }

    @Override
    public void onTaskEdited(SmallModel updatedTask, int position, int id) {
        Log.d(TAG,"onDialogPositiveClick:onTaskEdited:getId()"+id+" position:"+position);
        if (dbHelper.dbUpdateName(id,updatedTask.getName())==1){
            Toast.makeText(this,R.string.update_done, Toast.LENGTH_SHORT).show();
            adapter.setNotifyDataChange(updatedTask.getName(),position);
            sendBroadCastEditAbonent(id,updatedTask.getName(),ACTION_EDIT_NAME);
        }else {
            Toast.makeText(this,R.string.update_error, Toast.LENGTH_SHORT).show();
        }
        Log.v(TAG,"adapterSmallModel: email updated: ="+updatedTask.getEmail());
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int id, int position) {
        Log.d(TAG,"onDialogPositiveClick:position="+position+"; id="+id + " ;item count="+adapter.getItemCount());
        //Yes delete record
        if (id > 1){
            if (dbHelper.dbDeleteUser(id)>0){
                Toast.makeText(this,R.string.delete_completed, Toast.LENGTH_SHORT).show();
                adapter.setNotifyItemRemoved(position);
                sendBroadCastEditAbonent(id,"deleted",ACTION_DELETE);
            }else {
                Toast.makeText(this,R.string.delete_attention, Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this,R.string.delete_attention, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        //none todo`
    }
}
