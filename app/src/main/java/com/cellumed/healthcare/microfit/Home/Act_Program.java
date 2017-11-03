package com.cellumed.healthcare.microfit.Home;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.cellumed.healthcare.microfit.Bluetooth.BTConnectActivity;
import com.cellumed.healthcare.microfit.DAO.DAO_Program;
import com.cellumed.healthcare.microfit.DataBase.DBQuery;
import com.cellumed.healthcare.microfit.R;

import java.util.ArrayList;

public class Act_Program extends BTConnectActivity {

    CustomListAdapter2 customListAdapter;
    private Context mContext;
    private View decorView;
    private int uiOption;
    private boolean type;

    private ArrayList<DAO_Program> programList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_program);
        mContext = this;
        setCustomActionbar();

        final DBQuery dbQuery = new DBQuery(mContext);
        programList = dbQuery.getALLProgramActivated();

        ListView list = (ListView) findViewById(R.id.list);
        customListAdapter = new CustomListAdapter2(programList);
        list.setAdapter(customListAdapter);

    }

    @Override
    protected void connectedDevice() {}

    @Override
    protected void dataAvailableCheck(String data) {}

    private void setCustomActionbar() {
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        View mCustomView = LayoutInflater.from(this).inflate(R.layout.layout_actionbar, null);
        actionBar.setCustomView(mCustomView);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff6669")));
        ((ImageView) findViewById(R.id.custom_topImage)).setBackgroundDrawable(getResources().getDrawable(R.drawable.toptitle_eprog));


        Toolbar parent = (Toolbar) mCustomView.getParent();
        parent.setContentInsetsAbsolute(0, 0);

        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(mCustomView, params);

        ImageButton btn = (ImageButton) findViewById(R.id.custom_back_btn);
        btn.setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    }
                }
        );
    }
}
