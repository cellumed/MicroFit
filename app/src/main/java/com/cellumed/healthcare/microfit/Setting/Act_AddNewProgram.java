package com.cellumed.healthcare.microfit.Setting;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.cellumed.healthcare.microfit.Bluetooth.BTConnectActivity;
import com.cellumed.healthcare.microfit.DataBase.DBQuery;
import com.cellumed.healthcare.microfit.DataBase.SqlImp;
//import com.ikoob.ems_ui.Home.Frag_Program;
import com.cellumed.healthcare.microfit.Home.Imp_DBOK;
import com.cellumed.healthcare.microfit.R;
import com.cellumed.healthcare.microfit.Util.BudUtil;

import java.text.DecimalFormat;
import java.util.HashMap;


public class Act_AddNewProgram extends BTConnectActivity implements SqlImp {
    public Act_AddNewProgram parent = this;
    public Imp_DBOK mDbok;
    private Context mContext;
    private boolean et_PulseOperationTimeFocus = false;
    private boolean et_PulsePauseTimeFocus = false;
    private boolean et_PulseRiseTimeFocus = false;
    private boolean et_FrequencyFocus = false;


    final int pulseWidthMin = 50;


    private final int RESULT_OK = 1;
    Button save;
    Button cancel;

    private int initialTime = 0;
    @Override
    protected void connectedDevice() {}

    @Override
    protected void dataAvailableCheck(String data) {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_add_new_program);
        mContext = this;
        save = (Button) findViewById(R.id.save);
        cancel = (Button) findViewById(R.id.cancel);
        save.setOnClickListener(saveOnClickListener);
        cancel.setOnClickListener(cancelOnClickListener);

        setCustomActionbar();
        showNewProgram();
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

