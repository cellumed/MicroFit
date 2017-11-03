package com.cellumed.healthcare.microfit.Home;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cellumed.healthcare.microfit.Bluetooth.BTConnectActivity;
import com.cellumed.healthcare.microfit.DAO.DAO_Training;
import com.cellumed.healthcare.microfit.DataBase.DBQuery;
import com.cellumed.healthcare.microfit.R;
import com.cellumed.healthcare.microfit.Setting.OnAdapterClick;
import com.cellumed.healthcare.microfit.Bluetooth.IMP_CMD;
import com.cellumed.healthcare.microfit.Util.BudUtil;
import com.cellumed.healthcare.microfit.DataBase.SqlImp;

import java.text.DecimalFormat;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

@TargetApi(18)
public class Act_TrainingProgram extends BTConnectActivity implements OnAdapterClick, IMP_CMD, SqlImp {
    @Bind(R.id.time_minute)
    TextView minute;
    @Bind(R.id.time_second)
    TextView second;
    @Bind(R.id.listview1)
    ListView listView;
    @Bind(R.id.start_button)
    ImageButton start;

    ImageButton backBtn;
    private Context mContext;
    private int isRunning = 0;  // status. 0: stopped. 1: running. 2: user param sent. 3. start req sent.
    private DecimalFormat formatter;
    private String userId = null;   // updated by  mBluetoothConnectService.getUserId();
    private int programTime;
    private int stimulusIntensity;
    private String title;
    private int frequency;
    private int onTime;
    private int offTime;
    private int risingTime;
    private int width;
    private int time;
    private Custom_List_Adapter customAdapter;

    private Handler timeHandler = new Handler();
    private Runnable r;

    private DAO_Training trainingData;

    private boolean not_started = true;
    long ts_last_ms=0;
    int last_milli=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("TAG", "START ACT TRAINING PROGRAM");
        setContentView(R.layout.act_training_program);
        ButterKnife.bind(this);
        mContext = this;
        BudUtil.actList.add(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        startService();
        setStart();

        formatter = new DecimalFormat("00");
        String minuteDefault = formatter.format((int) programTime / 60);
        String secondDefault = formatter.format((int) programTime % 60);
        minute.setText(minuteDefault);
        second.setText(secondDefault);

        start.setOnClickListener(startClickListener);
        customAdapter = new Custom_List_Adapter(new Custom_List_Adapter.OnItemValueChangedListener() {
            @Override
            public void onItemValueChanged() {
                if (isRunning == 1)
                    mBluetoothConnectService.send(CMD_LEVEL_PARAM, makeLevelParam());
            }
        });

        setCustomActionbar();
        listView.setAdapter(customAdapter);
        setCustomList();
        //listView.setEnabled(false);

    }

    private void setStart() {
        final Bundle extras = getIntent().getExtras();
        title = extras.getString("title");

        final DBQuery dbQuery = new DBQuery(mContext);
        trainingData = dbQuery.getProgramTrainingData(title);

        try {
            programTime = Integer.parseInt(trainingData.getProgramTime());
        } catch (Exception e) {
            e.printStackTrace();
        }

        frequency = Integer.parseInt(trainingData.getProgramFrequency());

        try {
            stimulusIntensity = Integer.parseInt(trainingData.getProgramStimulusIntensity());
        } catch (Exception e) {
            stimulusIntensity = 0;
        }

        try {
            onTime = (int) (Float.parseFloat(trainingData.getProgramPulseOperationTime()) * 10);
        } catch (NumberFormatException e) {
            if (trainingData.getProgramPulseOperationTime().equals("duration")) {
                onTime = 100;
            } else {
                onTime = 0;
            }
        }

        try {
            offTime = (int) (Float.parseFloat(trainingData.getProgramPulsePauseTime()) * 10);
        } catch (NumberFormatException e) {
            offTime = 0;
        }
        try {
            risingTime = (int) (Float.parseFloat(trainingData.getProgramPulseRiseTime()) * 10);
        } catch (NumberFormatException e) {
            risingTime = 0;
        }
        try {
            width = Integer.parseInt(trainingData.getProgramPulseWidth());
        } catch (NumberFormatException e) {
            width = 0;
        }
        width = width / 25;
        time = programTime;
    }


