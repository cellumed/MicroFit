package com.cellumed.healthcare.microfit.Setting;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.cellumed.healthcare.microfit.Bluetooth.BTConnectActivity;
import com.cellumed.healthcare.microfit.R;
import com.cellumed.healthcare.microfit.Util.BudUtil;
import com.cellumed.healthcare.microfit.Util.SqliteToExcel;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.ButterKnife;

//import com.ikoob.ems_ui.Home.Act_Login;

public class Act_Setting extends BTConnectActivity implements OnAdapterClick {
    private Context mContext;

    @Override
    protected void connectedDevice() {}

    @Override
    protected void dataAvailableCheck(String data) {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_setting);
        ButterKnife.bind(this);
        mContext = this;
        BudUtil.actList.add(this);
        setCustomActionbar();

    }

    @Override
    protected void onDestroy() {
        RecycleUtils.recursiveRecycle(getWindow().getDecorView());
        System.gc();
        super.onDestroy();
    }


    public void programManagement(View view) {
        final Bundle bundle = new Bundle();
        BudUtil.goActivity(mContext, Act_ProgramManagement.class, bundle);
    }

    public void exercisePlanManagement (View view) {
        final Bundle bundle = new Bundle();
        BudUtil.goActivity(mContext, Act_ExercisePlanManagement.class, bundle);
    }

    public void favorites (View view) {
        final Bundle bundle = new Bundle();
        BudUtil.goActivity(mContext, Act_FavoritesSetting.class, bundle);
    }

    public void dataExtraction (View view) {
        File file1 = Environment.getExternalStorageDirectory();
        File file2 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);


        SqliteToExcel ste = new SqliteToExcel(this, "ems.db");
        ste.startExportSingleTable("WorkoutDataTable", "ems.xls", new SqliteToExcel.ExportListener() {
            @Override
            public void onStart() {
                Log.e("Frag_DataExtraction", "onStart");
            }

            @Override
            public void onComplete() {
                Log.e("Frag_DataExtraction", "onComplete");
                final String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + File.separator + "ems" + File.separator+"ems.xls";
                sendEmail(filePath);
                String r=mContext.getResources().getString(R.string.SaveDone);
                Toast.makeText(mContext, r, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError() {

            }
        });
    }

    private void sendEmail(String filePath) {

        File file = new File(filePath);
        Uri path = Uri.fromFile(file);

        AccountManager accountManager = (AccountManager) mContext.getSystemService(Context.ACCOUNT_SERVICE);
        final Account account = accountManager.getAccounts()[0];

        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        String[] address = {account.name};

        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setType("application/excel");
        intent.putExtra(Intent.EXTRA_EMAIL, address);
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "[MicroFit] Training Data");


        //intent.putExtra(Intent.EXTRA_STREAM, path);
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,new ArrayList<>(Arrays.asList(path)) );

        int requestCode = 1000;

        startActivityForResult(
                Intent.createChooser(intent, "Send mail via..."), requestCode);

    }

    public void systemInit (View view) {
        systemInitDialog();
    }

    public void versionInfo (View view) {
        String version= "";
        try {
            PackageInfo pi = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
            version = pi.versionName;
        } catch(PackageManager.NameNotFoundException e) { }

        MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext);
        builder
                .title(getString(R.string.SystemState))
                .titleColor(Color.parseColor("#FF6669"))
                .content("APP Version "+ version + "\nH/W Version  " + BudUtil.getInstance().HWVersion + "\nS/W Version  " + BudUtil.getInstance().FWVersion)
                .positiveText(getString(R.string.ok))
                .positiveColor(Color.parseColor("#FF6669"))
                .onPositive((dialog, which) -> {
                    dialog.dismiss();

                }).show();
    }

    public  void systemInitDialog(){
        MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext);
        builder
                .title(getString(R.string.warning))
                .titleColor(Color.parseColor("#FF6669"))
                .content(mContext.getString(R.string.SystemInit_Msg))
                .positiveText(getString(R.string.ok))
                .positiveColor(Color.parseColor("#FF6669"))
                .negativeColor(Color.DKGRAY)
                .negativeText(getString(R.string.cancel))
                .onPositive((dialog, which) -> {
                    dialog.dismiss();
                    new  com.cellumed.healthcare.microfit.DataBase.DataBases(mContext).reset();
                    MaterialDialog.Builder confirm = new MaterialDialog.Builder(mContext);
                    confirm.
                            title(getString(R.string.notice))
                            .titleColor(Color.parseColor("#FF6669"))
                            .content(getString(R.string.InitDone))
                            .positiveColor(Color.parseColor("#FF6669"))
                            .positiveText(getString(R.string.ok))
                            .onPositive((dialog2, which2) -> dialog2.dismiss()).show();
                })  .onNegative((dialog1, which1) -> dialog1.dismiss()
        ).show();
    }

    @Override
    public void onAdapterClick(int pos) {
    }

    private void setCustomActionbar() {
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        View mCustomView = LayoutInflater.from(this).inflate(R.layout.layout_actionbar, null);
        actionBar.setCustomView(mCustomView);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff6669")));
        ((ImageView) findViewById(R.id.custom_topImage)).setBackgroundDrawable(getResources().getDrawable(R.drawable.toptitle_set));


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
