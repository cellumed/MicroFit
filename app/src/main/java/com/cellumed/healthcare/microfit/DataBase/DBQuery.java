package com.cellumed.healthcare.microfit.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.cellumed.healthcare.microfit.DAO.DAO_Favorites;
import com.cellumed.healthcare.microfit.DAO.DAO_Program;
import com.cellumed.healthcare.microfit.DAO.DAO_ExercisePlan;
import com.cellumed.healthcare.microfit.DAO.DAO_Training;
import com.cellumed.healthcare.microfit.Util.BudUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;


public class DBQuery implements SqlImp {

    private DBOpenHelper db;

    public DBOpenHelper getDb() {
        return db;
    }


    public DBQuery(Context mContext) {
        db = new DBOpenHelper(mContext);
    }

    private boolean queryExecute(Boolean mQuery) {
        final Boolean query = mQuery;
        db.close();
        return query;

    }

    public String isFavoritesCheck(String idx){
        final String where = FavoritesLinkIdx
                + " = '"
                + idx
                + "'";
        return db.getField(FavoritesTable, Idx, where, null, null);
    }

    public boolean isFavorites(String idx, String userId) {
        final String where = String.format(FavoritesLinkIdx + " = '%s' AND " + FavoritesUserId + " = '%s'", idx, userId);
        String field = db.getField(FavoritesTable, Idx, where, null, null);
        return field != null;
    }

    public int getFavoritesCount(String userId) {
        final String where = FavoritesUserId
                + " = '"
                + userId
                + "'";
        Cursor mCursor = db.getField(FavoritesTable, ALL_FIELD, where, null, null, null);
        final int count = mCursor.getCount();
        mCursor.close();
        return count;
    }

    public ArrayList<DAO_Favorites> getFavoritesWhereUserId(String userId) {
        final String where = FavoritesUserId
                + " = '"
                + userId
                + "' AND "
                + FavoritesState
                + " = '"
                + "activation"
                + "'";
        final Cursor mCursor = db.getField(FavoritesTable, ALL_FIELD, where, null, null, null);
        ArrayList<DAO_Favorites> favorites = new ArrayList<>();
        while (mCursor.moveToNext()) {
            try {
                DAO_Favorites mFavorites = new DAO_Favorites();
                mFavorites.setIdx(mCursor.getString(mCursor
                        .getColumnIndex(Idx)));
                mFavorites.setFavoritesUserId(mCursor.getString(mCursor
                        .getColumnIndex(FavoritesUserId)));
                mFavorites.setLinkIdx(mCursor.getString(mCursor
                        .getColumnIndex(FavoritesLinkIdx)));
                mFavorites.setFavoritesStimulusIntensity(mCursor.getString(mCursor
                        .getColumnIndex(FavoritesStimulusIntensity)));
                mFavorites.setFavoritesPulseOperationTime(mCursor.getString(mCursor
                        .getColumnIndex(FavoritesPulseOperationTime)));
                mFavorites.setFavoritesPulsePauseTime(mCursor.getString(mCursor
                        .getColumnIndex(FavoritesPulsePauseTime)));
                mFavorites.setFavoritesFrequency(mCursor.getString(mCursor
                        .getColumnIndex(FavoritesFrequency)));
                mFavorites.setFavoritesPulseWidth(mCursor.getString(mCursor
                        .getColumnIndex(FavoritesPulseWidth)));
                mFavorites.setFavoritesPulseRiseTime(mCursor.getString(mCursor
                        .getColumnIndex(FavoritesPulseRiseTime)));
                mFavorites.setFavoritesExplanation(mCursor.getString(mCursor
                        .getColumnIndex(FavoritesExplanation)));
                mFavorites.setFavoritesName(mCursor.getString(mCursor
                        .getColumnIndex(FavoritesName)));
                mFavorites.setFavoritesPosture(mCursor.getString(mCursor
                        .getColumnIndex(FavoritesPosture)));
                mFavorites.setFavoritesPostureImageNumber(mCursor.getString(mCursor
                        .getColumnIndex(FavoritesImageNumber)));
                mFavorites.setFavoritesProgram(mCursor.getString(mCursor
                        .getColumnIndex(FavoritesProgram)));
                mFavorites.setFavoritesRepeatCount(mCursor.getString(mCursor
                        .getColumnIndex(FavoritesRepeatCount)));
                mFavorites.setFavoritesState(mCursor.getString(mCursor
                        .getColumnIndex(FavoritesState)));
                mFavorites.setFavoritesTime(mCursor.getString(mCursor
                        .getColumnIndex(FavoritesTime)));
                mFavorites.setFavoritesTitle(mCursor.getString(mCursor
                        .getColumnIndex(FavoritesTitle)));
                mFavorites.setFavoritesType2(mCursor.getString(mCursor
                        .getColumnIndex(FavoritesType2)));
                mFavorites.setFavoritesRegDate(mCursor.getString(mCursor
                        .getColumnIndex(FavoritesRegDate)));
                mFavorites.setFavoritesConstructor(mCursor.getString(mCursor
                        .getColumnIndex(FavoritesConstructor)));
                mFavorites.setFavoritesType(mCursor.getString(mCursor
                        .getColumnIndex(FavoritesType)));
                favorites.add(mFavorites);
            } catch (SQLiteException | IllegalStateException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        mCursor.close();
        db.close();
        return favorites;

    }

    public int getExercisePlanCountUsingProg(String prog) {
        final String where = ExercisePlanProgram
                + " = '"
                + prog
                + "'";
        Cursor mCursor = db.getField(ExercisePlanTable, ALL_FIELD, where, null, null, null);
        final int count = mCursor.getCount();
        mCursor.close();
        return count;
    }


    public ArrayList<DAO_Training> getTrainingData(Context mContext ,String name ,boolean isBasic) {
        String where = String.format(ExercisePlanName + " = '%s'", name);
        Cursor mCursor = db.getField(ExercisePlanTable, ALL_FIELD, where, null, Idx, null);
        ArrayList<DAO_Training> mTrainingList = new ArrayList<>();
        while (mCursor.moveToNext()) {
            try {
                DAO_Training mTraining = new DAO_Training();
                mTraining.setExerciseIdx(mCursor.getString(mCursor
                        .getColumnIndex(Idx)));
                mTraining.setExercisePlanExplanation(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanExplanation)));
                mTraining.setExercisePlanName(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanName)));
                mTraining.setExercisePlanPosture(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanPosture)));
                mTraining.setExercisePlanPostureImageNumber(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanImageNumber)));
            /*    if (isBasic) {
                    mTraining.setExercisePlanProgram(BudUtil.getInstance().getName(mContext,mCursor.getString(mCursor
                            .getColumnIndex(ExercisePlanProgram))));
                    mTraining.setExercisePlanTitle(BudUtil.getInstance().getName(mContext,mCursor.getString(mCursor
                            .getColumnIndex(ExercisePlanTitle))));
                }else{*/
                    mTraining.setExercisePlanProgram(mCursor.getString(mCursor
                            .getColumnIndex(ExercisePlanProgram)));
                    mTraining.setExercisePlanTitle(mCursor.getString(mCursor
                            .getColumnIndex(ExercisePlanTitle)));