    private void showNewProgram() {
        final EditText et_exercisePlanName = (EditText) findViewById(R.id.et_exercisePlanName);
        final EditText et_time = (EditText) findViewById(R.id.et_time);
        /*
        final EditText et_StimulusIntensity = (EditText) findViewById(R.id.et_StimulusIntensity);
        final EditText et_PulseOperationTime = (EditText) findViewById(R.id.et_PulseOperationTime);
        final EditText et_PulsePauseTime = (EditText) findViewById(R.id.et_PulsePauseTime);
        final EditText et_Frequency = (EditText) findViewById(R.id.et_Frequency);
        final EditText et_PulseWidth = (EditText) findViewById(R.id.et_PulseWidth);
        final EditText et_PulseRiseTime = (EditText) findViewById(R.id.et_PulseRiseTime);
        */
        final TextView et_StimulusIntensity = (TextView) findViewById(R.id.et_StimulusIntensity);
        final TextView et_PulseOperationTime = (TextView) findViewById(R.id.et_PulseOperationTime);
        final TextView et_PulsePauseTime = (TextView) findViewById(R.id.et_PulsePauseTime);
        final TextView et_Frequency = (TextView) findViewById(R.id.et_Frequency);
        final TextView et_PulseWidth = (TextView) findViewById(R.id.et_PulseWidth);
        final TextView et_PulseRiseTime = (TextView) findViewById(R.id.et_PulseRiseTime);

        final SeekBar sb_StimulusIntensity = (SeekBar) findViewById(R.id.sb_StimulusIntensity);
        final SeekBar sb_PulseOperationTime = (SeekBar) findViewById(R.id.sb_PulseOperationTime);
        final SeekBar sb_PulsePauseTime = (SeekBar) findViewById(R.id.sb_PulsePauseTime);
        final SeekBar sb_Frequency = (SeekBar) findViewById(R.id.sb_Frequency);
        final SeekBar sb_PulseWidth = (SeekBar) findViewById(R.id.sb_PulseWidth);
        final SeekBar sb_PulseRiseTime = (SeekBar) findViewById(R.id.sb_PulseRiseTime);
        final Button add_StimulusIntensity = (Button) findViewById(R.id.add_StimulusIntensity);
        final Button add_PulseOperationTime = (Button) findViewById(R.id.add_PulseOperationTime);
        final Button add_PulsePauseTime = (Button) findViewById(R.id.add_PulsePauseTime);
        final Button add_Frequency = (Button) findViewById(R.id.add_Frequency);
        final Button add_PulseWidth = (Button) findViewById(R.id.add_PulseWidth);
        final Button add_PulseRiseTime = (Button) findViewById(R.id.add_PulseRiseTime);
        final Button remove_StimulusIntensity = (Button) findViewById(R.id.remove_StimulusIntensity);
        final Button remove_PulseOperationTime = (Button) findViewById(R.id.remove_PulseOperationTime);
        final Button remove_PulsePauseTime = (Button) findViewById(R.id.remove_PulsePauseTime);
        final Button removeFrequency = (Button) findViewById(R.id.removeFrequency);
        final Button removePulseWidth = (Button) findViewById(R.id.removePulseWidth);
        final Button remove_PulseRiseTime = (Button) findViewById(R.id.remove_PulseRiseTime);

        et_exercisePlanName.setImeOptions(EditorInfo.IME_ACTION_DONE);
        et_exercisePlanName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    et_exercisePlanName.setCursorVisible(false);
                    downKeyboard(mContext, et_exercisePlanName);
                    return true;
                }
                return false;
            }
        });
        et_exercisePlanName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_exercisePlanName.setCursorVisible(true);
            }
        });

        et_time.setImeOptions(EditorInfo.IME_ACTION_DONE);
        et_time.setRawInputType(Configuration.KEYBOARD_12KEY);
        et_time.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    et_time.setCursorVisible(false);
                    downKeyboard(mContext, et_time);
                    if(TextUtils.isDigitsOnly(et_time.getText()))
                    {
                        initialTime = Integer.parseInt(et_time.getText().toString());
                        et_time.setText(et_time.getText() + getString(R.string.minute));
                        return true;
                    }
                    else
                    {
                        et_time.setText("");
                        MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext);
                        builder
                                .title(getString(R.string.notice))
                                .titleColor(Color.parseColor("#FF6669"))
                                .content(getString(R.string.NumberError))
                                .positiveText(getString(R.string.ok))
                                .positiveColor(Color.parseColor("#FF6669"))
                                .onPositive((dialog, which) -> {
                                    dialog.dismiss();

                                }).show();
                        return false;
                    }

                }
                return false;
            }
        });
        et_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_time.setCursorVisible(true);
            }
        });

        /*
        et_StimulusIntensity.setImeOptions(EditorInfo.IME_ACTION_DONE);
        et_StimulusIntensity.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        et_StimulusIntensity.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    et_StimulusIntensity.setCursorVisible(false);
                    downKeyboard(mContext, et_StimulusIntensity);
                    return true;
                }
                return false;
            }
        });
        et_StimulusIntensity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_StimulusIntensity.setCursorVisible(true);
            }
        });
*/

        /*
        et_PulseOperationTime.setImeOptions(EditorInfo.IME_ACTION_DONE);
        et_PulseOperationTime.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        et_PulseOperationTime.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    et_PulseOperationTime.setCursorVisible(false);
                    downKeyboard(mContext, et_PulseOperationTime);
                    return true;
                }
                return false;
            }
        });
        et_PulseOperationTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_PulseOperationTime.setCursorVisible(true);
            }
        });

        et_PulsePauseTime.setImeOptions(EditorInfo.IME_ACTION_DONE);
        et_PulsePauseTime.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        et_PulsePauseTime.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    et_PulsePauseTime.setCursorVisible(false);
                    downKeyboard(mContext, et_PulsePauseTime);
                    return true;
                }
                return false;
            }
        });
        et_PulsePauseTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_PulsePauseTime.setCursorVisible(true);
            }
        });

        et_PulseRiseTime.setImeOptions(EditorInfo.IME_ACTION_DONE);
        et_PulseRiseTime.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        et_PulseRiseTime.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    et_PulseRiseTime.setCursorVisible(false);
                    downKeyboard(mContext, et_PulseRiseTime);
                    return true;
                }
                return false;
            }
        });
        et_PulseRiseTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_PulseRiseTime.setCursorVisible(true);
            }
        });

        et_Frequency.setImeOptions(EditorInfo.IME_ACTION_DONE);
        et_Frequency.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        et_Frequency.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    et_Frequency.setCursorVisible(false);
                    downKeyboard(mContext, et_Frequency);
                    return true;
                }
                return false;
            }
        });
        et_Frequency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_Frequency.setCursorVisible(true);
            }
        });

        et_PulseWidth.setImeOptions(EditorInfo.IME_ACTION_DONE);
        et_PulseWidth.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        et_PulseWidth.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    et_PulseWidth.clearFocus();;
                    et_PulseWidth.setCursorVisible(false);
                    downKeyboard(mContext, et_PulseWidth);
                    return true;
                }
                return false;
            }
        });
        et_PulseWidth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_PulseWidth.setCursorVisible(true);
            }
        });



        et_StimulusIntensity.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                et_StimulusIntensity.setSelection(et_StimulusIntensity.getText().length());
            }
        });
        et_StimulusIntensity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    sb_StimulusIntensity.setProgress(Integer.parseInt(s.toString()));
                    et_StimulusIntensity.setSelection(s.length());
                } catch (Exception e) {
                }
            }
        });
        et_PulseOperationTime.setOnFocusChangeListener((v, hasFocus) -> {
            et_PulseOperationTimeFocus = hasFocus;
            if (hasFocus) {
                et_PulseOperationTime.setSelection(et_PulseOperationTime.length());
            }
        });

        et_PulsePauseTime.setOnFocusChangeListener((v, hasFocus) -> {
            et_PulsePauseTimeFocus = hasFocus;
            if (hasFocus) {
                et_PulsePauseTime.setSelection(et_PulsePauseTime.getText().length());
            }
        });

        et_Frequency.setOnFocusChangeListener((v, hasFocus) -> {
            et_FrequencyFocus = hasFocus;
            if (hasFocus) {
                et_Frequency.setSelection(et_Frequency.getText().length());
            }
        });
        et_PulseRiseTime.setOnFocusChangeListener((v, hasFocus) -> {
            et_PulseRiseTimeFocus = hasFocus;
            if (hasFocus) {
                et_PulseRiseTime.setSelection(et_PulseRiseTime.getText().length());
            }
        });
        et_PulseWidth.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                et_PulseWidth.setSelection(et_PulseWidth.getText().length());
            }
            else    // 포커스 잃었을때 입력이 완료된거로 처리
            {
                int value=Integer.parseInt(et_PulseWidth.getText().toString());

                if( value < 50) {

                    value=50;
                    et_PulseWidth.setText(String.valueOf(value));
                }
                else if (value > 400) {

                    value=400;
                    et_PulseWidth.setText(String.valueOf(value));
                }
                else
                {
                    value -= pulseWidthMin;
                    // 25단위로 보정
                    if(value%25!=0) {
                        int k = (int) (value / 25);
                        k = k * 25;
                        value=k;
                        et_PulseWidth.setText(String.valueOf(k));
                    }
                    sb_PulseWidth.setProgress(value / 25);
                    //et_PulseWidth.setSelection(s.length());

                }
            }

        });
*/




        setSeekberThumb(sb_StimulusIntensity, mContext.getResources());
        sb_StimulusIntensity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                et_StimulusIntensity.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sb_StimulusIntensity.setProgress(0);
        add_StimulusIntensity.setOnClickListener(v -> sb_StimulusIntensity.setProgress(sb_StimulusIntensity.getProgress() + 1));
        remove_StimulusIntensity.setOnClickListener(v -> sb_StimulusIntensity.setProgress(sb_StimulusIntensity.getProgress() - 1));

        // 동작시간
        /*
        et_PulseOperationTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    float v = Float.parseFloat(s.toString());

                    if (v >10) {

						v=10;
                    }else if(v<1) {
                        v = 1;
                        //    v = v * 10;
                    }
                    sb_PulseOperationTime.setProgress((int) v);
                    //et_PulseOperationTime.setSelection(s.length());


                } catch (Exception e) {
                }
            }
        });
        */
        setSeekberThumb(sb_PulseOperationTime, mContext.getResources());
        sb_PulseOperationTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            private boolean sb_pulseOperationTimeTracking;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                float value = (float) (progress +1);
                if (value == 10.0) {
                    et_PulseOperationTime.setText(String.valueOf((int) value));
                } else {
                    et_PulseOperationTime.setText(String.valueOf((int) value));

                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                sb_pulseOperationTimeTracking = true;

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sb_pulseOperationTimeTracking = false;
            }
        });

        sb_PulseOperationTime.setProgress(3); // 1+3
        add_PulseOperationTime.setOnClickListener(v -> sb_PulseOperationTime.setProgress(sb_PulseOperationTime.getProgress() + 1));
        remove_PulseOperationTime.setOnClickListener(v -> sb_PulseOperationTime.setProgress(sb_PulseOperationTime.getProgress() - 1));


        setSeekberThumb(sb_PulsePauseTime, mContext.getResources());
        sb_PulsePauseTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            private boolean sb_pulsePauseTimeTracking;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!et_PulsePauseTimeFocus || sb_pulsePauseTimeTracking) {
                    float value = (float) (progress); // / 10.0);
                    if (value == 10.0) {
                        et_PulsePauseTime.setText(String.valueOf((int) value));
                      //  sb_PulsePauseTime.setProgress((int)value);
                    } else {
                      //  sb_PulsePauseTime.setProgress((int)value);
                        et_PulsePauseTime.setText(String.valueOf((int)value));

                    }

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                sb_pulsePauseTimeTracking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sb_pulsePauseTimeTracking = false;
            }
        });
        /*
        et_PulsePauseTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    float v = Float.parseFloat(s.toString());
                    if (v >10) {

						v=10;
                    }else{
                   //     v = v * 10;
                        sb_PulsePauseTime.setProgress((int) v);
                        et_PulsePauseTime.setSelection(s.length());
                    }
                } catch (Exception e) {
                }
            }
        });
        */


        sb_PulsePauseTime.setProgress(4);
        add_PulsePauseTime.setOnClickListener(v -> sb_PulsePauseTime.setProgress(sb_PulsePauseTime.getProgress() + 1));
        remove_PulsePauseTime.setOnClickListener(v -> sb_PulsePauseTime.setProgress(sb_PulsePauseTime.getProgress() - 1));


        final int step = 1;
        final int frequencyMin = 2;

        setSeekberThumb(sb_Frequency, mContext.getResources());
        sb_Frequency.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (!et_FrequencyFocus ) {
                    int value = frequencyMin + (progress * step);
                    if (value > 150) {
                        value = 150;
                    }
                    et_Frequency.setText(String.valueOf(value));
                    //sb_Frequency.setProgress(value);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
/*
        et_Frequency.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    int value = Integer.parseInt(s.toString());
                    if (value < 2) {

                    } else if (value > 150) {

                    } else {
                        value -= frequencyMin;
                        sb_Frequency.setProgress(value);
                        et_Frequency.setSelection(s.length());
                    }
                } catch (Exception e) {
                }
            }
        });
*/
        sb_Frequency.setProgress(83);// 2+83
        add_Frequency.setOnClickListener(v -> sb_Frequency.setProgress(sb_Frequency.getProgress() + 1));
        removeFrequency.setOnClickListener(v -> sb_Frequency.setProgress(sb_Frequency.getProgress() - 1));


        setSeekberThumb(sb_PulseWidth, mContext.getResources());

        sb_PulseWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int stepSize = 25;
                int value = 50 + progress * stepSize;

                if (value > 400) {
                    value = 400;
                }
                et_PulseWidth.setText(String.valueOf(value));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

/*
        et_PulseWidth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_PulseWidth.setOnEditorActionListener((v, actionId, event) -> {
            final int i = Integer.parseInt(et_PulseWidth.getText().toString());
            if (i < 50) {

                return true;
            }
            return false;
        });
*/
        sb_PulseWidth.setProgress(12);  //350 =  50+25* 12
        add_PulseWidth.setOnClickListener(v -> sb_PulseWidth.setProgress(sb_PulseWidth.getProgress() + 1));
        removePulseWidth.setOnClickListener(v -> sb_PulseWidth.setProgress(sb_PulseWidth.getProgress() - 1));

        setSeekberThumb(sb_PulseRiseTime, mContext.getResources());
        sb_PulseRiseTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            private boolean sb_pulseRiseTimeTracking;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!et_PulseRiseTimeFocus || sb_pulseRiseTimeTracking) {
                    final String pattern = "#.#";
                    final DecimalFormat decimalFormat = new DecimalFormat(pattern);
                    float value = (float) (progress / 100.0);
                    et_PulseRiseTime.setText(String.valueOf(decimalFormat.format(value)));
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                sb_pulseRiseTimeTracking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sb_pulseRiseTimeTracking = false;
            }
        });


        sb_PulseRiseTime.setProgress(100);
