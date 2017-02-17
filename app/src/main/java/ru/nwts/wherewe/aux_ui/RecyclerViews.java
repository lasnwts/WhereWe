package ru.nwts.wherewe.aux_ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ru.nwts.wherewe.R;
import ru.nwts.wherewe.TODOApplication;
import ru.nwts.wherewe.adapter.adapterSmallModel;
import ru.nwts.wherewe.database.DBHelper;
import ru.nwts.wherewe.model.SmallModel;
import ru.nwts.wherewe.util.DialogFragmentOneItem;

public class RecyclerViews extends AppCompatActivity implements DialogFragmentOneItem.EditingTaskListener{

    //LOG
    public static final String TAG = "MyLogs";

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;

    private List<SmallModel> smallModels;

    public DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_views);

        //dbHelper = new DBHelper(this);
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

    @Override
    public void onTaskEdited(SmallModel updatedTask) {
        Log.v(TAG,"adapterSmallModel: email updated: ="+updatedTask.getEmail());
    }
}
