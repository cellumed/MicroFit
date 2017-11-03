package com.cellumed.healthcare.microfit.Setting;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.cellumed.healthcare.microfit.Bluetooth.BTConnectActivity;
import com.cellumed.healthcare.microfit.DAO.DAO_DialogNewExercisePaln;
import com.cellumed.healthcare.microfit.DAO.DAO_ExercisePlan;
import com.cellumed.healthcare.microfit.DAO.DAO_Program;
import com.cellumed.healthcare.microfit.DataBase.DBQuery;
import com.cellumed.healthcare.microfit.DataBase.SqlImp;
import com.cellumed.healthcare.microfit.Home.Imp_DBOK;
import com.cellumed.healthcare.microfit.Home.Imp_RepeatCount;
import com.cellumed.healthcare.microfit.R;
import com.cellumed.healthcare.microfit.Util.BudUtil;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Act_AddNewExercisePlan extends BTConnectActivity implements Imp_RepeatCount, SqlImp {
    @Bind(R.id.et_exercisePlanName)
    EditText etExercisePlanName;
    @Bind(R.id.cb_state)
    CheckBox cbState;
    @Bind(R.id.sp_program)
    Spinner spProgram;
    @Bind(R.id.et_time)
    EditText etTime;
    @Bind(R.id.bt_add)
    Button btAdd;
    @Bind(R.id.tv_cnt)
    TextView tvCnt;
    @Bind(R.id.lv_list)
    ListView lvList;
    @Bind(R.id.saveDone)
    Button saveDone;
    @Bind(R.id.cancel)
    Button cancel;

    private final int RESULT_OK = 1;

    private ArrayList<DAO_DialogNewExercisePaln> items;
    private Adapter_DialogNewExercisePlan adapter;
    private Context mContext;
    private  ArrayList<String> spNameSave ;
    private Imp_DBOK mDbOk;

    private int initialTime = 0;
    @Override
    protected void connectedDevice() {}

    @Override
    protected void dataAvailableCheck(String data) {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_add_new_exercise_plan);
        setCustomActionbar();
        mContext = this;
        spNameSave = new ArrayList<>();
        ButterKnife.bind(this);
        items = new ArrayList<>();

        etExercisePlanName.setImeOptions(EditorInfo.IME_ACTION_DONE);
        etExercisePlanName.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    downKeyboard(mContext, etExercisePlanName);
                    return true;
                }
                return false;
            }
        });


        etTime.setImeOptions(EditorInfo.IME_ACTION_DONE);
        etTime.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etTime.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    downKeyboard(mContext, etTime);
                    if(etTime.length() > 0 && TextUtils.isDigitsOnly(etTime.getText()))
                    {
                        initialTime = Integer.parseInt(etTime.getText().toString());
                        etTime.setText(etTime.getText() + getString(R.string.minute));
                        return true;
                    }
                    else
                    {
                        etTime.setText("");
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

        ArrayList<DAO_Program> allProgram = new DBQuery(mContext).getProgramsForNewExercisePlan();
        ArrayList<String> name = new ArrayList<>();
        tvCnt.setText(String.format(mContext.getString(R.string.repetitions),0,0));
        spNameSave.add(mContext.getString(R.string.program_error));
        for (DAO_Program dao_program : allProgram) {
            if (dao_program.getProgramType().equals("Basic")) {
                spNameSave.add(dao_program.getProgramName());
                name.add(BudUtil.getInstance().getName(mContext,dao_program.getProgramName()));
            }else{
                spNameSave.add(dao_program.getProgramName());
                name.add(dao_program.getProgramName());
            }

        }

        spProgram.setAdapter(new ArrayAdapter<>(mContext, R.layout.custom_spin_dropdown, spNameSave));
        spProgram.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                    if (!parent.getSelectedItem().toString().equals(mContext.getString(R.string.program_error))) {
                        setCount();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btAdd.setOnClickListener(v -> {
            if (initialTime > 0
                    && !spProgram.getSelectedItem().toString().equals(mContext.getString(R.string.program_error))) {
                addData();
            } else {
                if (spProgram.getSelectedItem().toString().equals(mContext.getString(R.string.program_error))) {
                    BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.program_error), (dialog, which) -> dialog.show());
                } else if (initialTime == 0) {
                    BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.time_error), (dialog, which) -> dialog.show());
                }
            }
        });

        etTime.setRawInputType(Configuration.KEYBOARD_12KEY);
        etTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setCount();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        lvList = (ListView) findViewById(R.id.lv_list);
        adapter = new Adapter_DialogNewExercisePlan(mContext, items, this);
        lvList.setAdapter(adapter);
        lvList.setDivider(null);


        saveDone.setOnClickListener(v -> {
            if (isCheckDialog()) {
                final HashMap<String, String> exercisePlanMap = new HashMap<>();
                exercisePlanMap.put(ExercisePlanName, etExercisePlanName.getText().toString());
                exercisePlanMap.put(ExercisePlanTitle, mContext.getString(R.string.Custom_workout_plans));
                if (cbState.isChecked()) {
                    exercisePlanMap.put(ExercisePlanState, "activation");
                } else {
                    exercisePlanMap.put(ExercisePlanState, "Inactive");
                }

                if (!spNameSave.get(spProgram.getSelectedItemPosition()).equals(spProgram.getSelectedItem().toString())) {
                    exercisePlanMap.put(ExercisePlanProgramIsBasic, "true");
                } else {
                    exercisePlanMap.put(ExercisePlanProgramIsBasic, "false");
                }

                Log.e("CustomNewExercisePlanDi", spNameSave.get(spProgram.getSelectedItemPosition()));
                exercisePlanMap.put(ExercisePlanProgram, spNameSave.get(spProgram.getSelectedItemPosition()));

                exercisePlanMap.put(ExercisePlanType, "Custom");
                exercisePlanMap.put(ExercisePlanTime, String.valueOf(BudUtil.getInstance().calcMinute(initialTime)));
                exercisePlanMap.put(ExercisePlanExplanation, null);
                exercisePlanMap.put(ExercisePlanConstructor, null);
                exercisePlanMap.put(ExercisePlanRegDate, BudUtil.getInstance().getToday("yyyy.MM.dd"));
                exercisePlanMap.put(UserExplanation, null);
                for (int i = 0; i < items.size(); i++) {
                    try {
                        exercisePlanMap.put(ExercisePlanPosture, items.get(i).getExercisePlanName());
                        final String[] exerciseType = mContext.getResources().getStringArray(R.array.ExerciseType);
                        for (int i2 = 0; i2 < exerciseType.length; i2++) {
                            if (exerciseType[i2].equals(items.get(i).getExercisePlanName())) {
                                exercisePlanMap.put(ExercisePlanImageNumber, String.valueOf(i2));
                                break;

                            }
                        }
                        exercisePlanMap.put(ExercisePlanRepeatCount, String.valueOf(items.get(i).getRepeatCount()));

                        final DBQuery dbQuery = new DBQuery(mContext);
                        if (dbQuery.newExercisePlanInsert(exercisePlanMap)) {
                            Log.d("DialogNewExercisePlan", "저장성공");
                            Intent intent = new Intent();
                            setResult(RESULT_OK, intent);
                            finish();
                            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        }
                    } catch (Exception e) {
                        continue;
                    }

                }
            }
        });
        cancel.setOnClickListener(v -> {
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });
    }

    private void setCount() {
        try {
            int allRepeatCount1 = allRepeatCount();
            int repeatCount = 0;
            for (int i = 0; i < items.size(); i++) {
                try {
                    repeatCount += items.get(i).getRepeatCount();
                } catch (Exception e) {
                    continue;
                }


            }
            final int number = (int) remainingNumber(allRepeatCount1, repeatCount);
            btAdd.setEnabled(true);
            if (number < 0) {
                MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext);
                builder
                        .title(getString(R.string.warning))
                        .titleColor(Color.parseColor("#FF6669"))
                        .content(String.format(getString(R.string.repetitions_error2), Math.abs((float) number)))
                        .positiveText(getString(R.string.ok))
                        .positiveColor(Color.parseColor("#FF6669"))
                        .onPositive((dialog, which) -> {
                            dialog.dismiss();

                        }).show();
                items.get(items.size() - 1).setRepeatCount(0);
                setCount();
                adapter.notifyDataSetChanged();
                return;
            } else if (number == 0) {
                btAdd.setEnabled(false);

            }
            tvCnt.setText(String.format(mContext.getString(R.string.repetitions), number, allRepeatCount1));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void addData() {


        if (!items.isEmpty()) {
            if (items.get(items.size() - 1).getRepeatCount() > 0) {
                items.add(new DAO_DialogNewExercisePaln(0, 0));
            } else {
                MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext);
                builder
                        .title(getString(R.string.warning))
                        .titleColor(Color.parseColor("#FF6669"))
                        .content(getString(R.string.RepeatCount_error))
                        .positiveText(getString(R.string.ok))
                        .positiveColor(Color.parseColor("#FF6669"))
                        .onPositive((dialog, which) -> {
                            dialog.dismiss();

                        }).show();
            }

        } else {
            items.add(new DAO_DialogNewExercisePaln(0, 0));
        }
        adapter.notifyDataSetChanged();

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
                    public void onClick(View v) {
                        finish();
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    }
                }
        );

    }

    @Override
    public void RepeatCountOk() {
        setCount();
    }

    @Override
    public void RepeatCountInput() {
        setCount();
    }

    private float allRepeatCount(int programTime, float oneRepeatCount) {
        return programTime / oneRepeatCount;

    }

    private float remainingNumber(float allCount, float nowCount) {
        return allCount - nowCount;
    }

    private int allRepeatCount() {
        final DBQuery dbQuery = new DBQuery(mContext);
        int allRepeatCount = 0;

        try {
            final String sp_program = spNameSave.get(spProgram.getSelectedItemPosition());
            final int programTime = BudUtil.getInstance().calcMinute(initialTime);
            final float pulseOperationTime = Float.parseFloat(dbQuery.getProgramPulseOperationTime(sp_program));
            final float pulsePauseTime = Float.parseFloat(dbQuery.getProgramPulsePauseTime(sp_program));
            allRepeatCount = (int) allRepeatCount(programTime, (pulseOperationTime + pulsePauseTime));
        } catch (Exception e) {
        }
        dbQuery.getDb().close();
        return allRepeatCount;
    }

    private boolean isCheckDialog() {

        final DBQuery dbQuery = new DBQuery(mContext);
        if (etExercisePlanName.getText().length() == 0) {
            MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext);
            builder
                    .title(getString(R.string.warning))
                    .titleColor(Color.parseColor("#FF6669"))
                    .content(getString(R.string.exercisePlanName))
                    .positiveText(getString(R.string.ok))
                    .positiveColor(Color.parseColor("#FF6669"))
                    .onPositive((dialog, which) -> {
                        dialog.dismiss();

                    }).show();
            return false;
        } else if (spProgram.getSelectedItem().toString().equals(mContext.getString(R.string.program_error))) {
            MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext);
            builder
                    .title(getString(R.string.warning))
                    .titleColor(Color.parseColor("#FF6669"))
                    .content(getString(R.string.program_error))
                    .positiveText(getString(R.string.ok))
                    .positiveColor(Color.parseColor("#FF6669"))
                    .onPositive((dialog, which) -> {
                        dialog.dismiss();

                    }).show();
            return false;
        } else if (initialTime == 0) {
            MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext);
            builder
                    .title(getString(R.string.warning))
                    .titleColor(Color.parseColor("#FF6669"))
                    .content(getString(R.string.time_error))
                    .positiveText(getString(R.string.ok))
                    .positiveColor(Color.parseColor("#FF6669"))
                    .onPositive((dialog, which) -> {
                        dialog.dismiss();

                    }).show();
            return false;
        }
        ArrayList<DAO_ExercisePlan> exercisePlan = dbQuery.getAllExercisePlan(etExercisePlanName.getText().toString());
        if (!exercisePlan.isEmpty()) {
            MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext);
            builder
                    .title(getString(R.string.warning))
                    .titleColor(Color.parseColor("#FF6669"))
                    .content(getString(R.string.exercisePlanName_error))
                    .positiveText(getString(R.string.ok))
                    .positiveColor(Color.parseColor("#FF6669"))
                    .onPositive((dialog, which) -> {
                        dialog.dismiss();

                    }).show();
            return false;
        }
        for (int i = 0; i < items.size(); i++) {
            try {
                if (items.get(i).getExercisePlanName().equals(mContext.getString(R.string.exercisePlanType))) {
                    MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext);
                    builder
                            .title(getString(R.string.warning))
                            .titleColor(Color.parseColor("#FF6669"))
                            .content(getString(R.string.exercisePlanType))
                            .positiveText(getString(R.string.ok))
                            .positiveColor(Color.parseColor("#FF6669"))
                            .onPositive((dialog, which) -> {
                                dialog.dismiss();

                            }).show();
                    return false;
                } else if (items.get(i).getExercisePlanNumber() < 0) {
                    MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext);
                    builder
                            .title(getString(R.string.warning))
                            .titleColor(Color.parseColor("#FF6669"))
                            .content(getString(R.string.RepeatCount))
                            .positiveText(getString(R.string.ok))
                            .positiveColor(Color.parseColor("#FF6669"))
                            .onPositive((dialog, which) -> {
                                dialog.dismiss();

                            }).show();
                    return false;
                }
            } catch (Exception e) {
                continue;
            }

        }
        final int allRepeatCount1 = allRepeatCount();
        int repeatCount = 0;
        for (int i = 0; i < items.size(); i++) {
            try {
                repeatCount += items.get(i).getRepeatCount();
            } catch (Exception e) {
                continue;
            }


        }
        final int number = (int) remainingNumber(allRepeatCount1, repeatCount);
        if (number > 0) {

            BudUtil.getInstance().showMaterialDialog(mContext, String.format(mContext.getString(R.string.repetitions_error), Math.abs((float) number)), (dialog, which) -> dialog.show());
            return false;
        } else if (number < 0) {
            BudUtil.getInstance().showMaterialDialog(mContext, String.format(mContext.getString(R.string.repetitions_error2), Math.abs((float) number)), (dialog, which) -> dialog.show());
            return false;
        }


        return true;
    }


    public static void downKeyboard(Context context, EditText editText) {
        InputMethodManager mInputMethodManager = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }
}
