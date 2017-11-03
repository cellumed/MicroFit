package com.cellumed.healthcare.microfit.DAO;

/**
 * Created by ikoob on 2016. 5. 24..
 */
public class DAORegUser {

    boolean type; //  true 유선 false 무선
    int centerProgress;
    int progress1;
    int progress2;
    int progress3;
    int progress4;
    int progress5;
    int progress6;
    int progress7;
    int progress8;
    int progress9;
    int progress10;
    String startTime;
    String endTime;
    int imu;
    int ecg;
    String diveceId;


    public String getDeveceId() {
        return diveceId;
    }

    public void setDiveceId(String diveceId) {
        this.diveceId = diveceId;
    }

    public int getImu() {
        return imu;
    }

    public void setImu(int imu) {
        this.imu = imu;
    }

    public int getEcg() {
        return ecg;
    }

    public void setEcg(int ecg) {
        this.ecg = ecg;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getCenterProgress() {
        return centerProgress;
    }

    public void setCenterProgress(int centerProgress) {
        this.centerProgress = centerProgress;
    }

    public int getProgress1() {
        return progress1;
    }

    public void setProgress1(int progress1) {
        this.progress1 = progress1;
    }

    public int getProgress2() {
        return progress2;
    }

    public void setProgress2(int progress2) {
        this.progress2 = progress2;
    }

    public int getProgress3() {
        return progress3;
    }

    public void setProgress3(int progress3) {
        this.progress3 = progress3;
    }

    public int getProgress4() {
        return progress4;
    }

    public void setProgress4(int progress4) {
        this.progress4 = progress4;
    }

    public int getProgress5() {
        return progress5;
    }

    public void setProgress5(int progress5) {
        this.progress5 = progress5;
    }

    public int getProgress6() {
        return progress6;
    }

    public void setProgress6(int progress6) {
        this.progress6 = progress6;
    }

    public int getProgress7() {
        return progress7;
    }

    public void setProgress7(int progress7) {
        this.progress7 = progress7;
    }

    public int getProgress8() {
        return progress8;
    }

    public void setProgress8(int progress8) {
        this.progress8 = progress8;
    }

    public int getProgress9() {
        return progress9;
    }

    public void setProgress9(int progress9) {
        this.progress9 = progress9;
    }

    public int getProgress10() {
        return progress10;
    }

    public void setProgress10(int progress10) {
        this.progress10 = progress10;
    }

    public boolean isType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
    }
}
