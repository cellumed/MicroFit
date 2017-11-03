package com.cellumed.healthcare.microfit.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.cellumed.healthcare.microfit.DAO.DAO_DialogNewExercisePaln;
import com.cellumed.healthcare.microfit.DAO.DAO_ExercisePlan;
import com.cellumed.healthcare.microfit.DAO.DAO_Program;
import com.cellumed.healthcare.microfit.DataBase.DBQuery;
import com.cellumed.healthcare.microfit.DataBase.SqlImp;
import com.cellumed.healthcare.microfit.Home.Imp_DBOK;
import com.cellumed.healthcare.microfit.Home.Imp_RepeatCount;

import com.cellumed.healthcare.microfit.R;
import com.cellumed.healthcare.microfit.Setting.Adapter_DialogNewExercisePlan;
import com.cellumed.healthcare.microfit.Util.BudUtil;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by test on 2016-11-19.
 */
public class DialogCustomExercisePlanEdit extends Dialog implements Imp_RepeatCount, SqlImp {

    @Bind(R.id.ll_main)
    LinearLayout llMain;
    @Bind(R.id.tv_title)
    TextView tvTitle;
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
    private ArrayList<DAO_DialogNewExercisePaln> items;
    private ArrayList<DAO_ExercisePlan> exercisePlan;
    private Context mContext;
    private Adapter_DialogNewExercisePlan adapter;
    private ArrayList<String> spNameSave;

    public DialogCustomExercisePlanEdit (Context mContext, ArrayList<DAO_ExercisePlan> exercisePlan, Imp_DBOK mDBok) {
        super(mContext);
        this.mContext = mContext;
        this.exercisePlan = exercisePlan;
        items = new ArrayList<>();
        spNameSave = new ArrayList<>();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setTitle(mContext.getString(R.string.Add_exercise_plan));
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_custom_exercise_plan_edit);
        ButterKnife.bind(this);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        WindowManager wm = ((WindowManager) mContext.getApplicationContext().getSystemService(mContext.getApplicationContext().WINDOW_SERVICE));
        lp.width = (int) (wm.getDefaultDisplay().getWidth() * 0.95);
        lp.height = (int) (wm.getDefaultDisplay().getHeight() * 0.8);
        getWindow().setAttributes(lp);
        Log.d("EPE","height=" + lp.height + "layout h=" + llMain.getHeight());

        tvTitle.setText(mContext.getString(R.string.Modified_exercise_plan));
        tvCnt.setText(String.format(mContext.getString(R.string.repetitions), 0, 0));
        etExercisePlanName.setText(exercisePlan.get(0).getExercisePlanName());
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

