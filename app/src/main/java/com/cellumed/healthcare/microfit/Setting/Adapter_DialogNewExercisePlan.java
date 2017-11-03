package com.cellumed.healthcare.microfit.Setting;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cellumed.healthcare.microfit.DAO.DAO_DialogNewExercisePaln;
import com.cellumed.healthcare.microfit.Home.Imp_RepeatCount;
import com.cellumed.healthcare.microfit.R;

import java.util.ArrayList;

/**
 * Created by ikoob on 2016. 7. 19..
 */
public class Adapter_DialogNewExercisePlan extends BaseAdapter {

    private Context mContext;
    private ArrayList<DAO_DialogNewExercisePaln> items;
    private Imp_RepeatCount mCount;

    public Adapter_DialogNewExercisePlan(Context mContext,
                                         ArrayList<DAO_DialogNewExercisePaln> items, Imp_RepeatCount mCount) {
        this.mContext = mContext;
        this.items = items;
        this.mCount = mCount;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater vi = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = vi.inflate(R.layout.row_new_exericse, null);
        final TextView tv_name = (TextView) convertView.findViewById(R.id.tv_name);
        final String[] exerciseType = mContext.getResources().getStringArray(R.array.ExerciseType);
        final Spinner sp_exercisePlan = (Spinner) convertView.findViewById(R.id.sp_exercisePlan);
        final ImageButton ib_remove = (ImageButton) convertView.findViewById(R.id.ib_remove);
        final EditText et_RepeatCount = (EditText) convertView.findViewById(R.id.et_RepeatCount);

        sp_exercisePlan.setAdapter(new ArrayAdapter<>(mContext,
                R.layout.spinner_default_item, exerciseType));
        sp_exercisePlan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                try {
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                    items.get(position).setExercisePlanNumber(pos);
                    items.get(position).setExercisePlanName(parent.getSelectedItem().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        ib_remove.setOnClickListener(v -> {
            items.remove(position);
            notifyDataSetChanged();
            mCount.RepeatCountOk();
        });
        tv_name.setText(String.format(mContext.getString(R.string.posture1), position + 1));


        sp_exercisePlan.setSelection(items.get(position).getExercisePlanNumber());
        if (items.get(position).getRepeatCount() ==0) {
            et_RepeatCount.setText("");
        }else{
            et_RepeatCount.setText(String.valueOf(items.get(position).getRepeatCount()));
        }
        et_RepeatCount.setRawInputType(Configuration.KEYBOARD_12KEY);
        et_RepeatCount.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE || (event!=null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                try {
                    items.get(position).setRepeatCount(Integer.parseInt( v.getText().toString()));
                } catch (NumberFormatException e) {
                    Toast.makeText(mContext, R.string.NumberError, Toast.LENGTH_SHORT).show();
                }
                mCount.RepeatCountOk();
            }
            return true;
        });
        return convertView;
    }
}
