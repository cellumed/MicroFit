package com.cellumed.healthcare.microfit.Setting;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cellumed.healthcare.microfit.DAO.DAO_ExercisePlan;
import com.cellumed.healthcare.microfit.DAO.DAO_Favorites;
import com.cellumed.healthcare.microfit.DAO.DAO_Program;
import com.cellumed.healthcare.microfit.DataBase.DBQuery;
import com.cellumed.healthcare.microfit.Dialog.DialogBasicExercisePlanInfo;
import com.cellumed.healthcare.microfit.Dialog.DialogBasicProgramInfo;
import com.cellumed.healthcare.microfit.Home.Imp_Click;
import com.cellumed.healthcare.microfit.R;
import com.cellumed.healthcare.microfit.Util.BudUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Adapter_FavoritesList extends RecyclerView.Adapter<Adapter_FavoritesList.ViewHolder> {

    private ArrayList<DAO_Favorites> mDataList;
    private Context mContext;
    private Imp_Click impClick;

    private ArrayList<ViewHolder> mList = new ArrayList<>();
    private ArrayList<DAO_Favorites> favorites;

    public Adapter_FavoritesList(Context context, Imp_Click impClick, ArrayList<DAO_Favorites> dataList) {
        super();
        this.mContext = context;
        this.mDataList = dataList;  // 전체 list
        this.impClick = impClick;

        //favorites = new DBQuery(mContext).getFavorites();
        favorites = new DBQuery(mContext).getFavoritesWhereUserId(BudUtil.getShareValue(mContext, BudUtil.USER_ID));    // 해당 사용자 list

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.row_favorites_list, viewGroup, false);
        v.setTag(v);
        final ViewHolder viewHolder = new ViewHolder(v);
        mList.add(viewHolder);
        Log.d("VH", "onCreateViewHolder vh=" +viewHolder.toString());

        return viewHolder;
    }

    private void showBasicInfo1(ArrayList<DAO_ExercisePlan> exercisePlan) {
        try {
            if (exercisePlan.get(0).getExercisePlanType().equals("Custom")) {
             //   new DialogCustomExercisePlanInfo(mContext, exercisePlan, null).show();
                new DialogBasicExercisePlanInfo(mContext, exercisePlan, null).show();
            }else {
                new DialogBasicExercisePlanInfo(mContext, exercisePlan, null).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showBasicInfo2 (ArrayList<DAO_Program> programPlan) {
        try {
            if (programPlan.get(0).getProgramType().equals("Custom")) {
              //  new DialogCustomProgramInfo(mContext, programPlan.get(0), null).show();
                new DialogBasicProgramInfo(mContext, programPlan.get(0), null).show();
            }else {
                new DialogBasicProgramInfo(mContext, programPlan.get(0), null).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d("VH", "onbindViewHolder vh=" + holder.toString() + ", pos=" + position);


        if (mDataList.get(position).getFavoritesType().equals("1")) {
            holder.tvType.setText(String.format(mContext.getString(R.string.ExercisePlan1),mDataList.get(position).getFavoritesType2()));

            holder.llMain.setOnClickListener(v -> {
                final ArrayList<DAO_ExercisePlan> exercisePlan = new DBQuery(mContext).getAllExercisePlan(mDataList.get(position).getFavoritesName());
                showBasicInfo1(exercisePlan);
            });

        }else{
            holder.tvType.setText(String.format(mContext.getString(R.string.Program1),mDataList.get(position).getFavoritesType2()));

            holder.llMain.setOnClickListener(v -> {
                final ArrayList<DAO_Program> programPlan = new DBQuery(mContext).getALLProgramByName(mDataList.get(position).getFavoritesName());
                showBasicInfo2(programPlan);
            });
        }

        holder.cb.setEnabled(false);

        holder.cb.setChecked(false);
        for (DAO_Favorites favorite : favorites) {
            if (mDataList.get(position).getIdx().equals(favorite.getLinkIdx())) {
                holder.cb.setChecked(true);
                break;
            }
        }

        if (mDataList.get(position).getFavoritesType2().equals("Basic")) {
            holder.tvRegDate.setVisibility(View.INVISIBLE);
            holder.tvName.setText(BudUtil.getInstance().getName(mContext,mDataList.get(position).getFavoritesName()));
        } else {
            holder.tvRegDate.setVisibility(View.VISIBLE);
            holder.tvName.setText(mDataList.get(position).getFavoritesName());
        }
        holder.tvRegDate.setText(BudUtil.getInstance().getRegDateToString("yy.MM.dd",mDataList.get(position).getFavoritesRegDate()));
        holder.cb.setOnCheckedChangeListener((buttonView, isChecked) ->
            { if(holder.cb.isEnabled())
              {
                impClick.checked(position);
                favorites = new DBQuery(mContext).getFavoritesWhereUserId(BudUtil.getShareValue(mContext, BudUtil.USER_ID));

                  // uncheck if check is canceled by limit
                  holder.cb.setEnabled(false);
                  holder.cb.setChecked(false);
                  for (DAO_Favorites favorite : favorites) {
                      if (mDataList.get(position).getIdx().equals(favorite.getLinkIdx())) {
                          holder.cb.setChecked(true);
                      }
                  }
                  holder.cb.setEnabled(true);

              }
            }
        );
        holder.cb.setEnabled(true);


    }

    @Override
    public int getItemCount() {
        try {
            return mDataList.size();
        } catch (NullPointerException e) {
            return 0;
        }
    }


    public ArrayList<DAO_Favorites> getmDataList() {
        return mDataList;
    }
    public ArrayList<DAO_Favorites> getmFavList() {
        return favorites;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.cb)
        CheckBox cb;
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.tv_type)
        TextView tvType;
        @Bind(R.id.tv_regDate)
        TextView tvRegDate;
        @Bind(R.id.ll_main)
        LinearLayout llMain;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

    }

    public List<ViewHolder> getList() {
        return mList;
    }

}
