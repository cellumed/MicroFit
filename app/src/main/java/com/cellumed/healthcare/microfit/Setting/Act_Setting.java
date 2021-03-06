package com.cellumed.healthcare.microfit.Setting;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import butterknife.ButterKnife;

//import com.ikoob.ems_ui.Home.Act_Login;

public class Act_Setting extends BTConnectActivity implements OnAdapterClick {
    private Context mContext;
    private static final String AUTHORITY="com.commonsware.android.cp.v4file";
    ImageButton bt_info;

    @Override
    protected void connectedDevice() {}

    @Override
    protected void dataAvailableCheck(String data) {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_setting);
        ButterKnife.bind(this);

        bt_info = (ImageButton)findViewById(R.id.bt_system_state);
        bt_info.setOnClickListener(bt_info_listener);

        mContext = this;
        BudUtil.actList.add(this);
        setCustomActionbar();

    }

    ImageButton.OnClickListener bt_info_listener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            read_manual_target26();
        }
    };

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


    public void read_manual(){

        Log.i("", "read manual");

        try {
            final Locale locale = mContext.getResources().getConfiguration().locale;
            String manual_name = "";
            String copyPath = "";

            if (locale.getLanguage().equals("ko")) {
                manual_name = "manual_ko.pdf";
                copyPath = "/sdcard/Download/" + manual_name;
            }
            else if (locale.getLanguage().equals("zh")) {
                manual_name = "manual_zh.pdf";
                copyPath = "/sdcard/Download/" + manual_name;
            }
            else if (locale.getLanguage().equals("ja")) {
                manual_name = "manual_ja.pdf";
                copyPath = "/sdcard/Download/" + manual_name;
            }
            else {
                manual_name = "manual_en.pdf";
                copyPath = "/sdcard/Download/" + manual_name;
            }

            AssetManager am = mContext.getResources().getAssets();
            InputStream is = am.open(manual_name);
            byte buff[] = new byte[1024];
            int read;

            File file = new File(copyPath);
            if(file.exists()){
                Log.i("", "Exist File !!!");
            }
            else{
                OutputStream os = new FileOutputStream(copyPath);

                while( ( read = is.read(buff)) != -1){
                    os.write(buff, 0, read);
                }
                is.close();
                os.flush();
                os.close();
            }

            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setDataAndType(Uri.fromFile(new File(copyPath)), "application/pdf");

            startActivity(intent);

        }catch(ActivityNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void read_manual_target26(){

        Log.i("pain0928", "read manual_target26");

        try {
            final Locale locale = mContext.getResources().getConfiguration().locale;
            String manual_name = "";
            String copyPath = "";

            if (locale.getLanguage().equals("ko")) {
                manual_name = "manual_ko.pdf";
                copyPath = Environment.getExternalStorageDirectory().getPath() + "/Download/" + manual_name;
            }
            else if (locale.getLanguage().equals("zh")) {
                manual_name = "manual_zh.pdf";
                copyPath = Environment.getExternalStorageDirectory().getPath() + "/Download/" + manual_name;
            }
            else if (locale.getLanguage().equals("ja")) {
                manual_name = "manual_ja.pdf";
                copyPath = Environment.getExternalStorageDirectory().getPath() + "/Download/" + manual_name;
            }
            else {
                manual_name = "manual_en.pdf";
                copyPath = Environment.getExternalStorageDirectory().getPath() + "/Download/" + manual_name;
            }
            Log.i("pain0928", "Copy Path:" + copyPath);

            Log.i("pain0928", "file-path:" + mContext.getFilesDir().getPath());
            Log.i("pain0928", "external-path:" + Environment.getExternalStorageDirectory().getPath());

            AssetManager am = mContext.getResources().getAssets();
            InputStream is = am.open(manual_name);
            byte buff[] = new byte[1024];
            int read;

            File file = new File(copyPath);
            if(file.exists()){
                Log.i("", "Exist File !!!");
            }
            else{
                OutputStream os = new FileOutputStream(copyPath);

                while( ( read = is.read(buff)) != -1){
                    os.write(buff, 0, read);
                }
                is.close();
                os.flush();
                os.close();
            }

            PackageInfo pi = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(FileProvider.getUriForFile(this, pi.packageName + ".fileprovider", file),"application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(intent);

        }catch(ActivityNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void versionInfo (View view) {
        /*
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
        */
        //read_manual();
        read_manual_target26();
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
