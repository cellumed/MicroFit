package com.cellumed.healthcare.microfit.Bluetooth;

/**
 * Created by ikoob on 2016. 5. 25..
 */
public interface IMP_CMD {
     String CMD_ACK = "01";
     String CMD_NAK = "02";
     String CMD_STATUS_REQ = "10";
     String CMD_STATUS_RSP = "11";
     String CMD_ID_INFO ="20";
     String CMD_INPUT_INFO ="21";
     //String CMD_SENSOR_INFO ="21";
     String CMD_USER_PARAM = "28";
     String CMD_LEVEL_PARAM ="2A";
     String CMD_START_REQ = "30";
     String CMD_STOP_REQ = "40";
     // error code
     String ERR_NONE_DATA = "00";
     String ERR_INVALID_LENGTH = "A0";
     String ERR_INVALID_CHECKSUM = "A1";
     String ERR_INVALID_PARAM = "A2";
     String ERR_INVALID_SEQ = "A3";
     String ERR_INVALID_ID = "A5";


}
