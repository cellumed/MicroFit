package com.cellumed.healthcare.microfit.Setting;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cellumed.healthcare.microfit.DAO.DAO_ExercisePlan;

import com.cellumed.healthcare.microfit.DataBase.DBQuery;
import com.cellumed.healthcare.microfit.Dialog.DialogBasicExercisePlanInfo;
import com.cellumed.healthcare.microfit.Dialog.DialogCustomExercisePlanInfo;
import com.cellumed.healthcare.microfit.Home.Imp_Click;
import com.cellumed.healthcare.microfit.Home.Imp_DBOK;
import com.cellumed.healthcare.microfit.R;
import com.cellumed.healthcare.microfit.Util.BudUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Adapter_ExercisePlanList extends RecyclerView.Adapter<Adapter_ExercisePlanList.ViewHolder> {
    private ArrayList<DAO_ExercisePlan> mDataList;
    private Context mContext;
    private Imp_Click impClick;
    private  Imp_DBOK mDbok;
    private ArrayList<ViewHolder> mList = new ArrayList<>();

    public Adapter_ExercisePlanList(Context context, Imp_Click impClick, Imp_DBOK mDbok, ArrayList<DAO_ExercisePlan> dataList) {
        super();
        this.mContext = context;
        this.mDataList = dataList;
        this.impClick = impClick;
        this.mDbok = mDbok;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.row_setting_exercise_plan_list, viewGroup, false);
        v.setTag(v);
        final ViewHolder viewHolder = new ViewHolder(v);
        mList.add(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (mDataList.get(position).getExercisePlanType().equals("Basic")) {
            holder.cb.setVisibility(View.INVISIBLE);
            holder.tvRegDate.setVisibility(View.INVISIBLE);
            holder.tvName.setText(BudUtil.getInstance().getName(mContext,mDataList.get(position).getExercisePlanName()));
        } else {
            holder.cb.setVisibility(View.VISIBLE);
            holder.tvRegDate.setVisibility(View.VISIBLE);
            holder.tvName.setText(mDataList.get(position).getExercisePlanName());
        }
        holder.tvRegDate.setText(BudUtil.getInstance().getRegDateToString("yy.MM.dd", mDataList.get(position).getExercisePlanRegDate()));
        holder.tvType.setText(String.format(mContext.getString(R.string.ExercisePlan1), mDataList.get(position).getExercisePlanType()));
        holder.cb.setOnCheckedChangeListener((buttonView, isChecked) -> impClick.checked(position));
        //holder.llMain.setOnClickListener(v -> impClick.viewClick(position));
        holder.llMain.setOnClickListener(v -> {
            final ArrayList<DAO_ExercisePlan> exercisePlan = new DBQuery(mContext).getAllExercisePlan(mDataList.get(position).getExercisePlanName());
            showBasicInfo(exercisePlan);
        });
    }

    private void showBasicInfo(ArrayList<DAO_ExercisePlan> exercisePlan) {
        try {
            if (exercisePlan.get(0).getExercisePlanType().equals("Custom")) {
                new DialogCustomExercisePlanInfo(mContext, exercisePlan, mDbok).show();
            }else {
                new DialogBasicExercisePlanInfo(mContext, exercisePlan, mDbok).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {

        try {
            return mDataList.size();
        } catch (Exception e) {
            return 0;
        }

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @Nullable @Bind(R.id.ll_main)
        LinearLayout llMain;
        @Nullable @Bind(R.id.cb)
        CheckBox cb;
        @Nullable @Bind(R.id.tv_name)
        TextView tvName;
        @Nullable @Bind(R.id.tv_regDate)
        TextView tvRegDate;
        @Nullable @Bind(R.id.tv_type)
        TextView tvType;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    public List<ViewHolder> getList() {
        return mList;
    }

}
