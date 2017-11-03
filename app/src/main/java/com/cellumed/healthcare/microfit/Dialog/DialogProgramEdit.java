package com.cellumed.healthcare.microfit.Dialog;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.cellumed.healthcare.microfit.DAO.DAO_Program;
import com.cellumed.healthcare.microfit.DataBase.DBQuery;
import com.cellumed.healthcare.microfit.DataBase.SqlImp;
import com.cellumed.healthcare.microfit.Home.Imp_DBOK;
import com.cellumed.healthcare.microfit.R;
import com.cellumed.healthcare.microfit.Util.BudUtil;

import java.text.DecimalFormat;
import java.util.HashMap;

/**
 * Created by test on 2016-11-20.
 */
public class DialogProgramEdit implements SqlImp {
    private Context mContext;
    private Imp_DBOK mDbok;
    private DAO_Program dao_program;
    private boolean et_PulseOperationTimeFocus = false;
    private boolean et_PulsePauseTimeFocus = false;
    private boolean et_PulseRiseTimeFocus = false;
    private boolean et_FrequencyFocus = false;

    final int pulseWidthMin = 50;

    public DialogProgramEdit (Context mContext, DAO_Program dao_program, Imp_DBOK mDbok) {
        this.mContext = mContext;
        this.mDbok = mDbok;
        this.dao_program = dao_program;
        showEditProgram();
    }

