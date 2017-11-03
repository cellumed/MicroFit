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

import com.cellumed.healthcare.microfit.Bluetooth.BTConnectActivity;
import com.cellumed.healthcare.microfit.DAO.DAO_Favorites;
import com.cellumed.healthcare.microfit.DataBase.DBQuery;
import com.cellumed.healthcare.microfit.R;
import com.cellumed.healthcare.microfit.Util.BudUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Act_Favorites extends BTConnectActivity {

    @Bind(R.id.bt_1)
    Button bt1;
    @Bind(R.id.bt_2)
    Button bt2;
    @Bind(R.id.bt_3)
    Button bt3;
    @Bind(R.id.bt_4)
    Button bt4;
    @Bind(R.id.bt_5)
    Button bt5;
    @Bind(R.id.bt_6)
    Button bt6;
    @Bind(R.id.bt_7)
    Button bt7;

    private Context mContext;
    private List<Button> buttons;
    private static final int[] exerciseImg = {R.drawable.btn_eplan01, R.drawable.btn_eplan02, R.drawable.btn_eplan03, R.drawable.btn_eplan04};
    private static final int[] programImg = {R.drawable.btn_program01, R.drawable.btn_program02, R.drawable.btn_program03, R.drawable.btn_program04, R.drawable.btn_program05};
    private static final String[] exerciseName = {"fitness", "Muscles", "ExercisePerformance", "ManagementBody"};
    private static final String[] programName = {"program1", "program2", "program3", "program4", "program5"};

    @Override
    protected void connectedDevice() {

    }

    @Override
    protected void dataAvailableCheck(String data) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_favorites);
        ButterKnife.bind(this);
        mContext = this;
        BudUtil.actList.add(this);
        setCustomActionbar();
    }

    @Override
    protected void onResume() {
        int resId;
        super.onResume();

        buttons = Arrays.asList(bt1, bt2, bt3, bt4, bt5, bt6, bt7);
        final DBQuery dbQuery = new DBQuery(mContext);
        final ArrayList<DAO_Favorites> favoritesList = dbQuery.getFavoritesWhereUserId(BudUtil.getShareValue(mContext, BudUtil.USER_ID));
        int i;
        for (i = 0; i < favoritesList.size(); i++) {
            if (favoritesList.get(i).getFavoritesType().equals("1")) {
                if (favoritesList.get(i).getFavoritesType2().equals("Basic")) {
                    for (int j = 0; j < 4; j++) {
                        resId=mContext.getResources().getIdentifier(exerciseName[j], "string", mContext.getPackageName());
                        if (favoritesList.get(i).getFavoritesName().equals(  mContext.getResources().getString( resId ) )) {
                            buttons.get(i).setCompoundDrawablesWithIntrinsicBounds(exerciseImg[j], 0, 0, 0);
                            buttons.get(i).setText(  mContext.getResources().getString( resId )  );

                            break;
                        }
                    }
                } else {
                    buttons.get(i).setText(favoritesList.get(i).getFavoritesName());
                }

            } else {
                if (favoritesList.get(i).getFavoritesType2().equals("Basic")) {
                    for (int j = 0; j < 5; j++) {
                        resId=mContext.getResources().getIdentifier(programName[j], "string", mContext.getPackageName());
                        if (favoritesList.get(i).getFavoritesName().equals(   mContext.getResources().getString( resId )  )) {
                            buttons.get(i).setCompoundDrawablesWithIntrinsicBounds(programImg[j], 0, 0, 0);
                            buttons.get(i).setText(  mContext.getResources().getString( resId )  );
                            break;
                        }
                    }
                } else {
                    buttons.get(i).setText(favoritesList.get(i).getFavoritesName());
                }
            }

            final int finalI = i;

            buttons.get(i).setOnClickListener(v -> {
                final Bundle bundle = new Bundle();
                bundle.putString("title", favoritesList.get(finalI).getFavoritesName());
                if (favoritesList.get(finalI).getFavoritesType2().equals("Basic")) {
                    bundle.putBoolean("Basic", true);
                } else {
                    bundle.putBoolean("Basic", false);
                }
                if (favoritesList.get(finalI).getFavoritesType().equals("1")) {
                    BudUtil.goActivity(mContext, Act_Training.class, bundle);
                } else {
                    BudUtil.goActivity(mContext, Act_TrainingProgram.class, bundle);
                }
            });
        }

        for (; i < 7; i++) {
            buttons.get(i).setVisibility(View.GONE);
        }
    }

    private void setCustomActionbar() {
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        View mCustomView = LayoutInflater.from(this).inflate(R.layout.layout_actionbar, null);
        actionBar.setCustomView(mCustomView);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff6669")));
        ((ImageView) findViewById(R.id.custom_topImage)).setBackgroundDrawable(getResources().getDrawable(R.drawable.toptitle_favo));

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
