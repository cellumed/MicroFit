package com.cellumed.healthcare.microfit.Dialog;

import android.content.Context;

import com.cellumed.healthcare.microfit.DAO.DAO_ExercisePlan;
import com.cellumed.healthcare.microfit.Home.Imp_DBOK;

import java.util.ArrayList;

/**
 * Created by test on 2016-11-19.
 */
public class DialogExercisePlanEdit {
    private Context mContext;
    private ArrayList<DAO_ExercisePlan> exercisePlan;
    private Imp_DBOK mDBok;
    public DialogExercisePlanEdit (Context mContext, ArrayList<DAO_ExercisePlan> exercisePlan, Imp_DBOK mDbok) {
        this.mContext = mContext;
        this.exercisePlan = exercisePlan;
        this.mDBok = mDbok;
        showEditUser();
    }

    private void showEditUser() {
        DialogCustomExercisePlanEdit mMaterialDialog = new DialogCustomExercisePlanEdit (mContext, exercisePlan, mDBok);
        mMaterialDialog.show();
        mMaterialDialog.setCanceledOnTouchOutside(false);
    }
}