    private boolean isCheckDialog(MaterialDialog materialDialog) {
        /*final EditText et_exercisePlanName = (EditText) materialDialog.findViewById(R.id.et_exercisePlanName);
        final EditText et_time = (EditText) materialDialog.findViewById(R.id.et_time);
        final EditText et_StimulusIntensity = (EditText) materialDialog.findViewById(R.id.et_StimulusIntensity);
        final EditText et_PulseOperationTime = (EditText) materialDialog.findViewById(R.id.et_PulseOperationTime);
        final EditText et_PulsePauseTime = (EditText) materialDialog.findViewById(R.id.et_PulsePauseTime);
        final EditText et_Frequency = (EditText) materialDialog.findViewById(R.id.et_Frequency);
        final EditText et_PulseWidth = (EditText) materialDialog.findViewById(R.id.et_PulseWidth);
        final EditText et_PulseRiseTime = (EditText) materialDialog.findViewById(R.id.et_PulseRiseTime);
        */
        final TextView et_exercisePlanName = (TextView) materialDialog.findViewById(R.id.et_exercisePlanName);
        final TextView et_time = (TextView) materialDialog.findViewById(R.id.et_time);
        final TextView et_StimulusIntensity = (TextView) materialDialog.findViewById(R.id.et_StimulusIntensity);
        final TextView et_PulseOperationTime = (TextView) materialDialog.findViewById(R.id.et_PulseOperationTime);
        final TextView et_PulsePauseTime = (TextView) materialDialog.findViewById(R.id.et_PulsePauseTime);
        final TextView et_Frequency = (TextView) materialDialog.findViewById(R.id.et_Frequency);
        final TextView et_PulseWidth = (TextView) materialDialog.findViewById(R.id.et_PulseWidth);
        final TextView et_PulseRiseTime = (TextView) materialDialog.findViewById(R.id.et_PulseRiseTime);

        if (et_exercisePlanName.getText().length() == 0) {
            BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.programNameEmpty), (dialog, which) -> materialDialog.show());
            return false;
        } else if (et_StimulusIntensity.getText().length() == 0) {
            BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.stimulusIntensity_error), (dialog, which) -> materialDialog.show());
            return false;
        } else if (et_PulseOperationTime.getText().length() == 0) {
            BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.PulseOperationTime_error), (dialog, which) -> materialDialog.show());
            return false;
        } else if (et_PulsePauseTime.getText().length() == 0) {
            BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.PulsePauseTime_error), (dialog, which) -> materialDialog.show());
            return false;
        } else if (et_Frequency.getText().length() == 0) {
            BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.Frequency_error), (dialog, which) -> materialDialog.show());
            return false;
        } else if (et_PulseWidth.getText().length() == 0) {
            BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.PulseWidth_error), (dialog, which) -> materialDialog.show());
            return false;
        } else if (et_PulseRiseTime.getText().length() == 0) {
            BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.PulseRiseTime_error), (dialog, which) -> materialDialog.show());
            return false;
        } else if (et_time.getText().length() == 0) {
            BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.time_error), (dialog, which) -> materialDialog.show());
            return false;
        } else if (Integer.parseInt(et_time.getText().toString()) > 60|| Integer.parseInt(et_time.getText().toString()) <0) {
            BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.time_six_error), (dialog, which) -> materialDialog.show());
            return false;
        }else if (Float.parseFloat(et_PulseOperationTime.getText().toString()) < 0 || Float.parseFloat(et_PulseOperationTime.getText().toString()) > 10) {
            BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.program_error1), (dialog, which) -> materialDialog.show());
            return false;
        }else if (Float.parseFloat(et_PulsePauseTime.getText().toString()) < 0 || Float.parseFloat(et_PulsePauseTime.getText().toString()) > 10) {
            BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.program_error2), (dialog, which) -> materialDialog.show());
            return false;
        }else if (Integer.parseInt(et_Frequency.getText().toString()) < 2 || Integer.parseInt(et_Frequency.getText().toString()) > 150) {
            BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.program_error3), (dialog, which) -> materialDialog.show());
            return false;
        }else if (Integer.parseInt(et_PulseWidth.getText().toString()) < 50 || Integer.parseInt(et_PulseWidth.getText().toString()) > 400) {
            BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.program_error4), (dialog, which) -> materialDialog.show());
            return false;
        }else if (Float.parseFloat(et_PulseRiseTime.getText().toString()) < 0 || Float.parseFloat(et_PulseRiseTime.getText().toString()) > 1) {
            BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.program_error5), (dialog, which) -> materialDialog.show());
            return false;
        }

        return true;
    }


    private void showEditProgram() {

        MaterialDialog mMaterialDialog = new MaterialDialog.Builder(mContext)
                .title(mContext.getString(R.string.Program_modifications))
                .titleColor(Color.parseColor("#ffffff"))
                .positiveColor(Color.parseColor("#ffffff"))
                .negativeColor(Color.parseColor("#9b928e"))
                .backgroundColor(Color.parseColor("#ff6669"))
                .customView(R.layout.dialog_custom_program_edit, false)
                .positiveText(mContext.getString(R.string.save))
                .negativeText(mContext.getString(R.string.cancel))
                .onPositive((dialog, which) -> {
                    if (isCheckDialog(dialog)) {
                        final EditText et_exercisePlanName = (EditText) dialog.findViewById(R.id.et_exercisePlanName);
                        final EditText et_time = (EditText) dialog.findViewById(R.id.et_time);
                        final CheckBox cb_state = (CheckBox) dialog.findViewById(R.id.cb_state);


                        final TextView et_StimulusIntensity = (TextView) dialog.findViewById(R.id.et_StimulusIntensity);
                        final TextView et_PulseOperationTime = (TextView) dialog.findViewById(R.id.et_PulseOperationTime);
                        final TextView et_PulsePauseTime = (TextView) dialog.findViewById(R.id.et_PulsePauseTime);
                        final TextView et_Frequency = (TextView) dialog.findViewById(R.id.et_Frequency);
                        final TextView et_PulseWidth = (TextView) dialog.findViewById(R.id.et_PulseWidth);
                        final TextView et_PulseRiseTime = (TextView) dialog.findViewById(R.id.et_PulseRiseTime);
                       /* final EditText et_StimulusIntensity = (EditText) dialog.findViewById(R.id.et_StimulusIntensity);
                        final EditText et_PulseOperationTime = (EditText) dialog.findViewById(R.id.et_PulseOperationTime);
                        final EditText et_PulsePauseTime = (EditText) dialog.findViewById(R.id.et_PulsePauseTime);
                        final EditText et_Frequency = (EditText) dialog.findViewById(R.id.et_Frequency);
                        final EditText et_PulseWidth = (EditText) dialog.findViewById(R.id.et_PulseWidth);
                        final EditText et_PulseRiseTime = (EditText) dialog.findViewById(R.id.et_PulseRiseTime);
                        */
                        final SeekBar sb_StimulusIntensity = (SeekBar) dialog.findViewById(R.id.sb_StimulusIntensity);
                        final SeekBar sb_PulseOperationTime = (SeekBar) dialog.findViewById(R.id.sb_PulseOperationTime);
                        final SeekBar sb_PulsePauseTime = (SeekBar) dialog.findViewById(R.id.sb_PulsePauseTime);
                        final SeekBar sb_Frequency = (SeekBar) dialog.findViewById(R.id.sb_Frequency);
                        final SeekBar sb_PulseWidth = (SeekBar) dialog.findViewById(R.id.sb_PulseWidth);
                        final SeekBar sb_PulseRiseTime = (SeekBar) dialog.findViewById(R.id.sb_PulseRiseTime);
                        final HashMap<String, String> programMap = new HashMap<>();
                        final HashMap<String, String>favoritesMap = new HashMap<>();
                        programMap.put(ProgramName, et_exercisePlanName.getText().toString());
                        favoritesMap.put(FavoritesName, et_exercisePlanName.getText().toString());
                        programMap.put(ProgramTitle, mContext.getString(R.string.CustomPrograms));
                        favoritesMap.put(FavoritesTitle, mContext.getString(R.string.CustomPrograms));
                        if (cb_state.isChecked()) {
                            programMap.put(ProgramState, "activation");
                            favoritesMap.put(FavoritesState, "activation");
                        } else {
                            programMap.put(ProgramState, "Inactive");
                            favoritesMap.put(FavoritesState, "Inactive");
                        }
                        programMap.put(ProgramTime, String.valueOf(BudUtil.getInstance().calcMinute(Integer.parseInt(et_time.getText().toString()))));
                        favoritesMap.put(FavoritesTime, String.valueOf(BudUtil.getInstance().calcMinute(Integer.parseInt(et_time.getText().toString()))));
                        programMap.put(ProgramType, "Custom");
                        favoritesMap.put(FavoritesType2, "Custom");
                        programMap.put(ProgramExplanation, null);
                        favoritesMap.put(FavoritesExplanation, null);
                        programMap.put(ProgramConstructor, dao_program.getProgramConstructor());
                        favoritesMap.put(FavoritesConstructor,  dao_program.getProgramConstructor());
                        programMap.put(ProgramRegDate, dao_program.getProgramRegDate());
                        favoritesMap.put(FavoritesRegDate, dao_program.getProgramRegDate());
                        programMap.put(ProgramPulseOperationTime, et_PulseOperationTime.getText().toString());
                        favoritesMap.put(FavoritesPulseOperationTime, et_PulseOperationTime.getText().toString());
                        programMap.put(ProgramPulseOperationTimeProgress, String.valueOf(sb_PulseOperationTime.getProgress()));
                        programMap.put(ProgramStimulusIntensityProgress, String.valueOf(sb_StimulusIntensity.getProgress()));
                        programMap.put(ProgramStimulusIntensity, et_StimulusIntensity.getText().toString());
                        favoritesMap.put(FavoritesStimulusIntensity, et_StimulusIntensity.getText().toString());
                        programMap.put(ProgramPulsePauseTime, et_PulsePauseTime.getText().toString());
                        favoritesMap.put(FavoritesPulsePauseTime, et_PulsePauseTime.getText().toString());
                        programMap.put(ProgramPulsePauseTimeProgress, String.valueOf(sb_PulsePauseTime.getProgress()));
                        programMap.put(ProgramFrequency, et_Frequency.getText().toString());
                        favoritesMap.put(FavoritesFrequency, et_Frequency.getText().toString());
                        programMap.put(ProgramFrequencyProgress, String.valueOf(sb_Frequency.getProgress()));
                        programMap.put(ProgramPulseWidth, et_PulseWidth.getText().toString());
                        favoritesMap.put(FavoritesPulseWidth, et_PulseWidth.getText().toString());
                        programMap.put(ProgramPulseWidthProgress, String.valueOf(sb_PulseWidth.getProgress()));
                        programMap.put(ProgramPulseRiseTime, et_PulseRiseTime.getText().toString());
                        favoritesMap.put(FavoritesPulseRiseTime, et_PulseRiseTime.getText().toString());
                        programMap.put(ProgramPulseRiseTimeProgress, String.valueOf(sb_PulseRiseTime.getProgress()));
                        final DBQuery dbQuery = new DBQuery(mContext);
                        if (!dbQuery.isFavorites(dao_program.getIdx(),BudUtil.getShareValue(mContext,BudUtil.USER_ID))) {
                            if (dbQuery.setProgramUpdate(programMap,dao_program.getIdx())) {
                                mDbok.dbOK();
                            }
                        }else{
                            if (dbQuery.setProgramUpdateAndFavorites(programMap,favoritesMap,dao_program.getIdx())) {
                                mDbok.dbOK();
                            }
                        }

                    } else {
                        dialog.show();
                    }

                }).onNegative((dialog, which) ->
                        dialog.dismiss()
                ).show();
        mMaterialDialog.setCanceledOnTouchOutside(false);
        final SeekBar sb_StimulusIntensity = (SeekBar) mMaterialDialog.findViewById(R.id.sb_StimulusIntensity);
        final SeekBar sb_PulseOperationTime = (SeekBar) mMaterialDialog.findViewById(R.id.sb_PulseOperationTime);
        final SeekBar sb_PulsePauseTime = (SeekBar) mMaterialDialog.findViewById(R.id.sb_PulsePauseTime);
        final SeekBar sb_Frequency = (SeekBar) mMaterialDialog.findViewById(R.id.sb_Frequency);
        final SeekBar sb_PulseWidth = (SeekBar) mMaterialDialog.findViewById(R.id.sb_PulseWidth);
        final SeekBar sb_PulseRiseTime = (SeekBar) mMaterialDialog.findViewById(R.id.sb_PulseRiseTime);
       /* final EditText et_StimulusIntensity = (EditText) mMaterialDialog.findViewById(R.id.et_StimulusIntensity);
        final EditText et_PulseOperationTime = (EditText) mMaterialDialog.findViewById(R.id.et_PulseOperationTime);
        final EditText et_PulsePauseTime = (EditText) mMaterialDialog.findViewById(R.id.et_PulsePauseTime);
        final EditText et_Frequency = (EditText) mMaterialDialog.findViewById(R.id.et_Frequency);
        final EditText et_PulseWidth = (EditText) mMaterialDialog.findViewById(R.id.et_PulseWidth);
        final EditText et_PulseRiseTime = (EditText) mMaterialDialog.findViewById(R.id.et_PulseRiseTime);
        */
        final TextView et_StimulusIntensity = (TextView) mMaterialDialog.findViewById(R.id.et_StimulusIntensity);
        final TextView et_PulseOperationTime = (TextView) mMaterialDialog.findViewById(R.id.et_PulseOperationTime);
        final TextView et_PulsePauseTime = (TextView) mMaterialDialog.findViewById(R.id.et_PulsePauseTime);
        final TextView et_Frequency = (TextView) mMaterialDialog.findViewById(R.id.et_Frequency);
        final TextView et_PulseWidth = (TextView) mMaterialDialog.findViewById(R.id.et_PulseWidth);
        final TextView et_PulseRiseTime = (TextView) mMaterialDialog.findViewById(R.id.et_PulseRiseTime);

        final Button add_StimulusIntensity = (Button) mMaterialDialog.findViewById(R.id.add_StimulusIntensity);
        final Button add_PulseOperationTime = (Button) mMaterialDialog.findViewById(R.id.add_PulseOperationTime);
        final Button add_PulsePauseTime = (Button) mMaterialDialog.findViewById(R.id.add_PulsePauseTime);
        final Button add_Frequency = (Button) mMaterialDialog.findViewById(R.id.add_Frequency);
        final Button add_PulseWidth = (Button) mMaterialDialog.findViewById(R.id.add_PulseWidth);
        final Button add_PulseRiseTime = (Button) mMaterialDialog.findViewById(R.id.add_PulseRiseTime);
        final Button remove_StimulusIntensity = (Button) mMaterialDialog.findViewById(R.id.remove_StimulusIntensity);
        final Button remove_PulseOperationTime = (Button) mMaterialDialog.findViewById(R.id.remove_PulseOperationTime);
        final Button remove_PulsePauseTime = (Button) mMaterialDialog.findViewById(R.id.remove_PulsePauseTime);
        final Button removeFrequency = (Button) mMaterialDialog.findViewById(R.id.removeFrequency);
        final Button removePulseWidth = (Button) mMaterialDialog.findViewById(R.id.removePulseWidth);
        final Button remove_PulseRiseTime = (Button) mMaterialDialog.findViewById(R.id.remove_PulseRiseTime);
        final EditText et_exercisePlanName = (EditText) mMaterialDialog.findViewById(R.id.et_exercisePlanName);
        final EditText et_time = (EditText) mMaterialDialog.findViewById(R.id.et_time);
        final CheckBox cb_state = (CheckBox) mMaterialDialog.findViewById(R.id.cb_state);


        boolean isExcActivated=false;
        final DBQuery dbQuery = new DBQuery(mContext);
        if (dbQuery.getExercisePlanCountUsingProg(dao_program.getProgramName()) > 0) {
            isExcActivated=true;
        }



        et_exercisePlanName.setText(dao_program.getProgramName());
        if(isExcActivated)
        {
            et_exercisePlanName.setEnabled(false);
            et_exercisePlanName.setClickable(true);
            et_exercisePlanName.setOnClickListener(new TextView.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "사용중인 프로그램 이름은 변경할 수 없습니다", Toast.LENGTH_SHORT).show();
                }
            });

        }

        et_time.setText(String.valueOf(secondToMinute(Integer.parseInt(dao_program.getProgramTime()))));
        if (dao_program.getProgramState().equals("activation")) {
            cb_state.setChecked(true);
        } else {
            cb_state.setChecked(false);
        }

        et_exercisePlanName.setImeOptions(EditorInfo.IME_ACTION_DONE);
        et_exercisePlanName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    et_exercisePlanName.clearFocus();
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
                        int initialTime = Integer.parseInt(et_time.getText().toString());
                       // et_time.setText(et_time.getText() + "분");
                        return true;
                    }
                    else
                    {
                        et_time.setText("");
                        MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext);
                        builder
                                .title(mContext.getString(R.string.notice))
                                .titleColor(Color.parseColor("#FF6669"))
                                .content(mContext.getString(R.string.NumberError))
                                .positiveText(mContext.getString(R.string.ok))
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

        cb_state.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton bView, boolean isChecked)
            {
                if(bView.getId() == R.id.cb_state && !isChecked)
                {
                    final DBQuery dbQuery = new DBQuery(mContext);
                    if (dbQuery.getExercisePlanCountUsingProg(dao_program.getProgramName()) > 0)
                    {
                        bView.setChecked(true);
                        //Toast.makeText(mContext, mContext.getResources().getString( R.string.ConnectFailed), Toast.LENGTH_SHORT).show();
                        Toast.makeText(mContext, "사용중인 프로그램은 비활성화할 수 없습니다", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        });


        /*
        et_StimulusIntensity.setImeOptions(EditorInfo.IME_ACTION_DONE);
        et_StimulusIntensity.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        et_StimulusIntensity.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    et_StimulusIntensity.clearFocus();
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

        et_PulseOperationTime.setImeOptions(EditorInfo.IME_ACTION_DONE);
        et_PulseOperationTime.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        et_PulseOperationTime.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    et_PulseOperationTime.clearFocus();
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
                    et_PulsePauseTime.clearFocus();
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
                    et_PulseRiseTime.clearFocus();
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
                    et_Frequency.clearFocus();;
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
//                    et_StimulusIntensity.setSelection();
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
                    BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.pleasefive), (dialog, which) -> {
                        dialog.dismiss();
                        mMaterialDialog.show();
                    });
                    value=50;
                    et_PulseWidth.setText(String.valueOf(value));
                }
                else if (value > 400) {
                    BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.pleaseonefore), (dialog, which) -> {
                        dialog.dismiss();
                        mMaterialDialog.show();
                    });
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

        sb_StimulusIntensity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d("DialogEditProgram", "progress:" + progress);
                et_StimulusIntensity.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        Log.d("DialogEditProgram", "Integer.parseInt(dao_program.getProgramStimulusIntensityProgress()):" + Integer.parseInt(dao_program.getProgramStimulusIntensityProgress()));
        sb_StimulusIntensity.setProgress(Integer.parseInt(dao_program.getProgramStimulusIntensityProgress()));
        add_StimulusIntensity.setOnClickListener(v -> sb_StimulusIntensity.setProgress(sb_StimulusIntensity.getProgress() + 1));
        remove_StimulusIntensity.setOnClickListener(v -> sb_StimulusIntensity.setProgress(sb_StimulusIntensity.getProgress() - 1));
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
                        BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.pleaseoneten), (dialog, which) -> {
                            dialog.dismiss();
                            mMaterialDialog.show();
                        });
						v=10;
                    }else if(v<1) {
                        v = 1;
                    }
                        sb_PulseOperationTime.setProgress((int) v);
                        //et_PulseOperationTime.setSelection(s.length());
                } catch (Exception e) {
                }
            }
        });
        */
        sb_PulseOperationTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private boolean sb_pulseOperationTimeTracking;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


               // if (!et_PulseOperationTimeFocus || sb_pulseOperationTimeTracking) {
                    //float value = (float) (progress / 10.0);
                    float value = (float) (progress +1);
                    if (value == 10.0) {
                        et_PulseOperationTime.setText(String.valueOf((int) value));
                    } else {
                        et_PulseOperationTime.setText(String.valueOf((int) value));

                    }

                //}
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
        sb_PulseOperationTime.setProgress( Integer.parseInt(dao_program.getProgramPulseOperationTimeProgress()) < 1 ? 1 : Integer.parseInt(dao_program.getProgramPulseOperationTimeProgress()));
        add_PulseOperationTime.setOnClickListener(v -> sb_PulseOperationTime.setProgress(sb_PulseOperationTime.getProgress() + 1));
        remove_PulseOperationTime.setOnClickListener(v -> sb_PulseOperationTime.setProgress(sb_PulseOperationTime.getProgress() - 1));





        sb_PulsePauseTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            private boolean sb_pulsePauseTimeTracking;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!et_PulsePauseTimeFocus || sb_pulsePauseTimeTracking) {
                    float value = (float) (progress); // / 10.0);
                    if (value == 10.0) {
                        et_PulsePauseTime.setText(String.valueOf((int) value));
                    } else {
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
                        BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.pleaseoneten), (dialog, which) -> {
                            dialog.dismiss();
                            mMaterialDialog.show();

                        });
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
        sb_PulsePauseTime.setProgress( Integer.parseInt(dao_program.getProgramPulsePauseTimeProgress()));
        add_PulsePauseTime.setOnClickListener(v -> sb_PulsePauseTime.setProgress(sb_PulsePauseTime.getProgress() + 1));
        remove_PulsePauseTime.setOnClickListener(v -> sb_PulsePauseTime.setProgress(sb_PulsePauseTime.getProgress() - 1));


        final int step = 1;
        final int frequencyMin = 2;
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
                if(et_FrequencyFocus) et_Frequency.clearFocus();
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
                        BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.pleasetwo), (dialog, which) -> {
                            dialog.dismiss();
                            mMaterialDialog.show();
                        });
                    } else if (value > 150) {
                        BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.pleaseonefive), (dialog, which) -> {
                            dialog.dismiss();
                            mMaterialDialog.show();
                        });
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
        sb_Frequency.setProgress(Integer.parseInt(dao_program.getProgramFrequencyProgress()));
        add_Frequency.setOnClickListener(v -> sb_Frequency.setProgress(sb_Frequency.getProgress() + 1));
        removeFrequency.setOnClickListener(v -> sb_Frequency.setProgress(sb_Frequency.getProgress() - 1));




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
*/

        // 값 초기화
        int pw_int=Integer.parseInt(dao_program.getProgramPulseWidthProgress());
        if(pw_int==0) sb_PulseWidth.setProgress(1); // 강제로 setProgress에서 changed부르기위해서
        sb_PulseWidth.setProgress(pw_int);

        add_PulseWidth.setOnClickListener(v -> sb_PulseWidth.setProgress(sb_PulseWidth.getProgress() + 1));
        removePulseWidth.setOnClickListener(v -> sb_PulseWidth.setProgress(sb_PulseWidth.getProgress() - 1));


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
                        BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.pleaseone), (dialog, which) -> {
                            dialog.dismiss();
                            mMaterialDialog.show();
                        });
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
        sb_PulseRiseTime.setProgress(Integer.parseInt(dao_program.getProgramPulseRiseTimeProgress()));
     //   add_PulseRiseTime.setOnClickListener(v -> sb_PulseRiseTime.setProgress(sb_PulseRiseTime.getProgress() + 1));
    //    remove_PulseRiseTime.setOnClickListener(v -> sb_PulseRiseTime.setProgress(sb_PulseRiseTime.getProgress() - 1));

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


    public int secondToMinute(int num) {
        int minute = num % 3600 / 60;
        if (minute==0) {
            return  num/60;
        }else{
            return minute;

        }
    }

    public static void downKeyboard(Context context, EditText editText) {
        InputMethodManager mInputMethodManager = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }
}
