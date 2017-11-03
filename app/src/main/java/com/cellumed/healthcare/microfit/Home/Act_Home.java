package com.cellumed.healthcare.microfit.Home;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.cellumed.healthcare.microfit.Bluetooth.BTConnectActivity;
import com.cellumed.healthcare.microfit.DAO.DAO_Favorites;
import com.cellumed.healthcare.microfit.DataBase.DBQuery;
import com.cellumed.healthcare.microfit.R;
import com.cellumed.healthcare.microfit.Setting.Act_Setting;
import com.cellumed.healthcare.microfit.Util.BudUtil;

import java.util.ArrayList;

import butterknife.ButterKnife;


public class Act_Home extends BTConnectActivity {

    private Context mContext;
    private boolean type;

    private BackPressCloseHandler backPressCloseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_home);
        setTitle("");
        ButterKnife.bind(this);
        mContext = this;
        BudUtil.actList.add(this);
        assert getSupportActionBar() != null;
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#fF6669")));



        backPressCloseHandler = new BackPressCloseHandler(this);
    }

    @Override
    protected void connectedDevice() {}

    @Override
    protected void dataAvailableCheck(String data) {}

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();
    }

    public void exercisePlan(View view) {
        final Bundle bundle = new Bundle();
        BudUtil.goActivity(mContext, Act_ExercisePlan.class,bundle);
    }

    public void program(View view) {
        final Bundle bundle = new Bundle();
        BudUtil.goActivity(mContext, Act_Program.class,bundle);
    }

    public void favorites(View view) {
        final DBQuery dbQuery = new DBQuery(mContext);
        final ArrayList<DAO_Favorites> favoritesList = dbQuery.getFavoritesWhereUserId(BudUtil.getShareValue(mContext, BudUtil.USER_ID));
        if (favoritesList.size() == 0) {
            MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext);
            builder
                    .title(getString(R.string.notice))
                    .titleColor(Color.parseColor("#3BBBCE"))
                    .content(getString(R.string.noFavorites))
                    .positiveText(getString(R.string.ok))
                    .positiveColor(Color.parseColor("#3BBBCE"))
                    .onPositive(((dialog, which) -> {
                        dialog.dismiss();
                    }))
                    .show();
        } else {
            final Bundle bundle = new Bundle();
            BudUtil.goActivity(mContext, Act_Favorites.class);
        }
    }

    public void setting(View view) {
        final Bundle bundle = new Bundle();
        bundle.putBoolean("type", type);
        BudUtil.goActivity(mContext, Act_Setting.class, bundle);
    }
}
