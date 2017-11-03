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
import android.widget.ListView;

import com.cellumed.healthcare.microfit.Bluetooth.BTConnectActivity;
import com.cellumed.healthcare.microfit.DAO.DAO_ExercisePlan;
import com.cellumed.healthcare.microfit.DataBase.DBQuery;
import com.cellumed.healthcare.microfit.R;

import java.util.ArrayList;

/**
 * Created by ikoob on 2016. 6. 2..
 */
public class Act_ExercisePlan extends BTConnectActivity {

    CustomListAdapter customListAdapter;
    private Context mContext;
    private View decorView;
    private int uiOption;
    private boolean type;

    private ArrayList<DAO_ExercisePlan> exerciseList = new ArrayList<DAO_ExercisePlan>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_exercise_plan);
        mContext = this;
        final DBQuery dbQuery = new DBQuery(mContext);
        ArrayList<DAO_ExercisePlan> temp = dbQuery.getSettingExercisePlan();
        for (int i = 0; i < temp.size(); i++) {
            if (temp.get(i).getExercisePlanState().equals("activation")) {
                exerciseList.add(temp.get(i));
            }
        }
        //exerciseList = dbQuery.getSettingExercisePlan();
        ListView list = (ListView) findViewById(R.id.list);
        customListAdapter = new CustomListAdapter(exerciseList);
        list.setAdapter(customListAdapter);
        setCustomActionbar();
    }

    @Override
    protected void connectedDevice() {

    }

    @Override
    protected void dataAvailableCheck(String data) {

    }

    private void setCustomActionbar() {
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        View mCustomView = LayoutInflater.from(this).inflate(R.layout.layout_actionbar, null);
        actionBar.setCustomView(mCustomView);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff6669")));

        Toolbar parent = (Toolbar) mCustomView.getParent();
        parent.setContentInsetsAbsolute(0, 0);

        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(mCustomView, params);

        ImageButton btn = (ImageButton) findViewById(R.id.custom_back_btn);
        btn.setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public  void onClick(View v) {
                        finish();
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    }
                }
        );
    }


}