//                }

                mTraining.setExercisePlanRepeatCount(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanRepeatCount)));
                mTraining.setExercisePlanState(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanState)));
                mTraining.setExercisePlanTime(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanTime)));

                mTraining.setExercisePlanType(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanType)));
                mTraining.setExercisePlanRegDate(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanRegDate)));
                mTraining.setExercisePlanConstructor(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanConstructor)));
                mTrainingList.add(mTraining);
            } catch (SQLiteException | IllegalStateException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        mCursor.close();
            where = String.format(ProgramName + " = '%s'", mTrainingList.get(0).getExercisePlanProgram());
        mCursor = db.getField(ProgramTable, ALL_FIELD, where, Idx, "1");
        while (mCursor.moveToNext()) {
            try {
                mTrainingList.get(0).setProgramIdx(mCursor.getString(mCursor
                        .getColumnIndex(Idx)));
                mTrainingList.get(0).setProgramName(mCursor.getString(mCursor
                        .getColumnIndex(ProgramName)));
                if (isBasic) {
                    mTrainingList.get(0).setProgramState(BudUtil.getInstance().getName(mContext,mCursor.getString(mCursor
                            .getColumnIndex(ProgramState))));
                    mTrainingList.get(0).setProgramTitle(BudUtil.getInstance().getName(mContext,mCursor.getString(mCursor
                            .getColumnIndex(ProgramTitle))));
                }else{
                    mTrainingList.get(0).setProgramState(mCursor.getString(mCursor
                            .getColumnIndex(ProgramState)));
                    mTrainingList.get(0).setProgramTitle(mCursor.getString(mCursor
                            .getColumnIndex(ProgramTitle)));
                }

                mTrainingList.get(0).setProgramTime(mCursor.getString(mCursor
                        .getColumnIndex(ProgramTime)));
                mTrainingList.get(0).setProgramStimulusIntensity(mCursor.getString(mCursor
                        .getColumnIndex(ProgramStimulusIntensity)));
                mTrainingList.get(0).setProgramPulseOperationTime(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseOperationTime)));
                mTrainingList.get(0).setProgramPulsePauseTime(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulsePauseTime)));
                mTrainingList.get(0).setProgramFrequency(mCursor.getString(mCursor
                        .getColumnIndex(ProgramFrequency)));
                mTrainingList.get(0).setProgramPulseWidth(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseWidth)));
                mTrainingList.get(0).setProgramPulseRiseTime(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseRiseTime)));
                mTrainingList.get(0).setProgramExplanation(mCursor.getString(mCursor
                        .getColumnIndex(ProgramExplanation)));

                mTrainingList.get(0).setProgramType(mCursor.getString(mCursor
                        .getColumnIndex(ProgramType)));
                mTrainingList.get(0).setProgramConstructor(mCursor.getString(mCursor
                        .getColumnIndex(ProgramConstructor)));
                mTrainingList.get(0).setProgramRegDate(mCursor.getString(mCursor
                        .getColumnIndex(ProgramRegDate)));

            } catch (SQLiteException | IllegalStateException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        mCursor.close();
        db.close();
        return mTrainingList;
    }

    public DAO_Training getProgramTrainingData(String name) {
        String where = String.format(ProgramName + " = '%s'", name);
        Cursor mCursor = db.getField(ProgramTable, ALL_FIELD, where, Idx, "1");

        DAO_Training mTraining  = new DAO_Training();
        while (mCursor.moveToNext()) {
            try {
                mTraining.setProgramIdx(mCursor.getString(mCursor
                        .getColumnIndex(Idx)));
                mTraining.setProgramName(mCursor.getString(mCursor
                        .getColumnIndex(ProgramName)));

                mTraining.setProgramState(mCursor.getString(mCursor
                        .getColumnIndex(ProgramState)));
                mTraining.setProgramTime(mCursor.getString(mCursor
                        .getColumnIndex(ProgramTime)));
                mTraining.setProgramStimulusIntensity(mCursor.getString(mCursor
                        .getColumnIndex(ProgramStimulusIntensity)));
                mTraining.setProgramPulseOperationTime(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseOperationTime)));
                mTraining.setProgramPulsePauseTime(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulsePauseTime)));
                mTraining.setProgramFrequency(mCursor.getString(mCursor
                        .getColumnIndex(ProgramFrequency)));
                mTraining.setProgramPulseWidth(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseWidth)));
                mTraining.setProgramPulseRiseTime(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseRiseTime)));
                mTraining.setProgramExplanation(mCursor.getString(mCursor
                        .getColumnIndex(ProgramExplanation)));
                mTraining.setProgramTitle(mCursor.getString(mCursor
                        .getColumnIndex(ProgramTitle)));
                mTraining.setProgramType(mCursor.getString(mCursor
                        .getColumnIndex(ProgramType)));
                mTraining.setProgramConstructor(mCursor.getString(mCursor
                        .getColumnIndex(ProgramConstructor)));
                mTraining.setProgramRegDate(mCursor.getString(mCursor
                        .getColumnIndex(ProgramRegDate)));

            } catch (SQLiteException | IllegalStateException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        mCursor.close();
        db.close();
        return mTraining;
    }

    public ArrayList<DAO_Favorites> getFavorites() {
         String where = ExercisePlanState
                + " = '"
                + "activation"
                + "' ";
        Cursor mCursor = db.getField(ExercisePlanTable, ALL_FIELD, where, ExercisePlanName, Idx, null);
        ArrayList<DAO_Favorites> favorites = new ArrayList<>();
        while (mCursor.moveToNext()) {
            try {
                DAO_Favorites mFavorites = new DAO_Favorites();
                mFavorites.setIdx(mCursor.getString(mCursor
                        .getColumnIndex(Idx)));
                mFavorites.setFavoritesExplanation(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanExplanation)));
                mFavorites.setFavoritesName(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanName)));
                mFavorites.setFavoritesPosture(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanPosture)));
                mFavorites.setFavoritesPostureImageNumber(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanImageNumber)));
                mFavorites.setFavoritesProgram(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanProgram)));
                mFavorites.setFavoritesRepeatCount(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanRepeatCount)));
                mFavorites.setFavoritesState(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanState)));
                mFavorites.setFavoritesTime(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanTime)));
                mFavorites.setFavoritesTitle(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanTitle)));
                mFavorites.setFavoritesType2(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanType)));
                mFavorites.setFavoritesRegDate(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanRegDate)));
                mFavorites.setFavoritesConstructor(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanConstructor)));
                mFavorites.setFavoritesType("1");
                favorites.add(mFavorites);
            } catch (SQLiteException | IllegalStateException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        mCursor.close();


         where = ProgramState
                + " = '"
                + "activation"
                + "' ";


        mCursor = db.getField(ProgramTable, ALL_FIELD, where, ProgramName, Idx, null);
        while (mCursor.moveToNext()) {
            try {
                DAO_Favorites mFavorites = new DAO_Favorites();
                mFavorites.setIdx(mCursor.getString(mCursor
                        .getColumnIndex(Idx)));
                mFavorites.setFavoritesName(mCursor.getString(mCursor
                        .getColumnIndex(ProgramName)));
                mFavorites.setFavoritesState(mCursor.getString(mCursor
                        .getColumnIndex(ProgramState)));
                mFavorites.setFavoritesTime(mCursor.getString(mCursor
                        .getColumnIndex(ProgramTime)));
                mFavorites.setFavoritesStimulusIntensity(mCursor.getString(mCursor
                        .getColumnIndex(ProgramStimulusIntensity)));
                mFavorites.setFavoritesPulseOperationTime(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseOperationTime)));
                mFavorites.setFavoritesPulsePauseTime(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulsePauseTime)));
                mFavorites.setFavoritesFrequency(mCursor.getString(mCursor
                        .getColumnIndex(ProgramFrequency)));
                mFavorites.setFavoritesPulseWidth(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseWidth)));
                mFavorites.setFavoritesPulseRiseTime(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseRiseTime)));
                mFavorites.setFavoritesExplanation(mCursor.getString(mCursor
                        .getColumnIndex(ProgramExplanation)));
                mFavorites.setFavoritesTitle(mCursor.getString(mCursor
                        .getColumnIndex(ProgramTitle)));
                mFavorites.setFavoritesType2(mCursor.getString(mCursor
                        .getColumnIndex(ProgramType)));
                mFavorites.setFavoritesRegDate(mCursor.getString(mCursor
                        .getColumnIndex(ProgramRegDate)));
                mFavorites.setFavoritesConstructor(mCursor.getString(mCursor
                        .getColumnIndex(ProgramConstructor)));
                mFavorites.setFavoritesType("2");
                favorites.add(mFavorites);
            } catch (SQLiteException | IllegalStateException | NullPointerException e) {
                e.printStackTrace();
            }
            try {
                Collections.sort(favorites, RegDateComparator);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mCursor.close();
        db.close();
        return favorites;
    }

    private Comparator<DAO_Favorites> RegDateComparator = (o1, o2) -> o1.getFavoritesRegDate().compareTo(o2.getFavoritesRegDate());

    
    public boolean newWorkoutData(HashMap<String, String> workOut){
        Log.e("DBQuery", "newWorkoutData");
        ContentValues mValues = new ContentValues();
        final Set<String> key = workOut.keySet();
        for (String keyName : key) {
            String valueName = workOut.get(keyName);
            mValues.put(keyName, valueName);
        }
        final boolean b = db.setRecords(WorkoutDataTable, mValues);
        db.close();
        return b;
    }

    public boolean editWorkoutData(String startTime, int time){
        final String where =
                WorkoutDataStartDate
                + " = '"
                + startTime
                + "'";
        ContentValues mValues = new ContentValues();
        mValues.put(WorkoutDataEndDate,BudUtil.getInstance().getToday("yyyy.MM.dd HH.mm.ss"));
		DecimalFormat formatter = new DecimalFormat("00");
        String min = formatter.format(time / 60);
        String sec = formatter.format(time % 60);
        mValues.put(WorkoutDataTime, String.format("%s:%s", min, sec));
        final boolean b = db.setField(WorkoutDataTable, mValues,where);
        db.close();
        return b;
    }

    public boolean newExercisePlanInsert(HashMap<String, String> userInfo) {
        ContentValues mValues = new ContentValues();
        final Set<String> key = userInfo.keySet();
        for (String keyName : key) {
            String valueName = userInfo.get(keyName);
            mValues.put(keyName, valueName);

        }
        final boolean b = db.setRecords(ExercisePlanTable, mValues);
        db.close();
        return b;
    }

    public boolean setExercisePlanUpdate(HashMap<String, String> userInfo,String idx) {
        ContentValues mValues = new ContentValues();
        final Set<String> key = userInfo.keySet();
        for (String keyName : key) {
            String valueName = userInfo.get(keyName);
            mValues.put(keyName, valueName);

        }
        Log.d("setExercisePlanUpdate", idx);
        final String where = Idx
                + " = '"
                + idx
                + "'";
        final boolean b = db.setField(ExercisePlanTable, mValues,where);
        db.close();
        return b;
    }

    public boolean newProgramInsert(HashMap<String, String> programInfo) {
        ContentValues mValues = new ContentValues();
        final Set<String> key = programInfo.keySet();
        for (String keyName : key) {
            String valueName = programInfo.get(keyName);
            mValues.put(keyName, valueName);

        }
        final boolean b = db.setRecords(ProgramTable, mValues);
        db.close();
        return b;
    }

    public boolean setProgramUpdate(HashMap<String, String> programInfo,String idx) {
        ContentValues mValues = new ContentValues();
        final Set<String> key = programInfo.keySet();
        for (String keyName : key) {
            String valueName = programInfo.get(keyName);
            mValues.put(keyName, valueName);

        }
        final String where = Idx
                + " = '"
                + idx
                + "'";
        final boolean b = db.setField(ProgramTable, mValues,where);
        db.close();
        return b;
    }
    public boolean setProgramUpdateAndFavorites(HashMap<String, String> programInfo,HashMap<String, String> favoritesInfo,String idx) {
        ContentValues mValues = new ContentValues();
         Set<String> key = programInfo.keySet();
        for (String keyName : key) {
            String valueName = programInfo.get(keyName);
            mValues.put(keyName, valueName);

        }
         String where = Idx
                + " = '"
                + idx
                + "'";
         boolean b = db.setField(ProgramTable, mValues,where);
        if (b) {
            mValues = new ContentValues();
             key = favoritesInfo.keySet();
            for (String keyName : key) {
                String valueName = favoritesInfo.get(keyName);
                mValues.put(keyName, valueName);

            }
            where = FavoritesLinkIdx
                    + " = '"
                    + idx
                    + "'";
            b = db.setField(FavoritesTable, mValues,where);
        }

        db.close();
        return b;
    }
    public boolean setExerciseUpdateAndFavorites(HashMap<String, String> programInfo,HashMap<String, String> favoritesInfo,String name,String idx) {
        ContentValues mValues = new ContentValues();
        Set<String> key = programInfo.keySet();
        for (String keyName : key) {
            String valueName = programInfo.get(keyName);
            mValues.put(keyName, valueName);

        }
        String where = Idx
                + " = '"
                + name
                + "'";
        boolean b = db.setField(ExercisePlanTable, mValues,where);
        if (b) {
            mValues = new ContentValues();
            key = favoritesInfo.keySet();
            for (String keyName : key) {
                String valueName = favoritesInfo.get(keyName);
                mValues.put(keyName, valueName);

            }
            where = FavoritesLinkIdx
                    + " = '"
                    + idx
                    + "'";
            b = db.setField(FavoritesTable, mValues,where);
        }

        db.close();
        return b;
    }
    public boolean newUserInsert(HashMap<String, String> userInfo) {
        ContentValues mValues = new ContentValues();
        final Set<String> key = userInfo.keySet();
        for (String keyName : key) {
            String valueName = userInfo.get(keyName);
            mValues.put(keyName, valueName);

        }
        final boolean b = db.setRecords(UserTable, mValues);
        db.close();
        return b;
    }

    public boolean editUser(HashMap<String, String> userInfo, String idx) {
        final String where = Idx
                + " = '"
                + idx
                + "'";
        ContentValues mValues = new ContentValues();
        final Set<String> key = userInfo.keySet();
        for (String keyName : key) {
            String valueName = userInfo.get(keyName);
            mValues.put(keyName, valueName);

        }
        final boolean b = db.setField(UserTable, mValues, where);
        db.close();
        return b;
    }

    public boolean newFavoritesInsert(HashMap<String, String> favoritesInfo) {
        ContentValues mValues = new ContentValues();
        final Set<String> key = favoritesInfo.keySet();
        for (String keyName : key) {
            String valueName = favoritesInfo.get(keyName);
            mValues.put(keyName, valueName);

        }
        return db.setRecords(FavoritesTable, mValues);
    }

    public ArrayList<ArrayList<DAO_ExercisePlan>> getAllExercisePlan() {
        final String where = ExercisePlanState
                + " = '"
                + "activation"
                 + "' ";
        final Cursor mCursor = db.getField(ExercisePlanTable, ALL_FIELD, where, ExercisePlanName, Idx, null);
        ArrayList<DAO_ExercisePlan> mExercisePlan = new ArrayList<>();
        final ArrayList<ArrayList<DAO_ExercisePlan>> mExercisePlanList = new ArrayList<>();
        final int total = mCursor.getCount();
        int cnt = 0;
        while (mCursor.moveToNext()) {
            try {
                cnt += 1;
                DAO_ExercisePlan mDaoUser = new DAO_ExercisePlan();
                mDaoUser.setIdx(mCursor.getString(mCursor
                        .getColumnIndex(Idx)));
                mDaoUser.setExercisePlanExplanation(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanExplanation)));
                mDaoUser.setExercisePlanName(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanName)));
                mDaoUser.setExercisePlanPosture(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanPosture)));
                mDaoUser.setExercisePlanImageNumber(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanImageNumber)));
                mDaoUser.setExercisePlanIsBasic(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanProgramIsBasic)));
                mDaoUser.setExercisePlanConstructor(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanConstructor)));
                mDaoUser.setExercisePlanProgram(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanProgram)));
                mDaoUser.setExercisePlanRepeatCount(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanRepeatCount)));
                mDaoUser.setExercisePlanState(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanState)));
                mDaoUser.setExercisePlanTime(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanTime)));
                mDaoUser.setExercisePlanTitle(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanTitle)));
                mDaoUser.setExercisePlanType(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanType)));

                if (mExercisePlan.size() <= 4) {
                    mExercisePlan.add(mDaoUser);
                    if (mExercisePlan.size() == 4) {
                        mExercisePlanList.add(mExercisePlan);
                        mExercisePlan = new ArrayList<>();
                    }
                }
                if (total == cnt) {
                    if (!mExercisePlan.isEmpty()) {
                        mExercisePlanList.add(mExercisePlan);
                    }

                }
            } catch (SQLiteException | IllegalStateException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        mCursor.close();
        db.close();
        return mExercisePlanList;
    }

    public ArrayList<DAO_ExercisePlan> getWhereProgramNameExercisePlan(String programName){

        final String where = ExercisePlanProgram
                + " = '"
                + programName
                + "'";
        ArrayList<DAO_ExercisePlan> mExercisePlan = new ArrayList<>();
        final Cursor mCursor = db.getField(ExercisePlanTable, ALL_FIELD, where, ExercisePlanName, Idx, null);
        while (mCursor.moveToNext()) {
            try {
                DAO_ExercisePlan mDaoUser = new DAO_ExercisePlan();
                mDaoUser.setIdx(mCursor.getString(mCursor
                        .getColumnIndex(Idx)));
                mDaoUser.setExercisePlanExplanation(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanExplanation)));
                mDaoUser.setExercisePlanName(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanName)));
                mDaoUser.setExercisePlanPosture(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanPosture)));
                mDaoUser.setExercisePlanImageNumber(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanImageNumber)));

                mDaoUser.setExercisePlanProgram(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanProgram)));
                mDaoUser.setExercisePlanRepeatCount(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanRepeatCount)));
                mDaoUser.setExercisePlanState(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanState)));
                mDaoUser.setExercisePlanTime(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanTime)));
                mDaoUser.setExercisePlanTitle(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanTitle)));
                mDaoUser.setExercisePlanType(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanType)));

                    mExercisePlan.add(mDaoUser);
            } catch (SQLiteException | IllegalStateException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        mCursor.close();
        return mExercisePlan;
    }
    public ArrayList<ArrayList<DAO_ExercisePlan>> getWhereExercisePlan() {

        final String where = ExercisePlanType
                + " = '"
                + "Basic"
                + "' AND "
                + ExercisePlanState
                + " = '"
                + "activation"
                + "'";
        final Cursor mCursor = db.getField(ExercisePlanTable, ALL_FIELD, where, ExercisePlanName, Idx, null);
        ArrayList<DAO_ExercisePlan> mExercisePlan = new ArrayList<>();
        final ArrayList<ArrayList<DAO_ExercisePlan>> mExercisePlanList = new ArrayList<>();
        final int total = mCursor.getCount();
        int cnt = 0;
        while (mCursor.moveToNext()) {
            try {
                cnt += 1;
                DAO_ExercisePlan mDaoUser = new DAO_ExercisePlan();
                mDaoUser.setIdx(mCursor.getString(mCursor
                        .getColumnIndex(Idx)));
                mDaoUser.setExercisePlanExplanation(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanExplanation)));
                mDaoUser.setExercisePlanName(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanName)));
                mDaoUser.setExercisePlanPosture(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanPosture)));
                mDaoUser.setExercisePlanImageNumber(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanImageNumber)));

                mDaoUser.setExercisePlanProgram(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanProgram)));
                mDaoUser.setExercisePlanRepeatCount(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanRepeatCount)));
                mDaoUser.setExercisePlanState(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanState)));
                mDaoUser.setExercisePlanTime(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanTime)));
                mDaoUser.setExercisePlanTitle(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanTitle)));
                mDaoUser.setExercisePlanType(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanType)));

                if (mExercisePlan.size() <= 4) {
                    mExercisePlan.add(mDaoUser);
                    if (mExercisePlan.size() == 4) {
                        mExercisePlanList.add(mExercisePlan);
                        mExercisePlan = new ArrayList<>();
                    }
                }
                if (total == cnt) {
                    if (!mExercisePlan.isEmpty()) {
                        mExercisePlanList.add(mExercisePlan);
                    }

                }
            } catch (SQLiteException | IllegalStateException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        mCursor.close();
        db.close();
        return mExercisePlanList;
    }

    public ArrayList<DAO_ExercisePlan> getSettingExercisePlan() {

        final Cursor mCursor = db.getField(ExercisePlanTable, ALL_FIELD, null, ExercisePlanName, Idx, null);
        ArrayList<DAO_ExercisePlan> mExercisePlan = new ArrayList<>();
        while (mCursor.moveToNext()) {
            try {
                DAO_ExercisePlan mDaoExercisePlan = new DAO_ExercisePlan();
                mDaoExercisePlan.setIdx(mCursor.getString(mCursor
                        .getColumnIndex(Idx)));
                mDaoExercisePlan.setExercisePlanExplanation(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanExplanation)));
                mDaoExercisePlan.setExercisePlanName(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanName)));
                mDaoExercisePlan.setExercisePlanPosture(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanPosture)));
                mDaoExercisePlan.setExercisePlanImageNumber(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanImageNumber)));
                mDaoExercisePlan.setExercisePlanProgram(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanProgram)));
                mDaoExercisePlan.setExercisePlanRepeatCount(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanRepeatCount)));
                mDaoExercisePlan.setExercisePlanState(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanState)));
                mDaoExercisePlan.setExercisePlanTime(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanTime)));
                mDaoExercisePlan.setExercisePlanTitle(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanTitle)));
                mDaoExercisePlan.setExercisePlanType(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanType)));
                mDaoExercisePlan.setExercisePlanRegDate(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanRegDate)));
                mDaoExercisePlan.setExercisePlanConstructor(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanConstructor)));

                mExercisePlan.add(mDaoExercisePlan);

            } catch (SQLiteException | IllegalStateException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        mCursor.close();
        db.close();
        return mExercisePlan;
    }

    public String getPlanName(String name) {
        final String where = ExercisePlanProgram
                + " = '"
                + name
                + "' AND "
                + ProgramType
                + " = 'Basic'";

        return db.getField(ExercisePlanTable, UserId, where, null, null);
    }

    public String getProgramTime(String name) {
        final String where = ProgramName
                + " = '"
                + name
                + "'";

        return db.getField(ProgramTable, ProgramTime, where, null, null);
    }

    public String getProgramPulseOperationTime(String name) {
        final String where = ProgramName
                + " = '"
                + name
                + "'";

        return db.getField(ProgramTable, ProgramPulseOperationTime, where, null, null);
    }

    public String getProgramPulsePauseTime(String name) {
        final String where = ProgramName
                + " = '"
                + name
                + "'";

        return db.getField(ProgramTable, ProgramPulsePauseTime, where, null, null);
    }

    public boolean getIsProgramName(String name) {
        final String where = String.format(ProgramName + " = '%s'", name);
        final Cursor mCursor = db.getField(ExercisePlanTable, ALL_FIELD, where, Idx, null);
        boolean b = mCursor.getCount() != 1;
        mCursor.close();
        db.close();
        return b;
    }

    public ArrayList<DAO_Program> getProgramName(String name) {
        ArrayList<DAO_Program> mPrograms = new ArrayList<>();
        final String where = String.format(ProgramName + " = '%s'", name);
        final Cursor mCursor = db.getField(ExercisePlanTable, ALL_FIELD, where, Idx, null);
        while (mCursor.moveToNext()) {
            try {
                DAO_Program mDaoProgram = new DAO_Program();
                mDaoProgram.setIdx(mCursor.getString(mCursor
                        .getColumnIndex(Idx)));
                mDaoProgram.setProgramName(mCursor.getString(mCursor
                        .getColumnIndex(ProgramName)));
                mDaoProgram.setProgramState(mCursor.getString(mCursor
                        .getColumnIndex(ProgramState)));
                mDaoProgram.setProgramTime(mCursor.getString(mCursor
                        .getColumnIndex(ProgramTime)));
                mDaoProgram.setProgramStimulusIntensity(mCursor.getString(mCursor
                        .getColumnIndex(ProgramStimulusIntensity)));
                mDaoProgram.setProgramPulseOperationTime(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseOperationTime)));
                mDaoProgram.setProgramPulsePauseTime(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulsePauseTime)));
                mDaoProgram.setProgramFrequency(mCursor.getString(mCursor
                        .getColumnIndex(ProgramFrequency)));
                mDaoProgram.setProgramPulseWidth(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseWidth)));
                mDaoProgram.setProgramPulseRiseTime(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseRiseTime)));
                mDaoProgram.setProgramExplanation(mCursor.getString(mCursor
                        .getColumnIndex(ProgramExplanation)));
                mDaoProgram.setProgramTitle(mCursor.getString(mCursor
                        .getColumnIndex(ProgramTitle)));
                mDaoProgram.setProgramType(mCursor.getString(mCursor
                        .getColumnIndex(ProgramType)));

                mPrograms.add(mDaoProgram);
            } catch (SQLiteException | IllegalStateException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        mCursor.close();
        db.close();
        return mPrograms;
    }

    public ArrayList<DAO_ExercisePlan> getAllExercisePlan(String name) {
        final String where = String.format(ExercisePlanName + " = '%s'", name);
        final Cursor mCursor = db.getField(ExercisePlanTable, ALL_FIELD, where, Idx, null);
        ArrayList<DAO_ExercisePlan> mExercisePlan = new ArrayList<>();

        while (mCursor.moveToNext()) {
            try {

                DAO_ExercisePlan mDaoUser = new DAO_ExercisePlan();
                mDaoUser.setIdx(mCursor.getString(mCursor
                        .getColumnIndex(Idx)));
                mDaoUser.setExercisePlanExplanation(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanExplanation)));
                mDaoUser.setExercisePlanName(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanName)));
                mDaoUser.setExercisePlanPosture(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanPosture)));
                mDaoUser.setExercisePlanImageNumber(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanImageNumber)));

                mDaoUser.setExercisePlanProgram(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanProgram)));
                mDaoUser.setExercisePlanIsBasic(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanProgramIsBasic)));
                mDaoUser.setExercisePlanRepeatCount(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanRepeatCount)));
                mDaoUser.setExercisePlanState(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanState)));
                mDaoUser.setExercisePlanTime(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanTime)));
                mDaoUser.setExercisePlanTitle(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanTitle)));
                mDaoUser.setExercisePlanType(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanType)));
                mDaoUser.setExercisePlanRegDate(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanRegDate)));
                mDaoUser.setExercisePlanConstructor(mCursor.getString(mCursor
                        .getColumnIndex(ExercisePlanConstructor)));

                mExercisePlan.add(mDaoUser);

            } catch (SQLiteException | IllegalStateException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        mCursor.close();
        db.close();
        return mExercisePlan;
    }

    public ArrayList<ArrayList<DAO_Program>> getWhereProgram() {

        final String where = ProgramType
                + " = '"
                + "Basic"
                + "' AND "
                + ProgramState
                + " = '"
                + "activation"
                + "'";
        final Cursor mCursor = db.getField(ProgramTable, ALL_FIELD, where, ProgramName, Idx, null);
        ArrayList<DAO_Program> mProgram = new ArrayList<>();
        final ArrayList<ArrayList<DAO_Program>> mProgramList = new ArrayList<>();
        final int total = mCursor.getCount();
        int cnt = 0;
        while (mCursor.moveToNext()) {
            try {
                cnt += 1;
                DAO_Program mDaoProgram = new DAO_Program();
                mDaoProgram.setIdx(mCursor.getString(mCursor
                        .getColumnIndex(Idx)));
                mDaoProgram.setProgramName(mCursor.getString(mCursor
                        .getColumnIndex(ProgramName)));
                mDaoProgram.setProgramState(mCursor.getString(mCursor
                        .getColumnIndex(ProgramState)));
                mDaoProgram.setProgramTime(mCursor.getString(mCursor
                        .getColumnIndex(ProgramTime)));
                mDaoProgram.setProgramStimulusIntensity(mCursor.getString(mCursor
                        .getColumnIndex(ProgramStimulusIntensity)));
                mDaoProgram.setProgramPulseOperationTime(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseOperationTime)));
                mDaoProgram.setProgramPulsePauseTime(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulsePauseTime)));
                mDaoProgram.setProgramFrequency(mCursor.getString(mCursor
                        .getColumnIndex(ProgramFrequency)));
                mDaoProgram.setProgramPulseWidth(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseWidth)));
                mDaoProgram.setProgramPulseRiseTime(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseRiseTime)));
                mDaoProgram.setProgramExplanation(mCursor.getString(mCursor
                        .getColumnIndex(ProgramExplanation)));
                mDaoProgram.setProgramTitle(mCursor.getString(mCursor
                        .getColumnIndex(ProgramTitle)));
                mDaoProgram.setProgramType(mCursor.getString(mCursor
                        .getColumnIndex(ProgramType)));

                if (mProgram.size() <= 5) {
                    mProgram.add(mDaoProgram);
                    if (mProgram.size() == 5) {
                        mProgramList.add(mProgram);
                        mProgram = new ArrayList<>();
                    }

                }
                if (total == cnt) {
                    if (!mProgram.isEmpty()) {
                        mProgramList.add(mProgram);
                    }

                }
            } catch (SQLiteException | IllegalStateException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        mCursor.close();
        db.close();
        return mProgramList;
    }

    public ArrayList<ArrayList<DAO_Program>> getAllProgram() {
        final String where = ProgramState
                + " = '"
                + "activation"
                + "' ";
        final Cursor mCursor = db.getField(ProgramTable, ALL_FIELD, where, ProgramName, Idx, null);
        ArrayList<DAO_Program> mProgram = new ArrayList<>();
        final ArrayList<ArrayList<DAO_Program>> mProgramList = new ArrayList<>();
        final int total = mCursor.getCount();
        int cnt = 0;
        while (mCursor.moveToNext()) {
            try {
                cnt += 1;
                DAO_Program mDaoProgram = new DAO_Program();
                mDaoProgram.setIdx(mCursor.getString(mCursor
                        .getColumnIndex(Idx)));
                mDaoProgram.setProgramName(mCursor.getString(mCursor
                        .getColumnIndex(ProgramName)));
                mDaoProgram.setProgramState(mCursor.getString(mCursor
                        .getColumnIndex(ProgramState)));
                mDaoProgram.setProgramTime(mCursor.getString(mCursor
                        .getColumnIndex(ProgramTime)));
                mDaoProgram.setProgramStimulusIntensity(mCursor.getString(mCursor
                        .getColumnIndex(ProgramStimulusIntensity)));
                mDaoProgram.setProgramPulseOperationTime(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseOperationTime)));
                mDaoProgram.setProgramPulsePauseTime(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulsePauseTime)));
                mDaoProgram.setProgramFrequency(mCursor.getString(mCursor
                        .getColumnIndex(ProgramFrequency)));
                mDaoProgram.setProgramPulseWidth(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseWidth)));
                mDaoProgram.setProgramPulseRiseTime(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseRiseTime)));
                mDaoProgram.setProgramExplanation(mCursor.getString(mCursor
                        .getColumnIndex(ProgramExplanation)));
                mDaoProgram.setProgramTitle(mCursor.getString(mCursor
                        .getColumnIndex(ProgramTitle)));
                mDaoProgram.setProgramType(mCursor.getString(mCursor
                        .getColumnIndex(ProgramType)));

                if (mProgram.size() <= 5) {
                    mProgram.add(mDaoProgram);
                    if (mProgram.size() == 5) {
                        mProgramList.add(mProgram);
                        mProgram = new ArrayList<>();
                    }

                }
                if (total == cnt) {
                    if (!mProgram.isEmpty()) {
                        mProgramList.add(mProgram);
                    }

                }
            } catch (SQLiteException | IllegalStateException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        mCursor.close();
        db.close();
        return mProgramList;
    }

    public ArrayList<DAO_Program> getALLProgram() {

        final Cursor mCursor = db.getField(ProgramTable, ALL_FIELD, null, ProgramName, Idx, null);
        ArrayList<DAO_Program> mProgram = new ArrayList<>();
        while (mCursor.moveToNext()) {
            try {
                DAO_Program mDaoProgram = new DAO_Program();
                mDaoProgram.setIdx(mCursor.getString(mCursor
                        .getColumnIndex(Idx)));
                mDaoProgram.setProgramType(mCursor.getString(mCursor
                        .getColumnIndex(ProgramType)));

                    mDaoProgram.setProgramName(mCursor.getString(mCursor
                            .getColumnIndex(ProgramName)));

                mDaoProgram.setProgramState(mCursor.getString(mCursor
                        .getColumnIndex(ProgramState)));
                mDaoProgram.setProgramTime(mCursor.getString(mCursor
                        .getColumnIndex(ProgramTime)));
                mDaoProgram.setProgramStimulusIntensity(mCursor.getString(mCursor
                        .getColumnIndex(ProgramStimulusIntensity)));
                mDaoProgram.setProgramStimulusIntensityProgress(mCursor.getString(mCursor
                        .getColumnIndex(ProgramStimulusIntensityProgress)));
                mDaoProgram.setProgramPulseOperationTime(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseOperationTime)));
                mDaoProgram.setProgramPulseOperationTimeProgress(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseOperationTimeProgress)));
                mDaoProgram.setProgramPulsePauseTime(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulsePauseTime)));
                mDaoProgram.setProgramPulsePauseTimeProgress(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulsePauseTimeProgress)));
                mDaoProgram.setProgramFrequency(mCursor.getString(mCursor
                        .getColumnIndex(ProgramFrequency)));
                mDaoProgram.setProgramFrequencyProgress(mCursor.getString(mCursor
                        .getColumnIndex(ProgramFrequencyProgress)));
                mDaoProgram.setProgramPulseWidth(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseWidth)));
                mDaoProgram.setProgramPulseWidthProgress(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseWidthProgress)));
                mDaoProgram.setProgramPulseRiseTime(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseRiseTime)));
                mDaoProgram.setProgramPulseRiseTimeProgress(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseRiseTimeProgress)));
                mDaoProgram.setProgramExplanation(mCursor.getString(mCursor
                        .getColumnIndex(ProgramExplanation)));
                mDaoProgram.setProgramTitle(mCursor.getString(mCursor
                        .getColumnIndex(ProgramTitle)));

                mDaoProgram.setProgramConstructor(mCursor.getString(mCursor
                        .getColumnIndex(ProgramConstructor)));
                mDaoProgram.setProgramRegDate(mCursor.getString(mCursor
                        .getColumnIndex(ProgramRegDate)));

                mProgram.add(mDaoProgram);

            } catch (SQLiteException | IllegalStateException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        mCursor.close();
        db.close();
        return mProgram;
    }

    public ArrayList<DAO_Program> getALLProgramActivated() {
        final String where = ProgramState
                + " = '"
                + "activation"
                + "' ";
        final Cursor mCursor = db.getField(ProgramTable, ALL_FIELD, where, ProgramName, Idx, null);

        ArrayList<DAO_Program> mProgram = new ArrayList<>();
        while (mCursor.moveToNext()) {
            try {
                DAO_Program mDaoProgram = new DAO_Program();
                mDaoProgram.setIdx(mCursor.getString(mCursor
                        .getColumnIndex(Idx)));
                mDaoProgram.setProgramType(mCursor.getString(mCursor
                        .getColumnIndex(ProgramType)));

                mDaoProgram.setProgramName(mCursor.getString(mCursor
                        .getColumnIndex(ProgramName)));

                mDaoProgram.setProgramState(mCursor.getString(mCursor
                        .getColumnIndex(ProgramState)));
                mDaoProgram.setProgramTime(mCursor.getString(mCursor
                        .getColumnIndex(ProgramTime)));
                mDaoProgram.setProgramStimulusIntensity(mCursor.getString(mCursor
                        .getColumnIndex(ProgramStimulusIntensity)));
                mDaoProgram.setProgramStimulusIntensityProgress(mCursor.getString(mCursor
                        .getColumnIndex(ProgramStimulusIntensityProgress)));
                mDaoProgram.setProgramPulseOperationTime(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseOperationTime)));
                mDaoProgram.setProgramPulseOperationTimeProgress(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseOperationTimeProgress)));
                mDaoProgram.setProgramPulsePauseTime(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulsePauseTime)));
                mDaoProgram.setProgramPulsePauseTimeProgress(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulsePauseTimeProgress)));
                mDaoProgram.setProgramFrequency(mCursor.getString(mCursor
                        .getColumnIndex(ProgramFrequency)));
                mDaoProgram.setProgramFrequencyProgress(mCursor.getString(mCursor
                        .getColumnIndex(ProgramFrequencyProgress)));
                mDaoProgram.setProgramPulseWidth(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseWidth)));
                mDaoProgram.setProgramPulseWidthProgress(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseWidthProgress)));
                mDaoProgram.setProgramPulseRiseTime(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseRiseTime)));
                mDaoProgram.setProgramPulseRiseTimeProgress(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseRiseTimeProgress)));
                mDaoProgram.setProgramExplanation(mCursor.getString(mCursor
                        .getColumnIndex(ProgramExplanation)));
                mDaoProgram.setProgramTitle(mCursor.getString(mCursor
                        .getColumnIndex(ProgramTitle)));

                mDaoProgram.setProgramConstructor(mCursor.getString(mCursor
                        .getColumnIndex(ProgramConstructor)));
                mDaoProgram.setProgramRegDate(mCursor.getString(mCursor
                        .getColumnIndex(ProgramRegDate)));

                mProgram.add(mDaoProgram);

            } catch (SQLiteException | IllegalStateException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        mCursor.close();
        db.close();
        return mProgram;
    }


    public ArrayList<DAO_Program> getALLProgramByName(String name) {
        final String where = String.format(ProgramName + " = '%s'", name);
        final Cursor mCursor = db.getField(ProgramTable, ALL_FIELD, where, ProgramName, Idx, null);
        ArrayList<DAO_Program> mProgram = new ArrayList<>();
        while (mCursor.moveToNext()) {
            try {
                DAO_Program mDaoProgram = new DAO_Program();
                mDaoProgram.setIdx(mCursor.getString(mCursor
                        .getColumnIndex(Idx)));
                mDaoProgram.setProgramType(mCursor.getString(mCursor
                        .getColumnIndex(ProgramType)));

                mDaoProgram.setProgramName(mCursor.getString(mCursor
                        .getColumnIndex(ProgramName)));

                mDaoProgram.setProgramState(mCursor.getString(mCursor
                        .getColumnIndex(ProgramState)));
                mDaoProgram.setProgramTime(mCursor.getString(mCursor
                        .getColumnIndex(ProgramTime)));
                mDaoProgram.setProgramStimulusIntensity(mCursor.getString(mCursor
                        .getColumnIndex(ProgramStimulusIntensity)));
                mDaoProgram.setProgramStimulusIntensityProgress(mCursor.getString(mCursor
                        .getColumnIndex(ProgramStimulusIntensityProgress)));
                mDaoProgram.setProgramPulseOperationTime(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseOperationTime)));
                mDaoProgram.setProgramPulseOperationTimeProgress(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseOperationTimeProgress)));
                mDaoProgram.setProgramPulsePauseTime(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulsePauseTime)));
                mDaoProgram.setProgramPulsePauseTimeProgress(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulsePauseTimeProgress)));
                mDaoProgram.setProgramFrequency(mCursor.getString(mCursor
                        .getColumnIndex(ProgramFrequency)));
                mDaoProgram.setProgramFrequencyProgress(mCursor.getString(mCursor
                        .getColumnIndex(ProgramFrequencyProgress)));
                mDaoProgram.setProgramPulseWidth(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseWidth)));
                mDaoProgram.setProgramPulseWidthProgress(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseWidthProgress)));
                mDaoProgram.setProgramPulseRiseTime(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseRiseTime)));
                mDaoProgram.setProgramPulseRiseTimeProgress(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseRiseTimeProgress)));
                mDaoProgram.setProgramExplanation(mCursor.getString(mCursor
                        .getColumnIndex(ProgramExplanation)));
                mDaoProgram.setProgramTitle(mCursor.getString(mCursor
                        .getColumnIndex(ProgramTitle)));

                mDaoProgram.setProgramConstructor(mCursor.getString(mCursor
                        .getColumnIndex(ProgramConstructor)));
                mDaoProgram.setProgramRegDate(mCursor.getString(mCursor
                        .getColumnIndex(ProgramRegDate)));

                mProgram.add(mDaoProgram);

            } catch (SQLiteException | IllegalStateException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        mCursor.close();
        db.close();
        return mProgram;
    }


    // 프로그램 중 사용할 수 있는 것만 가져옴.
    public ArrayList<DAO_Program> getProgramsForNewExercisePlan() {

        // active인 것 중 operation time이 실제 있는것만
        final String where = ProgramState
                + " = '"
                + "activation"
                + "' AND "
                + ProgramPulseOperationTime
                + "!= '"
                + "duration"
                + "'";
        final Cursor mCursor = db.getField(ProgramTable, ALL_FIELD, where, ProgramName, Idx, null);
        ArrayList<DAO_Program> mProgram = new ArrayList<>();
        while (mCursor.moveToNext()) {
            try {
                DAO_Program mDaoProgram = new DAO_Program();
                mDaoProgram.setIdx(mCursor.getString(mCursor
                        .getColumnIndex(Idx)));
                mDaoProgram.setProgramType(mCursor.getString(mCursor
                        .getColumnIndex(ProgramType)));

                mDaoProgram.setProgramName(mCursor.getString(mCursor
                        .getColumnIndex(ProgramName)));

                mDaoProgram.setProgramState(mCursor.getString(mCursor
                        .getColumnIndex(ProgramState)));
                mDaoProgram.setProgramTime(mCursor.getString(mCursor
                        .getColumnIndex(ProgramTime)));
                mDaoProgram.setProgramStimulusIntensity(mCursor.getString(mCursor
                        .getColumnIndex(ProgramStimulusIntensity)));
                mDaoProgram.setProgramStimulusIntensityProgress(mCursor.getString(mCursor
                        .getColumnIndex(ProgramStimulusIntensityProgress)));
                mDaoProgram.setProgramPulseOperationTime(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseOperationTime)));
                mDaoProgram.setProgramPulseOperationTimeProgress(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseOperationTimeProgress)));
                mDaoProgram.setProgramPulsePauseTime(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulsePauseTime)));
                mDaoProgram.setProgramPulsePauseTimeProgress(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulsePauseTimeProgress)));
                mDaoProgram.setProgramFrequency(mCursor.getString(mCursor
                        .getColumnIndex(ProgramFrequency)));
                mDaoProgram.setProgramFrequencyProgress(mCursor.getString(mCursor
                        .getColumnIndex(ProgramFrequencyProgress)));
                mDaoProgram.setProgramPulseWidth(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseWidth)));
                mDaoProgram.setProgramPulseWidthProgress(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseWidthProgress)));
                mDaoProgram.setProgramPulseRiseTime(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseRiseTime)));
                mDaoProgram.setProgramPulseRiseTimeProgress(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseRiseTimeProgress)));
                mDaoProgram.setProgramExplanation(mCursor.getString(mCursor
                        .getColumnIndex(ProgramExplanation)));
                mDaoProgram.setProgramTitle(mCursor.getString(mCursor
                        .getColumnIndex(ProgramTitle)));

                mDaoProgram.setProgramConstructor(mCursor.getString(mCursor
                        .getColumnIndex(ProgramConstructor)));
                mDaoProgram.setProgramRegDate(mCursor.getString(mCursor
                        .getColumnIndex(ProgramRegDate)));

                mProgram.add(mDaoProgram);

            } catch (SQLiteException | IllegalStateException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        mCursor.close();
        db.close();
        return mProgram;
    }

    public boolean userRemove(String idx) {
        final String where = String.format(Idx + " = '%s'", idx);
        return db.dataDelete(UserTable, where) != 0;
    }

    public boolean ExercisePlanRemove(String name) {
        final String where = String.format(ExercisePlanName + " = '%s'", name);
        return db.dataDelete(ExercisePlanTable, where) != 0;
    }

    public boolean programRemove(String idx) {
        final String where = String.format(Idx + " = '%s'", idx);
        return db.dataDelete(ProgramTable, where) != 0;
    }

    public boolean favoritesRemove(String linkIdx, String userId) {

        final String where = String.format(FavoritesLinkIdx + " = '%s' AND " + FavoritesUserId + " = '%s'", linkIdx, userId);
        return db.dataDelete(FavoritesTable, where) != 0;
    }
    public boolean favoritesRemoveName(String name) {

        final String where = String.format(FavoritesName + " = '%s'  ", name);
        return db.dataDelete(FavoritesTable, where) != 0;
    }
    public boolean favoritesRemoveAll(String linkIdx) {

        final String where = String.format(FavoritesLinkIdx + " = '%s' ", linkIdx);
        return db.dataDelete(FavoritesTable, where) != 0;
    }
}
