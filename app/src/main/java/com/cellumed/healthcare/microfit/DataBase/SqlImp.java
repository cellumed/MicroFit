package com.cellumed.healthcare.microfit.DataBase;

public interface SqlImp {
    String DATABASE_NAME = "ems.db";
    int DATABASE_VERSION = 1;
    String UserTable = "UserTable";
    String WorkoutDataTable = "WorkoutDataTable";
    String FavoritesTable = "FavoritesTable"; //즐겨찾기
    String ExercisePlanTable = "ExercisePlanTable"; //운동계획
    String ProgramTable = "ProgramTable"; //운동프로그램
    String UserId = "id";
    String UserPass = "pass";
    String UserType = "type";
    String Idx = "idx";
    String UserName = "name";
    String UserGender = "gender";
    String UserBirthday = "birthday";
    String UserRegDate = "reg_date";
    String UserUpDate = "up_date";
    String UserExplanation = "explanation"; //계획

    String ExercisePlanName = "name"; //계획 이름
    String ExercisePlanTitle = "title"; //계획 제목
    String ExercisePlanType = "type"; //계획 타잎
    String ExercisePlanState = "state"; //계획 상태
    String ExercisePlanProgram = "program"; //계획 프로그램
    String ExercisePlanProgramIsBasic = "pro_isBasic"; //계획 프로그램 페이직여부 체크
    String ExercisePlanTime = "time"; //계획 운동시간
    String ExercisePlanPosture = "posture"; //계획 자세
    //String ExercisePlanPostureUS = "posture_us"; //계획 자세
    String ExercisePlanImageNumber = "image_number"; //계획 자세
    String ExercisePlanRepeatCount = "repeat_count"; //계획 반복 횟수
    String ExercisePlanExplanation = "explanation"; //계획 설명
    String ExercisePlanConstructor = "constructor"; //계획 생성자
    String ExercisePlanRegDate = "reg_date"; //계획 생성자

    String ProgramName = "name"; //프로그램 이름
    String ProgramState = "state"; //프로그램 상태
    String ProgramTime = "time"; //프로그램 시간
    String ProgramStimulusIntensity = "stimulus_intensity"; // 자극강도
    String ProgramStimulusIntensityProgress = "stimulus_intensity_progress"; // 자극강도
    String ProgramPulseOperationTime = "pulse_operation_time"; //동작시간
    String ProgramPulseOperationTimeProgress = "pulse_operation_time_progress"; //동작시간
    String ProgramPulsePauseTime = "pulse_pause_time"; //휴지 시간
    String ProgramPulsePauseTimeProgress = "pulse_pause_time_progress"; //휴지 시간
    String ProgramFrequency = "frequency";//주파수
    String ProgramFrequencyProgress = "frequency_progress";//주파수
    String ProgramPulseWidth = "pulse_width";//펄스폭
    String ProgramPulseWidthProgress = "pulse_width_progress";//펄스폭
    String ProgramPulseRiseTime = "pulse_rise_time"; //펄스 상승시간
    String ProgramPulseRiseTimeProgress = "pulse_rise_time_progress"; //펄스 상승시간
    String ProgramExplanation = "explanation";//설명
    String ProgramTitle = "title"; //계획 제목
    String ProgramType = "type"; //계획 타잎
    String ProgramConstructor = "constructor"; //프로그램 생성자
    String ProgramRegDate = "reg_date"; //프로그램 생성자


    String FavoritesName = "name";//즐겨찾기 이름
    String FavoritesProgram = "program";//즐겨찾기 프로그램
    String FavoritesProgramIsBasic = "pro_isBasic";//즐겨찾기 프로그램
    String FavoritesTime = "time";//즐겨찾기 운동시간
    String FavoritesUserId = "user_id";//즐겨찾기 등록 아이디
    String FavoritesLinkIdx = "link_idx";//즐겨찾기 등록 idx
    String FavoritesType = "type1";//즐겨찾기 등록 아이디
    String FavoritesType2 = "type2"; //계획 타잎
    String FavoritesState = "state"; //계획 상태
    String FavoritesPosture = "posture"; //계획 자세
    //String FavoritesPostureUS = "posture_us"; //계획 자세
    String FavoritesImageNumber = "image_number"; //계획 자세
    String FavoritesTitle = "title"; //계획 자세
    String FavoritesRepeatCount = "repeat_count"; //계획 반복 횟수
    String FavoritesStimulusIntensity = "stimulus_intensity"; // 자극강도
    String FavoritesPulseOperationTime = "pulse_operation_time"; //동작시간
    String FavoritesPulsePauseTime = "pulse_pause_time"; //휴지 시간
    String FavoritesFrequency = "frequency";//주파수
    String FavoritesPulseWidth = "pulse_width";//펄스폭
    String FavoritesPulseRiseTime = "pulse_rise_time"; //펄스 상승시간
    String FavoritesExplanation = "explanation";//설명
    String FavoritesConstructor = "constructor"; //프로그램 생성자
    String FavoritesRegDate = "reg_date"; //생성일자

    String WorkoutDataStartDate = "start_date"; //시작시간
    String WorkoutDataEndDate = "end_date"; //종료시간
    String WorkoutDataTime = "time"; //운동시간
    String WorkoutDataContent = "content"; //내용
    String WorkoutDataETC = "etc"; //내용

    String ORDER_BY = "ORDER BY";
    String BETWEEN = "BETWEEN";
    String DESC = "DESC";
    String ASC = "ASC";
    String ALL_FIELD[] ={"*"};

}
