package com.cellumed.healthcare.microfit.Home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cellumed.healthcare.microfit.DAO.DAO_ExercisePlan;
import com.cellumed.healthcare.microfit.R;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;


public class Adapter_DialogList extends RecyclerView.Adapter<Adapter_DialogList.ViewHolder> {


    private ArrayList<DAO_ExercisePlan> mDataList;
    private Context mContext;

    public Adapter_DialogList(Context context, ArrayList<DAO_ExercisePlan> dataList) {
        super();
        this.mContext = context;
        this.mDataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.row_dialog_exercise_plan, viewGroup, false);
        return new ViewHolder(v);
    }


    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
/*
        final Locale locale = mContext.getResources().getConfiguration().locale;
        if (locale.getLanguage().equals("ko")) {
            holder.tvName.setText(mDataList.get(position).getExercisePlanPostureKO());
        }else{
            holder.tvName.setText(mDataList.get(position).getExercisePlanPostureUS());
        }
*/
        holder.tvName.setText(mDataList.get(position).getExercisePlanPosture());
        holder.tvRepeatCount.setText(mDataList.get(position).getExercisePlanRepeatCount());
        holder.tvTitle.setText(String.format(mContext.getString(R.string.posture1), position + 1));
    }

    @Override
    public int getItemCount() {

        try {
            return mDataList.size();
        } catch (NullPointerException e) {
            return 0;
        }

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_title)
        TextView tvTitle;
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.tv_repeatCountText)
        TextView tvRepeatCountText;
        @Bind(R.id.tv_repeatCount)
        TextView tvRepeatCount;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);

        }

    }

}
