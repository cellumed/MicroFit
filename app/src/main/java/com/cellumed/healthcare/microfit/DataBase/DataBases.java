package com.cellumed.healthcare.microfit.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.cellumed.healthcare.microfit.R;
import com.cellumed.healthcare.microfit.Util.BudUtil;


public class DataBases extends SQLiteOpenHelper implements SqlImp {
    private  Context mContext;


    public DataBases(Context mContext) {
        super(mContext, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = mContext;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String favorites_create_sql = "CREATE TABLE IF NOT EXISTS "+ FavoritesTable +"("
                + "idx Integer PRIMARY KEY AUTOINCREMENT,"
                + FavoritesName +" TEXT  ,"
                + FavoritesProgram +" TEXT,"
                + FavoritesProgramIsBasic +" TEXT,"
                + FavoritesTime+" TEXT,"
                + FavoritesUserId+" TEXT,"
                + FavoritesLinkIdx+" TEXT,"
                + FavoritesTitle+" TEXT,"
                + FavoritesType+" TEXT,"
                + FavoritesType2+" TEXT,"
                + FavoritesState+" TEXT,"
                + FavoritesPosture+" TEXT,"
                + FavoritesImageNumber+" TEXT,"
                + FavoritesRepeatCount+" TEXT,"
                + FavoritesStimulusIntensity+" TEXT,"
                + FavoritesPulseOperationTime+" TEXT,"
                + FavoritesPulsePauseTime+" TEXT,"
                + FavoritesFrequency+" TEXT,"
                + FavoritesPulseWidth+" TEXT,"
                + FavoritesRegDate+" TEXT,"
                + FavoritesPulseRiseTime+" TEXT,"
                + FavoritesConstructor+" TEXT,"
                + FavoritesExplanation+" TEXT );";

        db.execSQL(favorites_create_sql);

        String exercise_plan_create_sql = "CREATE TABLE IF NOT EXISTS "+ ExercisePlanTable +"("
                + "idx Integer PRIMARY KEY AUTOINCREMENT,"
                + ExercisePlanName +" TEXT  ,"
                + ExercisePlanState +" TEXT,"
                + ExercisePlanType +" TEXT,"
                + ExercisePlanProgram+" TEXT,"
                + ExercisePlanProgramIsBasic+" TEXT,"
                + ExercisePlanTime+" TEXT,"
                + ExercisePlanPosture+" TEXT,"
                + ExercisePlanImageNumber+" TEXT,"
                + ExercisePlanTitle+" TEXT,"
                + ExercisePlanRegDate+" TEXT,"
                + ExercisePlanConstructor+" TEXT,"
                + ExercisePlanRepeatCount+" TEXT,"
                + ExercisePlanExplanation+" TEXT );";
        db.execSQL(exercise_plan_create_sql);

        String program_create_sql = "CREATE TABLE IF NOT EXISTS "+ ProgramTable +"("
                + "idx Integer PRIMARY KEY AUTOINCREMENT,"
                + ProgramName +" TEXT  ,"
                + ProgramState +" TEXT,"
                + ProgramTime+" TEXT,"
                + ProgramStimulusIntensity+" TEXT,"
                + ProgramStimulusIntensityProgress+" TEXT,"
                + ProgramPulseOperationTime+" TEXT,"
                + ProgramPulseOperationTimeProgress+" TEXT,"
                + ProgramPulsePauseTime+" TEXT,"
                + ProgramPulsePauseTimeProgress+" TEXT,"
                + ProgramFrequency+" TEXT,"
                + ProgramFrequencyProgress+" TEXT,"
                + ProgramPulseWidth+" TEXT,"
                + ProgramPulseWidthProgress+" TEXT,"
                + ProgramType+" TEXT,"
                + ProgramTitle+" TEXT,"
                + ProgramRegDate+" TEXT,"
                + ProgramConstructor+" TEXT,"
                + ProgramPulseRiseTime+" TEXT,"
                + ProgramPulseRiseTimeProgress+" TEXT,"
                + ProgramExplanation+" TEXT );";
        db.execSQL(program_create_sql);

        String workout_data_create_sql = "CREATE TABLE IF NOT EXISTS "+ WorkoutDataTable +"("
                + "idx Integer PRIMARY KEY AUTOINCREMENT,"
                + WorkoutDataStartDate +" TEXT  ,"
                + WorkoutDataEndDate +" TEXT,"
                + WorkoutDataTime + " TEXT,"
                + WorkoutDataContent+" TEXT,"
                + WorkoutDataETC+" TEXT );";

        db.execSQL(workout_data_create_sql);

      //  makeUserAdmin(db);
        makeBasicExercisePlan(db);
    }
    private  void makeBasicExercisePlan(SQLiteDatabase db ){
        final String[] fitnessName = mContext.getResources().getStringArray(R.array.FitnessName);
        final String[] FitnessImageNumber = mContext.getResources().getStringArray(R.array.FitnessImageNumber);
        final String[] fitnessRepeatCount = mContext.getResources().getStringArray(R.array.FitnessRepeatCount);
        final String[] muscleStrengthening = mContext.getResources().getStringArray(R.array.MuscleStrengthening);
        final String[] MuscleStrengtheningImageNumber = mContext.getResources().getStringArray(R.array.MuscleStrengtheningImageNumber);
        final String[] muscleStrengtheningRepeatCount = mContext.getResources().getStringArray(R.array.MuscleStrengtheningRepeatCount);
        final String[] exercisePerformance = mContext.getResources().getStringArray(R.array.ExercisePerformance);
        final String[] ExercisePerformanceImageNumber = mContext.getResources().getStringArray(R.array.ExercisePerformanceImageNumber);
        final String[] exercisePerformanceRepeatCount = mContext.getResources().getStringArray(R.array.ExercisePerformanceRepeatCount);
        final String[] managementBody = mContext.getResources().getStringArray(R.array.ManagementBody);
        final String[] ManagementBodyImageNumber = mContext.getResources().getStringArray(R.array.ManagementBodyImageNumber);
        final String[] managementBodyRepeatCount = mContext.getResources().getStringArray(R.array.ManagementBodyRepeatCount);
        final String[] program = {"program1","program2","program3","program4","program5"};
        final String[] programExercisePlanTime = mContext.getResources().getStringArray(R.array.ProgramExercisePlanTime);
        final String[] programFrequency = mContext.getResources().getStringArray(R.array.ProgramFrequency);
//		final String[] programOperationTime = mContext.getResources().getStringArray(R.array.ProgramOperationTime);
        final String[] programOperationTime = {"duration","4","4","duration","1"};
//		final String[] programPauseTime = mContext.getResources().getStringArray(R.array.ProgramPauseTime);
        final String[] programPauseTime = {"none","4","4","none","1"};
//		final String[] programRiseTime = mContext.getResources().getStringArray(R.array.ProgramRiseTime);
        final String[] programRiseTime = {"none","0.4","none","none","none"};
        final String[] programWidth = mContext.getResources().getStringArray(R.array.ProgramWidth);

        for (int i = 0; i < fitnessName.length; i++) {
            final ContentValues mValues = new ContentValues();
            mValues.put(ExercisePlanName, mContext.getResources().getString(R.string.fitness));
            mValues.put(ExercisePlanState,"activation");
            mValues.put(ExercisePlanProgram,"program2");
            mValues.put(ExercisePlanProgramIsBasic,"true");
            mValues.put(ExercisePlanTime,String.format("%s",String.valueOf(BudUtil.getInstance().calcMinute(20))));
            mValues.put(ExercisePlanPosture,fitnessName[i]);
            mValues.put(ExercisePlanImageNumber,FitnessImageNumber[i]);
            mValues.put(ExercisePlanRepeatCount,fitnessRepeatCount[i]);
            mValues.put(ExercisePlanExplanation,"-");
            mValues.put(ExercisePlanTitle,"ExercisePlanBasic");
            mValues.put(ExercisePlanType,"Basic");
            mValues.put(ExercisePlanConstructor,"admin");
            mValues.put(ExercisePlanRegDate,BudUtil.getInstance().getToday("yyyy.MM.dd"));
            db.insert(ExercisePlanTable, null, mValues);
        }
        for (int i = 0; i < muscleStrengthening.length; i++) {
            final ContentValues mValues = new ContentValues();
            mValues.put(ExercisePlanName, mContext.getResources().getString(R.string.Muscles));
            mValues.put(ExercisePlanState,"activation");
            mValues.put(ExercisePlanProgram,"program2");
            mValues.put(ExercisePlanProgramIsBasic,"true");
            mValues.put(ExercisePlanTime,String.format("%s",String.valueOf(BudUtil.getInstance().calcMinute(20))));
            mValues.put(ExercisePlanPosture,muscleStrengthening[i]);
            mValues.put(ExercisePlanImageNumber,MuscleStrengtheningImageNumber[i]);
            mValues.put(ExercisePlanRepeatCount,muscleStrengtheningRepeatCount[i]);
            mValues.put(ExercisePlanExplanation,"-");
            mValues.put(ExercisePlanTitle,"ExercisePlanBasic");
            mValues.put(ExercisePlanType,"Basic");
            mValues.put(ExercisePlanConstructor,"admin");
            mValues.put(ExercisePlanRegDate, BudUtil.getInstance().getToday("yyyy.MM.dd"));
            db.insert(ExercisePlanTable, null, mValues);
        }
        for (int i = 0; i < exercisePerformance.length; i++) {
            final ContentValues mValues = new ContentValues();
            mValues.put(ExercisePlanName,mContext.getResources().getString(R.string.ExercisePerformance));
            mValues.put(ExercisePlanState,"activation");
            mValues.put(ExercisePlanProgram,"program3");
            mValues.put(ExercisePlanProgramIsBasic,"true");
            mValues.put(ExercisePlanTime,String.format("%s",String.valueOf(BudUtil.getInstance().calcMinute(20))));
            mValues.put(ExercisePlanPosture,exercisePerformance[i]);
            mValues.put(ExercisePlanImageNumber,ExercisePerformanceImageNumber[i]);
            mValues.put(ExercisePlanRepeatCount,exercisePerformanceRepeatCount[i]);
            mValues.put(ExercisePlanExplanation,"-");
            mValues.put(ExercisePlanTitle,"ExercisePlanBasic");
            mValues.put(ExercisePlanType,"Basic");
            mValues.put(ExercisePlanRegDate,BudUtil.getInstance().getToday("yyyy.MM.dd"));
            mValues.put(ExercisePlanConstructor,"admin");
            db.insert(ExercisePlanTable, null, mValues);
        }
        for (int i = 0; i < managementBody.length; i++) {
            final ContentValues mValues = new ContentValues();
            mValues.put(ExercisePlanName,mContext.getResources().getString(R.string.ManagementBody));
            mValues.put(ExercisePlanState,"activation");
            mValues.put(ExercisePlanProgram,"program2");
            mValues.put(ExercisePlanProgramIsBasic,"true");
            mValues.put(ExercisePlanTime,String.format("%s",String.valueOf(BudUtil.getInstance().calcMinute(20))));
            mValues.put(ExercisePlanPosture,managementBody[i]);
            mValues.put(ExercisePlanImageNumber,ManagementBodyImageNumber[i]);
            mValues.put(ExercisePlanRepeatCount,managementBodyRepeatCount[i]);
            mValues.put(ExercisePlanExplanation,"-");
            mValues.put(ExercisePlanTitle,"ExercisePlanBasic");
            mValues.put(ExercisePlanType,"Basic");
            mValues.put(ExercisePlanConstructor,"admin");
            mValues.put(ExercisePlanRegDate,BudUtil.getInstance().getToday("yyyy.MM.dd"));
            db.insert(ExercisePlanTable, null, mValues);
        }
        int resId;
        for (int i = 0; i < program.length; i++) {
            final ContentValues mValues = new ContentValues();
            resId=mContext.getResources().getIdentifier(program[i], "string", mContext.getPackageName());
            mValues.put(ProgramName, mContext.getResources().getString( resId ));
            mValues.put(ProgramState,"activation");
            mValues.put(ProgramTime,String.format("%s",String.valueOf(BudUtil.getInstance().calcMinute(Integer.parseInt(programExercisePlanTime[i])))));
            mValues.put(ProgramStimulusIntensity,"-");
            mValues.put(ProgramPulseOperationTime,programOperationTime[i]);
            mValues.put(ProgramPulsePauseTime,programPauseTime[i]);
            mValues.put(ProgramFrequency,programFrequency[i]);
            mValues.put(ProgramPulseWidth,programWidth[i]);
            mValues.put(ProgramPulseRiseTime,programRiseTime[i]);
            mValues.put(ProgramExplanation,"-");
            mValues.put(ProgramTitle,"ExercisePlanBasic");
            mValues.put(ProgramType,"Basic");
            mValues.put(ProgramRegDate,BudUtil.getInstance().getToday("yyyy.MM.dd"));
            mValues.put(ProgramConstructor,"admin");
            db.insert(ProgramTable, null, mValues);
        }

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropAllTables(db);
        onCreate(db);
    }

    public void dropAllTables(SQLiteDatabase db) {
        String drop_blood = "DROP TABLE IF EXISTS "+ UserTable +" ;";
        db.execSQL(drop_blood);
        String drop_favorites = "DROP TABLE IF EXISTS "+ FavoritesTable +" ;";
        db.execSQL(drop_favorites);
        String drop_exercise_plan = "DROP TABLE IF EXISTS "+ ExercisePlanTable +" ;";
        db.execSQL(drop_exercise_plan);
        String drop_program = "DROP TABLE IF EXISTS "+ ProgramTable +" ;";
        db.execSQL(drop_program);
        String drop_data = "DROP TABLE IF EXISTS "+ WorkoutDataTable +" ;";
        db.execSQL(drop_data);
    }
    public void reset(){
        SQLiteDatabase db = this.getWritableDatabase();
        dropAllTables(db);
        onCreate(db);
    }

}
