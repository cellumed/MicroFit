package com.cellumed.healthcare.microfit.Setting;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.cellumed.healthcare.microfit.DAO.DAO_ExercisePlan;
import com.cellumed.healthcare.microfit.DAO.DAO_Program;
//import com.ikoob.ems_ui.Dialog.DialogNewProgram;
import com.cellumed.healthcare.microfit.DataBase.DBQuery;
import com.cellumed.healthcare.microfit.Home.Imp_Click;
import com.cellumed.healthcare.microfit.Home.Imp_DBOK;
import com.cellumed.healthcare.microfit.R;
import com.cellumed.healthcare.microfit.Util.BudUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class Frag_SettingProgram extends Fragment implements Imp_Click, View.OnClickListener {
    @Bind(R.id.btn_newExercisePlan)
    Button btnNewExercisePlan;
    @Bind(R.id.rl_remove)
    Button rlRemove;
    @Bind(R.id.rv_list)
    RecyclerView rvList;

    private ArrayList<DAO_Program> mProgramList;
    private Adapter_SettingProgramList adapter;
    private ArrayList<Integer> savePos;

    public static final int REQUEST_OK = 1;
    public Frag_SettingProgram() {
        // Required empty public constructor
    }

    public static Frag_SettingProgram newInstance() {

        return new Frag_SettingProgram();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.frag_setting_program, container, false);
        ButterKnife.bind(this, view);
        rvList.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        rvList.setLayoutManager(manager);
        mProgramList = getProgramList();
        programSettingData();
        btnNewExercisePlan.setOnClickListener(this);
        rlRemove.setOnClickListener(v -> showRemoveProgram());

        return view;
    }

    private  void sort( Comparator<DAO_Program> mComparator){
        ArrayList<DAO_Program> BasicList = new ArrayList<>();
        ArrayList<DAO_Program> CustomList = new ArrayList<>();
        ArrayList<DAO_Program> SumList = new ArrayList<>();
        for (int i = 0; i < mProgramList.size(); i++) {
            if (mProgramList.get(i).getProgramType().equals("Basic")) {
                BasicList.add(mProgramList.get(i));
            }else{
                CustomList.add(mProgramList.get(i));
            }
        }
        Collections.sort(CustomList, mComparator);
        for (DAO_Program plan : BasicList) {
            SumList.add(plan);
        }
        for (DAO_Program plan : CustomList) {
            SumList.add(plan);
        }
        mProgramList = SumList;
    }


    private void showRemoveProgram() {
        new MaterialDialog.Builder(getContext())
                .title(getString(R.string.warning))
                .titleColor(Color.parseColor("#2BCC92"))
                .content(R.string.removeProgramBydialog)
                .positiveText(getString(R.string.ok))
                .positiveColor(Color.parseColor("#3BBBCE"))
                .negativeColor(Color.DKGRAY)
                .negativeText(getString(R.string.cancel))
                .onPositive((dialog, which) ->
                {
                    final DBQuery dbQuery = new DBQuery(getContext());
                    boolean isRemove = false;
                    dialog.dismiss();
                    for (Integer savePo : savePos) {
                        if (dbQuery.isFavoritesCheck(mProgramList.get(savePo).getIdx())!=null) {
                            dbQuery.favoritesRemoveAll(mProgramList.get(savePo).getIdx());
                        }
                        final ArrayList<DAO_ExercisePlan> whereProgramNameExercisePlan = dbQuery.getWhereProgramNameExercisePlan(mProgramList.get(savePo).getProgramName());
                        ArrayList<String> savePlanName = new ArrayList<>();
                        if (!whereProgramNameExercisePlan.isEmpty()) {
                            for (DAO_ExercisePlan plan : whereProgramNameExercisePlan) {
                                if (savePlanName.isEmpty()) {
                                    savePlanName.add(plan.getExercisePlanName());
                                }else{
                                    for (String s : savePlanName) {
                                        if (!s.equals(plan.getExercisePlanName())) {
                                            savePlanName.add(plan.getExercisePlanName());
                                        }
                                    }
                                }
                            }
                            for (String s : savePlanName) {
                                dbQuery.ExercisePlanRemove(s);
                            }

                        }
                        isRemove = dbQuery.programRemove(mProgramList.get(savePo).getIdx());
                    }

                    dbQuery.getDb().close();
                    if (isRemove) {
                        BudUtil.getInstance().showMaterialDialog(getContext(), getString(R.string.removeProgram), null);
                         mProgramList = getProgramList();
                        programSettingData();
                        rlRemove.setVisibility(View.VISIBLE);
                    }
                })
                .onNegative((dialog1, which1) -> {
                            dialog1.dismiss();
                            final List<Adapter_SettingProgramList.ViewHolder> list = adapter.getList();
                            for (int i = 0; i < list.size(); i++) {
                                for (Integer savePo : savePos) {
                                    if (i == savePo) {
                                        list.get(i).cb.setChecked(false);
                                        mProgramList.remove(i);
                                    }
                                }
                            }
                        }
                ).show();

    }

    private void programSettingData() {
        adapter = new Adapter_SettingProgramList(getContext(), this, dbok, mProgramList);
        rvList.setAdapter(adapter);
    }

    private ArrayList<DAO_Program> getProgramList() {
        final DBQuery dbQuery = new DBQuery(getContext());
        return dbQuery.getALLProgram();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void checked(int pos) {
        boolean checked = false;
        savePos = new ArrayList<>();
        final List<Adapter_SettingProgramList.ViewHolder> list = adapter.getList();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).cb.isChecked()) {
                checked = true;
                savePos.add(i);
            }
        }
        if (checked) {
            rlRemove.setVisibility(View.VISIBLE);
        } else {
            rlRemove.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void viewClick(int pos) {


    }

    public Imp_DBOK dbok = new Imp_DBOK() {
        @Override
        public void dbOK() {
            mProgramList =  getProgramList();
            programSettingData();
        }
    };

    @Override
    public void onClick(View v) {
        if (v == btnNewExercisePlan) {
            //new DialogNewProgram(getActivity(), this);
            Intent intent = new Intent(getActivity(), Act_AddNewProgram.class);
            startActivityForResult(intent, REQUEST_OK);
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_OK) {
            dbok.dbOK();
        }
    }
}
