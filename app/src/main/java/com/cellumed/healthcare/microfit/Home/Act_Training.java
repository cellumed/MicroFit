package com.cellumed.healthcare.microfit.Home;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

@TargetApi(18)
public class Act_Training extends BTConnectActivity implements OnAdapterClick, IMP_CMD, SqlImp {
    @Bind(R.id.screenImage)
    ImageView screen;
    @Bind(R.id.posture_name)
    TextView posture;
    @Bind(R.id.time_minute)
    TextView minute;
    @Bind(R.id.time_second)
    TextView second;
    @Bind(R.id.listview1)
    ListView listView;
    @Bind(R.id.start_button)
    ImageButton start;
    @Bind(R.id.remainingNumber)
    TextView remainingNumber;

    ImageButton backBtn;
    private Context mContext;
    private int isRunning = 0;  // 0: stopped. 1: running. 2: sent user_param. 3: sent start_req
    private DecimalFormat formatter;
    private String userId = null;   // updated by  mBluetoothConnectService.getUserId();
    private int programTime;
    private int stimulusIntensity;
    private int frequency;
    private int onTime;
    private int offTime;
    private int risingTime;
    private int width;
    private int time;
    private int sumTime;
    private Custom_List_Adapter customAdapter;
    private CountDownTimer timer = null;
    private int listCnt = 0;
    private int exercisePlanRepeatCount = 0;
    private int exercisePlanRepeatCountTotal=0;
    private int rowCont;
    private int[] levelValues = new int[10];
    private long saveTime = 0;

    private int startTime;

    private Handler timeHandler = new Handler();
    private Runnable r;
    private ArrayList<DAO_Training> trainingData;

    private int last_send_cmd = 0;  // 0 is nothing

