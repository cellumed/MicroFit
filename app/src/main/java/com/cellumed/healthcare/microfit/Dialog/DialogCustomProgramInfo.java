package com.cellumed.healthcare.microfit.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.cellumed.healthcare.microfit.DAO.DAO_Program;
import com.cellumed.healthcare.microfit.Home.Imp_DBOK;
import com.cellumed.healthcare.microfit.R;
import com.cellumed.healthcare.microfit.Util.BudUtil;
import com.cellumed.healthcare.microfit.DataBase.SqlImp;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by ikoob on 2016. 7. 27..
 */
public class DialogCustomProgramInfo extends Dialog implements SqlImp {

    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.tv_state)
    TextView tvState;
    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.tv_StimulusIntensity)
    TextView tvStimulusIntensity;
    @Bind(R.id.tv_PulseOperationTime)
    TextView tvPulseOperationTime;
    @Bind(R.id.tv_PulsePauseTime)
    TextView tvPulsePauseTime;
    @Bind(R.id.tv_Frequency)
    TextView tvFrequency;
    @Bind(R.id.tv_PulseWidth)
    TextView tvPulseWidth;
    @Bind(R.id.tv_PulseRiseTime)
    TextView tvPulseRiseTime;
    @Bind(R.id.close)
    Button close;
    @Bind(R.id.saveDone)
    Button saveDone;

    public DialogCustomProgramInfo(Context mContext, DAO_Program program, Imp_DBOK impDbok) {
        super(mContext);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setTitle(mContext.getString(R.string.Add_exercise_plan));
        setContentView(R.layout.dialog_program_edit);
        ButterKnife.bind(this);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        WindowManager wm = ((WindowManager) mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE));
        lp.width = (int) (wm.getDefaultDisplay().getWidth() * 0.95);
        getWindow().setAttributes(lp);
        tvTitle.setText(String.format(mContext.getString(R.string.DetailsProgram), program.getProgramType()));

        if (program.getProgramType().equals("Basic")) {
            tvState.setText(BudUtil.getInstance().getName(getContext(), program.getProgramState()));
            tvName.setText(BudUtil.getInstance().getName(getContext(), program.getProgramName()));
        } else {
            tvState.setText(program.getProgramState());
            tvName.setText(program.getProgramName());
            tvPulseOperationTime.setText(program.getProgramPulseOperationTime());
        }
        tvStimulusIntensity.setText(program.getProgramStimulusIntensity());

        try {
            tvPulseOperationTime.setText(String.format(mContext.getString(R.string.second), Float.parseFloat(program.getProgramPulseOperationTime())));
        } catch (Exception e) {
            Log.e("getProgramType", program.getProgramType());
            Log.e("OperationTime", program.getProgramPulseOperationTime());
            if (program.getProgramType().equals("Basic")) {
                tvPulseOperationTime.setText(BudUtil.getInstance().getName(getContext(), program.getProgramPulseOperationTime()));
            } else {
                tvPulseOperationTime.setText(program.getProgramPulseOperationTime());
            }
        }
        try {
            tvPulsePauseTime.setText(String.format(mContext.getString(R.string.second), Float.parseFloat(program.getProgramPulsePauseTime())));
        } catch (Exception e) {
            if (program.getProgramType().equals("Basic")) {
                tvPulsePauseTime.setText(BudUtil.getInstance().getName(getContext(), program.getProgramPulsePauseTime()));
            } else {
                tvPulsePauseTime.setText(program.getProgramPulsePauseTime());
            }

        }
        tvFrequency.setText(program.getProgramFrequency().concat("Hz"));
        tvPulseWidth.setText(program.getProgramPulseWidth().concat("㎲"));
        try {
            tvPulseRiseTime.setText(String.format(mContext.getString(R.string.second), Float.parseFloat(program.getProgramPulseRiseTime())));
        } catch (Exception e) {
            if (program.getProgramType().equals("Basic")) {
                tvPulseRiseTime.setText(BudUtil.getInstance().getName(getContext(), program.getProgramPulseRiseTime()));
            } else {
                tvPulseRiseTime.setText(program.getProgramPulseRiseTime());
            }
        }
        tvTime.setText(BudUtil.getInstance().secondToMinute(Integer.parseInt(program.getProgramTime()), mContext.getString(R.string.minute), mContext.getString(R.string.secondText)));
        close.setOnClickListener(v -> dismiss());
        saveDone.setOnClickListener(v -> {
            new DialogProgramEdit(mContext, program, impDbok);
            dismiss();
        });
    }
}
