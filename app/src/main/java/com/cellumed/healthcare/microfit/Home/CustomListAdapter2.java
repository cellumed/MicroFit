package com.cellumed.healthcare.microfit.Home;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;

import com.cellumed.healthcare.microfit.DAO.DAO_Program;
import com.cellumed.healthcare.microfit.R;
import com.cellumed.healthcare.microfit.Util.BudUtil;

import java.util.ArrayList;

/**
 * Created by test on 2016-10-23.
 */
public class CustomListAdapter2 extends BaseAdapter {
    private static final int[] programImg = {R.drawable.btn_program01, R.drawable.btn_program02, R.drawable.btn_program03, R.drawable.btn_program04, R.drawable.btn_program05};
    private static final int usericon = R.drawable.bt_usericon;
    public ArrayList<DAO_Program> programList = new ArrayList<>();

    public CustomListAdapter2(ArrayList<DAO_Program> list) {
        programList = list;
    }

    @Override
    public int getCount() {
        return programList.size();
    }

    @Override
    public Object getItem(int position) {
        return programList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();
        final ViewGroup par = parent;

        CustomHolder viewHolder;
        Button button = null;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, parent, false);

            viewHolder = new CustomHolder();
            viewHolder.button = (Button) convertView.findViewById(R.id.bt);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (CustomHolder) convertView.getTag();
        }

        button = viewHolder.button;

        if (programList.get(position).getProgramType().equals("Basic")) {
            button.setBackgroundResource(0);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            params.weight = 1.0f;
            params.gravity = Gravity.LEFT;
            button.setLayoutParams(params);
            button.setCompoundDrawablesWithIntrinsicBounds(programImg[position], 0, 0, 0);
            button.setCompoundDrawablePadding(30);
            button.setText(programList.get(position).getProgramName());
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Bundle bundle = new Bundle();
                    bundle.putBoolean("Basic", true);
                    bundle.putString("title", programList.get(position).getProgramName());
                    BudUtil.goActivity(context, Act_TrainingProgram.class, bundle);
                }
            });

            return convertView;

        } else {
            button.setBackgroundResource(0);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.weight = 1.0f;
            params.gravity = Gravity.LEFT;

            button.setGravity(Gravity.LEFT);
            button.setLayoutParams(params);
            button.setCompoundDrawablesWithIntrinsicBounds(usericon, 0, 0, 0);
            button.setCompoundDrawablePadding(30);
            button.setText(programList.get(position).getProgramName());
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Bundle bundle = new Bundle();
                    bundle.putBoolean("Basic", false);

                    bundle.putString("title", programList.get(position).getProgramName());
                    BudUtil.goActivity(context, Act_TrainingProgram.class, bundle);
                }
            });

            return convertView;
        }

    }

    private class CustomHolder {
        Button button = null;
    }

}