    private boolean not_started = true;
    long ts_last_ms=0;
    int last_milli=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_training);
        Log.e("TAG", "START ACT TRAINING");
        ButterKnife.bind(this);
        BudUtil.actList.add(this);
        mContext = this;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        startService();
        setStart();

        posture.setVisibility(View.INVISIBLE);
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
    }

    private void startCountDownTimer() {
        not_started=false;
        long millisSeconds = Integer.parseInt(trainingData.get(listCnt).getExercisePlanTime()) * 1000;
        final Locale locale = getResources().getConfiguration().locale;
        sumTime = Integer.parseInt(String.format("%.0f", (float)(onTime + offTime)));
        screen.setImageBitmap((BitmapFactory.decodeResource(mContext.getResources(), BudUtil.getImage(mContext, Integer.parseInt(trainingData.get(listCnt).getExercisePlanPostureImageNumber())))));
        posture.setVisibility(View.VISIBLE);
        posture.setText(trainingData.get(listCnt).getExercisePlanPosture());
        exercisePlanRepeatCountTotal = exercisePlanRepeatCount = (Integer.parseInt(trainingData.get(listCnt).getExercisePlanRepeatCount()) - 1);
        setRepeatCount(exercisePlanRepeatCount);
        BudUtil.regUser.setStartTime(BudUtil.getInstance().getToday("yyyy.MM.dd HH.mm.ss"));
        setWorkoutData();
        rowCont = sumTime;

        if (timer != null) {
            timer.start();
        } else {

            timer = new CountDownTimer(millisSeconds, 100) {
                @Override
                public void onTick(long millisUntilFinished) {
                    saveTime = millisUntilFinished / 1000 + 1;

                    runOnUiThread(() -> {
                        rowCont -= 1;
                        if (rowCont == 0) {
                            rowCont = sumTime;
                            if (exercisePlanRepeatCount != 0) {
                                if( startTime != time) {    // 중지했다가 시작할 때 startTime이 같은 경우 바로 빼지않음
                                    exercisePlanRepeatCount -= 1;
                                    setRepeatCount(exercisePlanRepeatCount);
                                }
                            } else {
                                if (listCnt != trainingData.size() - 1) {
                                    listCnt++;
                                    screen.setImageBitmap((BitmapFactory.decodeResource(mContext.getResources(), BudUtil.getImage(mContext, Integer.parseInt(trainingData.get(listCnt).getExercisePlanPostureImageNumber())))));
                                    posture.setText(trainingData.get(listCnt).getExercisePlanPosture());
                                    exercisePlanRepeatCountTotal=exercisePlanRepeatCount = (Integer.parseInt(trainingData.get(listCnt).getExercisePlanRepeatCount()) - 1);
                                    setRepeatCount(exercisePlanRepeatCount);
                                }
                            }
                        }
                    });

                }
                @Override
                public void onFinish() {
                    finish();
                }
            }.start();
        }
    }

    private void setWorkoutData() {
        final HashMap<String, String> workoutData = new HashMap<>();
        workoutData.put(WorkoutDataStartDate, BudUtil.regUser.getStartTime());
        workoutData.put(WorkoutDataContent, String.format("%s", trainingData.get(listCnt).getExercisePlanName()));
        workoutData.put(WorkoutDataETC, String.format("Training %s", trainingData.get(listCnt).getProgramType()));

        if (new DBQuery(mContext).newWorkoutData(workoutData)) {
            Log.d("Act_Training", "저장");
        }
    }

    private void setRepeatCount(int cnt) {
        remainingNumber.setText(String.valueOf(cnt + 1) + "/" + String.valueOf(exercisePlanRepeatCountTotal + 1));
    }


    private void setStart() {
        final DBQuery dbQuery = new DBQuery(mContext);
        final Bundle extras = getIntent().getExtras();
        trainingData = dbQuery.getTrainingData(mContext, extras.getString("title"), extras.getBoolean("Basic"));

        if (extras.getBoolean("Basic") == true) {

            programTime = 1200;
            frequency = 85;
            stimulusIntensity = 0;
            onTime = 40;
            offTime = 40;
            if (extras.getString("title").equals("운동성능")) {
                risingTime = 1;
            } else {
                risingTime = 4;
            }
            width = 350 / 25;

        } else {
            try {
                programTime = Integer.parseInt(trainingData.get(0).getExercisePlanTime());
            } catch (Exception e) {
                programTime = 0;
            }
            try {
                stimulusIntensity = Integer.parseInt(trainingData.get(0).getProgramStimulusIntensity());
            } catch (Exception e) {
                stimulusIntensity = 0;
            }
            try {
                frequency = Integer.parseInt(trainingData.get(0).getProgramFrequency());
            } catch (NumberFormatException e) {
                finish();
            }
            try {
                onTime = (int) (Float.parseFloat(trainingData.get(0).getProgramPulseOperationTime()) * 10);
            } catch (NumberFormatException e) {
                onTime = 0;
            }
            try {
                offTime = (int) (Float.parseFloat(trainingData.get(0).getProgramPulsePauseTime()) * 10);
            } catch (NumberFormatException e) {
                offTime = 0;
            }
            try {
                risingTime = (int) (Float.parseFloat(trainingData.get(0).getProgramPulseRiseTime()) * 10);
            } catch (NumberFormatException e) {
                risingTime = 0;
            }
            try {
                width = Integer.parseInt(trainingData.get(0).getProgramPulseWidth());
            } catch (NumberFormatException e) {
                width = 0;
            }
            width = width / 25;
        }

        time = programTime;
    }

    Button.OnClickListener startClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int interval;

            if (isRunning == 0) {
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
                                finish();   // finish activity and go back to list
                            }
                        }
                    }
                };

                startTime = time;
                // check interval
                // set starttime when resumed
                interval = (onTime + offTime)/10;
                if (time % interval > 0) {
                    time -= time % interval;
                    minute.setText(formatter.format((int) time / 60));
                    second.setText(formatter.format((int) time % 60));
                }



                mBluetoothConnectService.send(CMD_USER_PARAM, makeUserParam());

                Log.e("TAG", "time=" + Integer.toString(time) + ", rowCont=" + Integer.toString(rowCont));

            } else {
                isRunning = 0;
                whenRequestStop();
            }
        }
    };

    private void whenRequestStop() {
        ts_last_ms=0;
        isRunning = 0;
        if(timer!=null) timer.cancel();
        start.setBackgroundResource(R.drawable.btn_start);
        backBtn.setClickable(true);
        backBtn.setSelected(false);
        backBtn.setBackgroundResource(R.drawable.custom_back_btn);
        mBluetoothConnectService.send(CMD_STOP_REQ, "");
        timeHandler.removeCallbacks(r);

        if(!not_started) {
            new DBQuery(mContext).editWorkoutData(BudUtil.regUser.getStartTime(), Integer.parseInt(trainingData.get(0).getExercisePlanTime()) - time);
        }

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
    protected void onPause() {
        super.onPause();
        whenRequestStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(not_started ==false) {
            recycleBitmap(screen);
        }
        try{
            timer.cancel();
        } catch (Exception e) {}
        timer = null;
    }

    private static void recycleBitmap(ImageView iv) {
        Drawable d = iv.getDrawable();
        if (d instanceof BitmapDrawable) {
            Bitmap b = ((BitmapDrawable)d).getBitmap();
            b.recycle();
        } // 현재로서는 BitmapDrawable 이외의 drawable 들에 대한 직접적인 메모리 해제는 불가능하다.

        d.setCallback(null);
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
            levelValues[i] = level;
            toSend.append(String.format("%02X", level));
        }
        setLevelValue();
        toSend.append("00");
        toSend.append("00");
        return toSend.toString();
    }

    private void setLevelValue() {
        BudUtil.regUser.setProgress1(levelValues[0]);
        BudUtil.regUser.setProgress2(levelValues[1]);
        BudUtil.regUser.setProgress3(levelValues[2]);
        BudUtil.regUser.setProgress4(levelValues[3]);
        BudUtil.regUser.setProgress5(levelValues[4]);
        BudUtil.regUser.setProgress6(levelValues[5]);
        BudUtil.regUser.setProgress7(levelValues[6]);
        BudUtil.regUser.setProgress8(levelValues[7]);
        BudUtil.regUser.setProgress9(levelValues[8]);
        BudUtil.regUser.setProgress10(levelValues[9]);
    }


    @Override
    protected void connectedDevice() {}

    @Override
    protected void dataAvailableCheck(String data) {
        Log.e("TAG", data);

        /*
        // update userId.
         // comment all cuz userid is constant in bluetooth version.
        if (data.split(" ")[2].equals("20"))
            userId = data.split(" ")[3];
        */

        int interval;

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
                if (rowCont > 1) rowCont=1;


                Log.d("TAG","ACK recved for START_REQ");
                if (timer != null) {
                    timer.start();
                } else {
                    Log.d("TAG","ACK recved for START_REQ. Timer not null");
                    startCountDownTimer();
                }

                isRunning=1;
                timeHandler.postDelayed(r, 1000);

                Log.e("TAG", "ACK!  time=" + Integer.toString(time) + ", rowCont=" + Integer.toString(rowCont));
            }
            else
            {
                Log.d("TAG","ACK recved. Running State = " + isRunning);
            }
        }
        else if (data.split(" ")[2].equals(CMD_INPUT_INFO))
        {
            Log.d("TAG","INPUT_INFO");
            if(isRunning!=0) {
                isRunning = 0;
                whenRequestStop();
            }
        }


    }

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
        ((ImageView) findViewById(R.id.custom_topImage)).setBackgroundDrawable(null);
        ((TextView) findViewById(R.id.custom_name)).setText(trainingData.get(0).getExercisePlanName() );


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