        etTime.setText(String.valueOf(secondToMinute(Integer.parseInt(exercisePlan.get(0).getExercisePlanTime()))));
        etTime.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etTime.setImeOptions(EditorInfo.IME_ACTION_DONE);
        etTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                items.clear();
                adapter.notifyDataSetChanged();
                setCount();
            }
        });

        etTime.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    downKeyboard(mContext, etTime);
                    return true;
                }
                return false;
            }
        });

        Boolean isChecked = exercisePlan.get(0).getExercisePlanState().equals("activation");
        cbState.setChecked(isChecked);
        if (cbState.isChecked()) {
            //cbState.setText(mContext.getString(R.string.activation));
        } else {
            //cbState.setText(mContext.getString(R.string.Inactive));
        }





        final String[] exerciseType = mContext.getResources().getStringArray(R.array.ExerciseType);

        for (int i = 0; i < exercisePlan.size(); i++) {

            ArrayList<DAO_Program> allProgram = new DBQuery(mContext).getALLProgram();
            ArrayList<String> name = new ArrayList<>();
            Collections.sort(allProgram, nameComparator);
            name.add(mContext.getString(R.string.program_error));
            spNameSave.add(mContext.getString(R.string.program_error));
            for (DAO_Program dao_program : allProgram) {
                if (dao_program.getProgramType().equals("Basic")) {
                    if (!BudUtil.getInstance().getName(mContext, dao_program.getProgramName()).equals(mContext.getString(R.string.program1)) && !BudUtil.getInstance().getName(mContext, dao_program.getProgramName()).equals(mContext.getString(R.string.program4))) {
                        spNameSave.add(dao_program.getProgramName());
                        name.add(BudUtil.getInstance().getName(mContext, dao_program.getProgramName()));
                    }

                } else {
                    if (!dao_program.getProgramName().equals(mContext.getString(R.string.program1)) && !dao_program.getProgramName().equals(mContext.getString(R.string.program4))) {
                        spNameSave.add(dao_program.getProgramName());
                        name.add(dao_program.getProgramName());
                    }
                }

            }
            spProgram.setAdapter(new ArrayAdapter<>(mContext,
                    android.R.layout.simple_spinner_dropdown_item, name));
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
            String programName ;
            if (exercisePlan.get(0).getExercisePlanType().equals("Basic")) {
                programName = BudUtil.getInstance().getName(mContext,exercisePlan.get(0).getExercisePlanProgram());
            }else{
                if (exercisePlan.get(0).getExercisePlanIsBasic().equals("true")) {
                    programName = BudUtil.getInstance().getName(mContext,exercisePlan.get(0).getExercisePlanProgram());
                }else{
                    programName  = exercisePlan.get(0).getExercisePlanProgram();
                }
            }

            for (int i1 = 0; i1 < spProgram.getAdapter().getCount(); i1++) {
                if (spProgram.getAdapter().getItem(i1).toString().equals(programName)) {
                    spProgram.setSelection(i1);
                    break;

                }
            }

            btAdd.setOnClickListener(v -> {
                if (etTime.getText().length() != 0
                        && !spProgram.getSelectedItem().toString().equals(mContext.getString(R.string.program_error))) {
                    addData();
                } else {
                    if (spProgram.getSelectedItem().toString().equals(mContext.getString(R.string.program_error))) {
                        BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.program_error), (dialog, which) -> this.show());
                    } else if (etTime.getText().length() == 0) {
                        BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.PlzWorkoutTime), (dialog, which) -> this.show());
                    }
                }
            });

            Log.d("CustomEditExercisePlanD", "getExercisePlanRepeatCount()):" + Integer.parseInt(exercisePlan.get(i).getExercisePlanRepeatCount()));

            for (int i1 = 0; i1 < exerciseType.length; i1++) {
                if (exerciseType[i1].equals(exercisePlan.get(i).getExercisePlanPosture())) {
                    DAO_DialogNewExercisePaln add_item=new DAO_DialogNewExercisePaln( Integer.parseInt(exercisePlan.get(i).getExercisePlanRepeatCount()),i1);
                    add_item.setExercisePlanName(exerciseType[i1]);
                    items.add(add_item);
                    break;
                }

            }

        }
        adapter = new Adapter_DialogNewExercisePlan(mContext, items, this);
        lvList.setAdapter(adapter);
        lvList.setDivider(null);
        saveDone.setOnClickListener(v -> {

            adapter.notifyDataSetChanged();

            if (isCheckDialog()) {
                final HashMap<String, String> exercisePlanMap = new HashMap<>();
                final HashMap<String, String> favoritesMap = new HashMap<>();
                exercisePlanMap.put(ExercisePlanName, etExercisePlanName.getText().toString());
                favoritesMap.put(FavoritesName, etExercisePlanName.getText().toString());
                exercisePlanMap.put(ExercisePlanTitle, mContext.getString(R.string.Custom_workout_plans));
                favoritesMap.put(FavoritesTitle, mContext.getString(R.string.Custom_workout_plans));
                if (cbState.isChecked()) {
                    exercisePlanMap.put(ExercisePlanState, "activation");
                    favoritesMap.put(FavoritesState, "activation");
                } else {
                    exercisePlanMap.put(ExercisePlanState, "Inactive");
                    favoritesMap.put(FavoritesState, "Inactive");
                }
                exercisePlanMap.put(ExercisePlanProgram, spNameSave.get(spProgram.getSelectedItemPosition()));
                Log.e("CustomEditExercisePlanD", spNameSave.get(spProgram.getSelectedItemPosition()));
                favoritesMap.put(FavoritesProgram, spNameSave.get(spProgram.getSelectedItemPosition()));
                if (!spNameSave.get(spProgram.getSelectedItemPosition()).equals(spProgram.getSelectedItem().toString())) {
                    exercisePlanMap.put(ExercisePlanProgramIsBasic, "true");
                    exercisePlanMap.put(FavoritesProgramIsBasic, "true");
                } else {
                    exercisePlanMap.put(ExercisePlanProgramIsBasic, "false");
                    exercisePlanMap.put(FavoritesProgramIsBasic, "false");
                }

                exercisePlanMap.put(ExercisePlanType, "Custom");
                exercisePlanMap.put(ExercisePlanTime, String.valueOf(BudUtil.getInstance().calcMinute(Integer.parseInt(etTime.getText().toString()))));
                favoritesMap.put(FavoritesTime, String.valueOf(BudUtil.getInstance().calcMinute(Integer.parseInt(etTime.getText().toString()))));
                exercisePlanMap.put(ExercisePlanExplanation, null);
                favoritesMap.put(FavoritesExplanation, null);
                exercisePlanMap.put(ExercisePlanConstructor, exercisePlan.get(0).getExercisePlanConstructor());
                favoritesMap.put(FavoritesConstructor, exercisePlan.get(0).getExercisePlanConstructor());
                exercisePlanMap.put(ExercisePlanRegDate, exercisePlan.get(0).getExercisePlanRegDate());
                favoritesMap.put(FavoritesRegDate, exercisePlan.get(0).getExercisePlanRegDate());
                for (int i = 0; i < items.size(); i++) {

                    if(items.get(i).getExercisePlanName() == null)
                    {
                        Log.e("modi","i=" + i + " null"+ " obj="+items.get(i).toString());
                    }
                    else Log.e("modi","i="+i+" len=" +items.get(i).getExercisePlanName().length());
                    try {
                        exercisePlanMap.put(ExercisePlanPosture, items.get(i).getExercisePlanName());
                        favoritesMap.put(FavoritesPosture, items.get(i).getExercisePlanName());
                        final String[] exerciseType1 = mContext.getResources().getStringArray(R.array.ExerciseType);
                        for (int i2 = 0; i2 < exerciseType1.length; i2++) {
                            if (exerciseType1[i2].equals(items.get(i).getExercisePlanName())) {
                                exercisePlanMap.put(ExercisePlanImageNumber, String.valueOf(i2));
                                favoritesMap.put(FavoritesImageNumber, String.valueOf(i2));

                                break;

                            }
                        }
                        exercisePlanMap.put(ExercisePlanRepeatCount, String.valueOf(items.get(i).getRepeatCount()));
                        favoritesMap.put(FavoritesRepeatCount, String.valueOf(items.get(i).getRepeatCount()));
                        final DBQuery dbQuery = new DBQuery(mContext);

                        if (dbQuery.newExercisePlanInsert(exercisePlanMap)) {
                            Log.d("DialogNewExercisePlan", "저장성공");
                            mDBok.dbOK();
                            dismiss();
                        }
                    } catch (Exception e) {
                        continue;
                    }
                }

            } else {
                this.show();
            }
        });
        cancel.setOnClickListener(v -> dismiss());

    }

    @Override
    public void RepeatCountOk() {
        setCount();
    }

    @Override
    public void RepeatCountInput() {
        setCount();
    }



    private boolean isCheckDialog() {

        if (etExercisePlanName.getText().length() == 0) {
            BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.exercisePlanName), (dialog, which) -> this.show());
            return false;
        } else if (spProgram.getSelectedItem().toString().equals(mContext.getString(R.string.program_error))) {
            BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.program_error), (dialog, which) -> this.show());
            return false;
        } else if (etTime.getText().length() == 0) {
            BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.time_error), (dialog, which) -> this.show());
            return false;
        }

        ArrayList<DAO_ExercisePlan> allExercisePlan = new DBQuery(mContext).getAllExercisePlan(etExercisePlanName.getText().toString());
        DBQuery dbQuery = new DBQuery(mContext);
        if (allExercisePlan.isEmpty()) {
            dbQuery.ExercisePlanRemove(exercisePlan.get(0).getExercisePlanName());
            if (dbQuery.favoritesRemoveName(exercisePlan.get(0).getExercisePlanName())) {
                Log.d("CustomEditExercisePlanD", "즐겨찾기 삭제");
            }
        } else {
            boolean isIdx = false;
            for (DAO_ExercisePlan dao_exercisePlan : allExercisePlan) {
                if (dao_exercisePlan.getIdx().equals(exercisePlan.get(0).getIdx())) {
                    isIdx = true;
                    break;
                }
            }
            if (!isIdx) {
                BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.exercisePlanName_error), (dialog, which) -> show());
            }else{
                dbQuery.ExercisePlanRemove(exercisePlan.get(0).getExercisePlanName());
                if (dbQuery.favoritesRemoveName(exercisePlan.get(0).getExercisePlanName())) {
                    Log.d("CustomEditExercisePlanD", "즐겨찾기 삭제");
                }
            }
        }

        dbQuery.getDb().close();
        for (int i = 0; i < items.size(); i++) {
            try {
                if (items.get(i).getExercisePlanName().equals(mContext.getString(R.string.exercisePlanType))) {
                    BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.exercisePlanType), (dialog, which) -> this.show());
                    return false;
                } else if (items.get(i).getExercisePlanNumber() < 0) {
                    BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.RepeatCount), (dialog, which) -> this.show());
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
            BudUtil.getInstance().showMaterialDialog(mContext, String.format(mContext.getString(R.string.repetitions_error), Math.abs((float) number)), (dialog, which) -> this.show());
            return false;
        } else if (number < 0) {
            BudUtil.getInstance().showMaterialDialog(mContext, String.format(mContext.getString(R.string.repetitions_error2), Math.abs((float) number)), (dialog, which) -> this.show());
            return false;
        }
        return true;
    }


    private Comparator<DAO_Program> nameComparator = (o1, o2) -> o1.getProgramName().compareTo(o2.getProgramName());

    public int secondToMinute(int num) {
        int minute = num % 3600 / 60;
        if (minute == 0) {
            return num / 60;
        } else {
            return minute;
        }
    }

    @SuppressLint("StringFormatMatches")
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
                BudUtil.getInstance().showMaterialDialog(mContext, String.format(mContext.getString(R.string.repetitions_error2), Math.abs((float) number)), (dialog, which) -> this.show());
                items.get(items.size() - 1).setRepeatCount(0);
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

    private int allRepeatCount() {
        final DBQuery dbQuery = new DBQuery(mContext);
        int allRepeatCount = 0;
        try {
            final String sp_program = spNameSave.get(spProgram.getSelectedItemPosition());
            final int programTime = BudUtil.getInstance().calcMinute(Integer.parseInt((etTime).getText().toString()));
            final float pulseOperationTime = Float.parseFloat(dbQuery.getProgramPulseOperationTime(sp_program));
            final float pulsePauseTime = Float.parseFloat(dbQuery.getProgramPulsePauseTime(sp_program));
            allRepeatCount = (int) allRepeatCount(programTime, (pulseOperationTime + pulsePauseTime));
        } catch (Exception e) {
            e.printStackTrace();
        }
        dbQuery.getDb().close();
        return allRepeatCount;
    }

    private float allRepeatCount(int programTime, float oneRepeatCount) {
        return programTime / oneRepeatCount;

    }

    private float remainingNumber(float allCount, float nowCount) {
        return allCount - nowCount;

    }

    private void addData() {


        if (!items.isEmpty()) {
            if (items.get(items.size() - 1).getRepeatCount() > 0) {
                items.add(new DAO_DialogNewExercisePaln(0, 0));
            } else {
                BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.RepeatCount_error), (dialog, which) -> this.show());
            }

        } else {
            items.add(new DAO_DialogNewExercisePaln(0, 0));
        }
        adapter.notifyDataSetChanged();
    }

    public static void downKeyboard(Context context, EditText editText) {
        InputMethodManager mInputMethodManager = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }
}
