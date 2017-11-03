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
//import com.ikoob.ems_ui.Dialog.DialogNewExercisePlan_New;
import com.cellumed.healthcare.microfit.DataBase.DBQuery;
import com.cellumed.healthcare.microfit.Home.Imp_Click;
import com.cellumed.healthcare.microfit.Home.Imp_DBOK;
import com.cellumed.healthcare.microfit.R;
import com.cellumed.healthcare.microfit.Util.BudUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class Frag_SettingExercisePlan extends Fragment implements Imp_Click, View.OnClickListener {

    @Bind(R.id.btn_newExercisePlan)
    Button btnNewExercisePlan;
    @Bind(R.id.rl_remove)
    Button rlRemove;
    @Bind(R.id.rv_list)
    RecyclerView rvList;

    private ArrayList<DAO_ExercisePlan> exercisePlanList;
    private Adapter_ExercisePlanList adapter;
    private ArrayList<Integer> savePos;
    public static final int REQUEST_OK = 1;
    public Frag_SettingExercisePlan() {
        // Required empty public constructor
    }

    public static Frag_SettingExercisePlan newInstance() {
        return new Frag_SettingExercisePlan();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_setting_exerise_plan, container, false);
        ButterKnife.bind(this, view);
        rvList.setHasFixedSize(true);
        rvList.setItemAnimator(new DefaultItemAnimator());

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rvList.setLayoutManager(manager);
        exercisePlanList = getExercisePlanList();
        exerciserPlanSettingData();
        btnNewExercisePlan.setOnClickListener(this);
        rlRemove.setOnClickListener(v -> showRemoveExercisePlan());
        return view;
    }


    private void showRemoveExercisePlan() {
        new MaterialDialog.Builder(getContext())
                .title(getString(R.string.warning))
                .titleColor(Color.parseColor("#000000"))
                .content(R.string.removeExercisePlanByDialog)
                .positiveText(getString(R.string.ok))
                .positiveColor(Color.parseColor("#3BBBCE"))
                .negativeColor(Color.DKGRAY)
                .negativeText(getString(R.string.cancel))
                .onPositive((dialog, which) ->
                {
                    boolean isRemove = false;
                    dialog.dismiss();
                    final DBQuery dbQuery = new DBQuery(getContext());
                    for (Integer savePo : savePos) {
                        if (dbQuery.isFavoritesCheck(exercisePlanList.get(savePo).getIdx())!=null) {
                            dbQuery.favoritesRemoveAll(exercisePlanList.get(savePo).getIdx());
                        }

                        isRemove = dbQuery.ExercisePlanRemove(exercisePlanList.get(savePo).getExercisePlanName());
                    }
                    dbQuery.getDb().close();
                    if (isRemove) {
                        BudUtil.getInstance().showMaterialDialog(getContext(), getString(R.string.removeExercisePlan), null);
                        exercisePlanList = getExercisePlanList();
                        exerciserPlanSettingData();
                        rlRemove.setVisibility(View.VISIBLE);
                    }
                })
                .onNegative((dialog1, which1) -> {
                            dialog1.dismiss();

                        }
                ).show();

    }

    private void exerciserPlanSettingData() {
        adapter = new Adapter_ExercisePlanList(getContext(), this, dbok, exercisePlanList);
        rvList.setAdapter(adapter);
    }

    private ArrayList<DAO_ExercisePlan> getExercisePlanList() {
        final DBQuery dbQuery = new DBQuery(getContext());
        return dbQuery.getSettingExercisePlan();
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
        final List<Adapter_ExercisePlanList.ViewHolder> list = adapter.getList();
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
    public void onResume() {
        super.onResume();
        checked(0);
    }

    @Override
    public void viewClick(int pos) {
        //DAO_ExercisePlan clicked = exercisePlanList.get(pos);
        //showBasicInfo(clicked);
    }

    public Imp_DBOK dbok = new Imp_DBOK() {
        @Override
        public void dbOK() {
            exercisePlanList = getExercisePlanList();
            exerciserPlanSettingData();
        }
    };

    @Override
    public void onClick(View v) {
        if (v == btnNewExercisePlan) {
            Intent intent = new Intent(getActivity(), Act_AddNewExercisePlan.class);
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
