package com.cellumed.healthcare.microfit.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.cellumed.healthcare.microfit.DAO.DAO_ExercisePlan;
import com.cellumed.healthcare.microfit.Home.Adapter_DialogList;
import com.cellumed.healthcare.microfit.Home.Imp_DBOK;
import com.cellumed.healthcare.microfit.R;
import com.cellumed.healthcare.microfit.Util.BudUtil;
import com.cellumed.healthcare.microfit.DataBase.SqlImp;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DialogBasicExercisePlanInfo extends Dialog implements SqlImp {
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.tv_state)
    TextView tvState;
    @Bind(R.id.tv_program)
    TextView tvProgram;
    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.rv_list)
    RecyclerView rvList;
    @Bind(R.id.close)
    Button close;
    @Bind(R.id.saveDone)
    Button saveDone;

    public DialogBasicExercisePlanInfo (Context mContext, ArrayList<DAO_ExercisePlan> exercisePlan, Imp_DBOK mDbok) {
        super(mContext);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setTitle(mContext.getString(R.string.Add_exercise_plan));
        setContentView(R.layout.dialog_exercise_plan_edit);
        ButterKnife.bind(this);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        WindowManager wm = ((WindowManager) mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE));
        lp.width = (int) (wm.getDefaultDisplay().getWidth() * 0.95);
        getWindow().setAttributes(lp);
        tvTitle.setText(String.format(mContext.getString(R.string.DetailsExercisePlan), exercisePlan.get(0).getExercisePlanType()));

        tvName.setText(BudUtil.getInstance().getName(getContext(), exercisePlan.get(0).getExercisePlanName()));
        tvProgram.setText(BudUtil.getInstance().getName(getContext(), exercisePlan.get(0).getExercisePlanProgram()));


        tvState.setText(BudUtil.getInstance().getName(getContext(), exercisePlan.get(0).getExercisePlanState()));
        tvTime.setText(BudUtil.getInstance().secondToMinute(Integer.parseInt(exercisePlan.get(0).getExercisePlanTime()), mContext.getString(R.string.minute), mContext.getString(R.string.secondText)));
        ((Activity) mContext).runOnUiThread(() -> {
            rvList.setHasFixedSize(true);
            rvList.setItemAnimator(new DefaultItemAnimator());
            LinearLayoutManager manager = new LinearLayoutManager(mContext);
            manager.setOrientation(LinearLayoutManager.VERTICAL);
            rvList.setLayoutManager(manager);
            rvList.setAdapter(new Adapter_DialogList(mContext, exercisePlan));
        });
        close.setOnClickListener(v -> dismiss());
        saveDone.setVisibility(View.INVISIBLE);
    }
}