/*
        et_PulseRiseTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    float v = Float.parseFloat(s.toString());
                    if (v > 1) {

                    } else {
                        v = v * 100;
                        sb_PulseRiseTime.setProgress((int) v);
                        et_PulseRiseTime.setSelection(s.length());
                    }

                } catch (Exception e) {
                }
            }
        });
*/

        add_PulseRiseTime.setOnClickListener(v -> {
            float v1 = Float.parseFloat(et_PulseRiseTime.getText().toString()) + 0.1f;
            if (v1 > 1) {
                return;
            }
            et_PulseRiseTime.setText(String.format("%.1f", v1));
            sb_PulseRiseTime.setProgress( (int)(v1 * 100));


        });

        remove_PulseRiseTime.setOnClickListener(v -> {
            float v1 = Float.parseFloat(et_PulseRiseTime.getText().toString()) - 0.1f;
            if (v1 < 0.0) {
                return;
            }
            et_PulseRiseTime.setText(String.format("%.1f", v1));
            sb_PulseRiseTime.setProgress( (int)(v1 * 100));
        });


    }

    Button.OnClickListener saveOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isCheckDialog()) {
                /*
                final EditText et_exercisePlanName = (EditText) findViewById(R.id.et_exercisePlanName);

                final EditText et_StimulusIntensity = (EditText) findViewById(R.id.et_StimulusIntensity);
                final EditText et_PulseOperationTime = (EditText) findViewById(R.id.et_PulseOperationTime);
                final EditText et_PulsePauseTime = (EditText) findViewById(R.id.et_PulsePauseTime);
                final EditText et_Frequency = (EditText) findViewById(R.id.et_Frequency);
                final EditText et_PulseWidth = (EditText) findViewById(R.id.et_PulseWidth);
                final EditText et_PulseRiseTime = (EditText) findViewById(R.id.et_PulseRiseTime);
                */
                final TextView et_exercisePlanName = (TextView) findViewById(R.id.et_exercisePlanName);
                final TextView et_StimulusIntensity = (TextView) findViewById(R.id.et_StimulusIntensity);
                final TextView et_PulseOperationTime = (TextView) findViewById(R.id.et_PulseOperationTime);
                final TextView et_PulsePauseTime = (TextView) findViewById(R.id.et_PulsePauseTime);
                final TextView et_Frequency = (TextView) findViewById(R.id.et_Frequency);
                final TextView et_PulseWidth = (TextView) findViewById(R.id.et_PulseWidth);
                final TextView et_PulseRiseTime = (TextView) findViewById(R.id.et_PulseRiseTime);

                final CheckBox cb_state = (CheckBox) findViewById(R.id.cb_state);
                final SeekBar sb_StimulusIntensity = (SeekBar) findViewById(R.id.sb_StimulusIntensity);
                final SeekBar sb_PulseOperationTime = (SeekBar) findViewById(R.id.sb_PulseOperationTime);
                final SeekBar sb_PulsePauseTime = (SeekBar) findViewById(R.id.sb_PulsePauseTime);
                final SeekBar sb_Frequency = (SeekBar) findViewById(R.id.sb_Frequency);
                final SeekBar sb_PulseWidth = (SeekBar) findViewById(R.id.sb_PulseWidth);
                final SeekBar sb_PulseRiseTime = (SeekBar) findViewById(R.id.sb_PulseRiseTime);
                final HashMap<String, String> ProgramMap = new HashMap<>();
                ProgramMap.put(ProgramName, et_exercisePlanName.getText().toString());
                if (cb_state.isChecked()) {
                    ProgramMap.put(ProgramState, "activation");
                } else {
                    ProgramMap.put(ProgramState, "Inactive");
                }
                ProgramMap.put(ProgramTitle, parent.getString(R.string.CustomPrograms));
                ProgramMap.put(ProgramTime, String.valueOf(BudUtil.getInstance().calcMinute(initialTime)));
                ProgramMap.put(ProgramType, "Custom");
                ProgramMap.put(ProgramExplanation, null);
                ProgramMap.put(ProgramConstructor, null);
                ProgramMap.put(ProgramRegDate, BudUtil.getInstance().getToday("yyyy.MM.dd"));
                ProgramMap.put(ProgramPulseOperationTime, et_PulseOperationTime.getText().toString());
                ProgramMap.put(ProgramPulseOperationTimeProgress, String.valueOf(sb_PulseOperationTime.getProgress()));
                ProgramMap.put(ProgramStimulusIntensityProgress, String.valueOf(sb_StimulusIntensity.getProgress()));
                ProgramMap.put(ProgramStimulusIntensity, et_StimulusIntensity.getText().toString());
                ProgramMap.put(ProgramPulsePauseTime, et_PulsePauseTime.getText().toString());
                ProgramMap.put(ProgramPulsePauseTimeProgress, String.valueOf(sb_PulsePauseTime.getProgress()));
                ProgramMap.put(ProgramFrequency, et_Frequency.getText().toString());
                ProgramMap.put(ProgramFrequencyProgress, String.valueOf(sb_Frequency.getProgress()));
                ProgramMap.put(ProgramPulseWidth, et_PulseWidth.getText().toString());
                ProgramMap.put(ProgramPulseWidthProgress, String.valueOf(sb_PulseWidth.getProgress()));
                ProgramMap.put(ProgramPulseRiseTime, et_PulseRiseTime.getText().toString());
                ProgramMap.put(ProgramPulseRiseTimeProgress, String.valueOf(sb_PulseRiseTime.getProgress()));

                final DBQuery dbQuery = new DBQuery(getApplicationContext());
                if (dbQuery.newProgramInsert(ProgramMap)) {
                    Log.d("DialogNewProgram", "저장성공");
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                } else {

                }
            }
        }
    };

    private boolean isCheckDialog() {

        final DBQuery dbQuery = new DBQuery(mContext);
        final TextView et_exercisePlanName = (TextView) findViewById(R.id.et_exercisePlanName);
        final TextView et_StimulusIntensity = (TextView) findViewById(R.id.et_StimulusIntensity);
        final TextView et_PulseOperationTime = (TextView) findViewById(R.id.et_PulseOperationTime);
        final TextView et_PulsePauseTime = (TextView) findViewById(R.id.et_PulsePauseTime);
        final TextView et_Frequency = (TextView) findViewById(R.id.et_Frequency);
        final TextView et_PulseWidth = (TextView) findViewById(R.id.et_PulseWidth);
        final TextView et_PulseRiseTime = (TextView) findViewById(R.id.et_PulseRiseTime);

        if (et_exercisePlanName.getText().length() == 0) {
            BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.programNameEmpty), (dialog, which) -> dialog.show());
            return false;
        } else if (et_StimulusIntensity.getText().length() == 0) {
            BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.stimulusIntensity_error), (dialog, which) -> dialog.show());
            return false;
        } else if (et_PulseOperationTime.getText().length() == 0) {
            BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.PulseOperationTime_error), (dialog, which) -> dialog.show());
            return false;
        } else if (et_PulsePauseTime.getText().length() == 0) {
            BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.PulsePauseTime_error), (dialog, which) -> dialog.show());
            return false;
        } else if (et_Frequency.getText().length() == 0) {
            BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.Frequency_error), (dialog, which) -> dialog.show());
            return false;
        } else if (et_PulseWidth.getText().length() == 0) {
            BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.PulseWidth_error), (dialog, which) -> dialog.show());
            return false;
        } else if (et_PulseRiseTime.getText().length() == 0) {
            BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.PulseRiseTime_error), (dialog, which) -> dialog.show());
            return false;
        } else if (initialTime == 0) {
            BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.time_error), (dialog, which) -> dialog.show());
            return false;
        } else if (initialTime > 60) {
            BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.time_six_error), (dialog, which) -> dialog.show());
            return false;
        } else if (initialTime < 0) {
            BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.time_six_error), ((dialog, which) -> dialog.show()));
            return false;
        } else if (Float.parseFloat(et_PulseOperationTime.getText().toString()) < 0 || Float.parseFloat(et_PulseOperationTime.getText().toString()) > 10) {
            BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.program_error1), (dialog, which) -> dialog.show());
            return false;
        }else if (Float.parseFloat(et_PulsePauseTime.getText().toString()) < 0 || Float.parseFloat(et_PulsePauseTime.getText().toString()) > 10) {
            BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.program_error2), (dialog, which) -> dialog.show());
            return false;
        }else if (Integer.parseInt(et_Frequency.getText().toString()) < 2 || Integer.parseInt(et_Frequency.getText().toString()) > 150) {
            BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.program_error3), (dialog, which) -> dialog.show());
            return false;
        }else if (Integer.parseInt(et_PulseWidth.getText().toString()) < 50 || Integer.parseInt(et_PulseWidth.getText().toString()) > 400) {
            BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.program_error4), (dialog, which) -> dialog.show());
            return false;
        }else if (Float.parseFloat(et_PulseRiseTime.getText().toString()) < 0 || Float.parseFloat(et_PulseRiseTime.getText().toString()) > 1) {
            BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.program_error5), (dialog, which) -> dialog.show());
            return false;
        }
        boolean programName = dbQuery.getIsProgramName(et_exercisePlanName.getText().toString());
        if (!programName) {
            BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.programName_error), (dialog, which) -> dialog.show());
            return false;
        }

        return true;
    }

    Button.OnClickListener cancelOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
    };


    public static void downKeyboard(Context context, EditText editText) {
        InputMethodManager mInputMethodManager = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public static void setSeekberThumb(final SeekBar seekBar, final Resources res) {
        seekBar.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                /*
                if (seekBar.getHeight() > 0) {

                    Drawable thumb = res.getDrawable(R.drawable.slide_dot);
                    int h = seekBar.getMeasuredHeight() / 2;
                    int w = h;
                    Bitmap bmpOrg = ((BitmapDrawable) thumb).getBitmap();
                    Bitmap bmpScaled = Bitmap.createScaledBitmap(bmpOrg, w, h, true);
                    Drawable newThumb = new BitmapDrawable(res, bmpScaled);
                    newThumb.setBounds(0, 0, newThumb.getIntrinsicWidth(), newThumb.getIntrinsicHeight());
                    seekBar.setThumb(newThumb);
                    seekBar.getViewTreeObserver().removeOnPreDrawListener(this);
                }*/
                return true;
            }
        });
    }
}