    Button.OnClickListener startClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (isRunning == 0) {
                not_started = false;
                isRunning = 2;
                start.setBackgroundResource(R.drawable.btn_stop);
                backBtn.setClickable(false);
                backBtn.setSelected(true);
                backBtn.setBackgroundResource(R.drawable.bt_back_sel);
                minute.setEnabled(false);
                second.setEnabled(false);

                r = new Runnable() {
                    @Override
                    public void run() {
                        timeHandler.postDelayed(this, 100);
                        if(ts_last_ms==0)
                        {
                            ts_last_ms = (System.currentTimeMillis());
                            last_milli= ((int)ts_last_ms % 10000000);
                        }
                        int milli = ((int)System.currentTimeMillis() % 10000000);
                        if(milli - last_milli > 900) {
                            last_milli+=1000;
                            time--;
                            minute.setText(formatter.format((int) time / 60));
                            second.setText(formatter.format((int) time % 60));

                            if (time <= 0) {
                                whenRequestStop();
                                finish();  // finish activity and go back to list
                            }
                        }

                    }
                };

                mBluetoothConnectService.send(CMD_USER_PARAM, makeUserParam());
            } else {
                isRunning = 0;
                whenRequestStop();
            }
        }
    };

    private void whenRequestStop() {
        ts_last_ms=0;
        isRunning = 0;
        start.setBackgroundResource(R.drawable.btn_start);
        backBtn.setClickable(true);
        backBtn.setSelected(false);
        backBtn.setBackgroundResource(R.drawable.custom_back_btn);
        mBluetoothConnectService.send(CMD_STOP_REQ, "");
        timeHandler.removeCallbacks(r);
        if(!not_started)   new DBQuery(mContext).editWorkoutData(BudUtil.regUser.getStartTime(), Integer.parseInt(trainingData.getProgramTime()) - time);

    }


    private void setCustomList() {
        customAdapter.addItem(mContext.getResources().getString(R.string.Leg1), formatter.format(stimulusIntensity));
        customAdapter.addItem(mContext.getResources().getString(R.string.Leg2), formatter.format(stimulusIntensity));
        customAdapter.addItem(mContext.getResources().getString(R.string.Back_Up), formatter.format(stimulusIntensity));
        customAdapter.addItem(mContext.getResources().getString(R.string.Back_Down), formatter.format(stimulusIntensity));
        customAdapter.addItem(mContext.getResources().getString(R.string.Hips), formatter.format(stimulusIntensity));
        customAdapter.addItem( mContext.getResources().getString(R.string.Chest), formatter.format(stimulusIntensity));
        customAdapter.addItem( mContext.getResources().getString(R.string.Abdomen), formatter.format(stimulusIntensity));
        customAdapter.addItem(mContext.getResources().getString(R.string.Arms), formatter.format(stimulusIntensity));
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        whenRequestStop();
    }

    private void setWorkoutData() {
        BudUtil.regUser.setStartTime(BudUtil.getInstance().getToday("yyyy.MM.dd HH.mm.ss"));
        final HashMap<String, String> workoutData = new HashMap<>();
        workoutData.put(WorkoutDataStartDate, BudUtil.regUser.getStartTime());
        workoutData.put(WorkoutDataContent, String.format("%s", trainingData.getProgramName()));
        workoutData.put(WorkoutDataETC, String.format("Program %s", trainingData.getProgramType()));

        if (new DBQuery(mContext).newWorkoutData(workoutData)) {
            Log.d("program", "저장");
        }
    }

    private String makeLevelParam() {
        if (userId==null) {
            if (mBluetoothConnectService.getUserId() != null)
                userId = mBluetoothConnectService.getUserId();
        }

        StringBuilder toSend = new StringBuilder(userId);
        toSend.append(makeLevelData());

        return toSend.toString();
    }

    private String makeUserParam() {
        if (userId==null) {
            if (mBluetoothConnectService.getUserId() != null)
                userId = mBluetoothConnectService.getUserId();
        }

        StringBuilder toSend = new StringBuilder(userId);
        toSend.append(String.format("%04X", time));
        toSend.append(String.format("%02X", frequency));
        toSend.append(String.format("%02X", onTime));
        toSend.append(String.format("%02X", offTime));
        toSend.append(String.format("%02X", risingTime));
        toSend.append(String.format("%02X", width));
        toSend.append(makeLevelData());

        Log.e("TAG", toSend.toString());
        return toSend.toString();
    }

    private String makeLevelData() {
        StringBuilder toSend = new StringBuilder();
        for (int i = 0; i < customAdapter.getCount(); i++) {
            Custom_List_View_Item item = (Custom_List_View_Item) customAdapter.getItem(i);
            int level = Integer.parseInt(item.getLevelValueString());
            toSend.append(String.format("%02X", level));
        }
        toSend.append("00");
        toSend.append("00");

        return toSend.toString();
    }

    @Override
    protected void dataAvailableCheck(String data) {
        Log.e("TAG", data);

        /*
        if (data.split(" ")[2].equals(CMD_ID_INFO))
            userId = data.split(" ")[3];
        */

        if (data.split(" ")[2].equals("1"))
        {
            if( isRunning == 2) { //ACK for USER_PARAM
                Log.d("TAG","ACK recved for USER_PARAM");
                mBluetoothConnectService.send(CMD_START_REQ, "");
                Log.d("TAG","Sent START_REQ");
                isRunning = 3;
            }
            else if(isRunning == 3) // ACK for START REQ
            {
                Log.d("TAG","ACK recved for START_REQ");
                setWorkoutData();
                isRunning=1;
                timeHandler.postDelayed(r, 1000);
            }
            else
            {
                Log.d("TAG","ACK recved. Running State = " + isRunning);
            }
        }
        else if (data.split(" ")[2].equals(CMD_INPUT_INFO))
        {
            if(isRunning!=0) {
                Log.d("TAG","INPUT INFO recved. Running State = " + isRunning + ". stop");
                isRunning = 0;
                whenRequestStop();

            }
        }
    }

    @Override
    protected void connectedDevice() {}

    @Override
    public void onAdapterClick(int pos) {}

    private void setCustomActionbar() {
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        View mCustomView = LayoutInflater.from(this).inflate(R.layout.layout_actionbar, null);
        actionBar.setCustomView(mCustomView);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff6669")));
    //    ((ImageView) findViewById(R.id.custom_topImage)).setBackgroundDrawable(getResources().getDrawable(R.drawable.toptitle_eprog));
        ((ImageView) findViewById(R.id.custom_topImage)).setBackgroundDrawable(null);
        ((TextView) findViewById(R.id.custom_name)).setText(trainingData.getProgramName() );


        Toolbar parent = (Toolbar) mCustomView.getParent();
        parent.setContentInsetsAbsolute(0, 0);

        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(mCustomView, params);

        backBtn = (ImageButton) findViewById(R.id.custom_back_btn);
        backBtn.setOnClickListener(
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
