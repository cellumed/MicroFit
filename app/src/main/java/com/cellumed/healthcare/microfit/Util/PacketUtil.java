package com.cellumed.healthcare.microfit.Util;

import android.util.Log;

public class PacketUtil {

    private static String STX = "21";
    private static String ETX = "75";
    public static String ACK = "01";
    public static String[] NONE_DATA = {"00"};


    /**
     * parse data
     *
     * @param data
     * @return
     */
    public static byte[] parseData(byte[] data) {
        int start = -1;
        int index = 0;
        for (byte b : data) {
            if (b == (byte) Long.parseLong(STX, 16) && start == -1) {
                start = index;
            }
            index++;
        }
        byte[] parsedData = null;
        try {
            if (start > -1) {
                int len = data[start + 1];

                parsedData = new byte[len];
                int idx = 0;
                for (int i = start; i <= start + len - 1; i++) {
                    parsedData[idx] = data[i];
                    idx++;
                }
            } else {
                Log.e("parseData", "" + start);
            }
        } catch (Exception e) {
            Log.d("parseData", e.getMessage());
        }
        return parsedData;
    }

    public static String[] buildPacket(String command, String[] data) {
        String[] packet = new String[5 + data.length];
        packet[0] = STX;
        // 메세지 길이 : 전체 메세지 길이
        String len = Integer.toHexString(packet.length);
        if (len.length() < 2) {
            len = "0" + len;
        }
        packet[1] = len;
        packet[2] = command;

        String[] cmdData = new String[2 + data.length];
        cmdData[0] = len;
        cmdData[1] = command;

        for (int i = 0; i < data.length; i++) {
            cmdData[i + 2] = data[i];
            packet[i + 3] = data[i];
        }

        // 체크섬 계산 : STX 및 ETX를 제외한 모든 메세지를 합한 값
        String checkSum = Integer.toHexString((byte) checksum(cmdData) & 0xFF);
        checkSum = String.format("%02X", (byte) Integer.parseInt(checkSum, 16));
        packet[packet.length - 2] = checkSum;
        packet[packet.length - 1] = ETX;

        return packet;
    }

    public static String[] userParamBuildPacket(String command, String userId, int time, String[] data) {
        String[] packet = new String[5 + data.length + 3];
        packet[0] = STX;
        // 메세지 길이 : 전체 메세지 길이
        String len = Integer.toHexString(packet.length);
        if (len.length() < 2) {
            len = "0" + len;
        }

        String mUserId = Integer.toHexString((byte) (Integer.parseInt(userId) & 0xff));
        String firstTime = Integer.toHexString((byte) (time >> 8) & 0xff);
        String secondTime = Integer.toHexString((byte) time & 0xff);
        if (mUserId.length() < 2) {
            mUserId = "0" + mUserId;
        }
        if (firstTime.length() < 2) {
            firstTime = "0" + firstTime;
        }
        if (secondTime.length() < 2) {
            secondTime = "0" + secondTime;
        }
        packet[1] = len;
        packet[2] = command;
        packet[3] = mUserId;
        packet[4] = firstTime;

        packet[5] = secondTime;
        String[] cmdData = new String[2 + data.length + 3];
        cmdData[0] = len;
        cmdData[1] = command;
        cmdData[2] = mUserId;
        cmdData[3] = firstTime;
        cmdData[4] = secondTime;
        for (int i = 0; i < data.length; i++) {
            cmdData[i + 5] = data[i];
            packet[i + 6] = data[i];
        }

        // 체크섬 계산 : STX 및 ETX를 제외한 모든 메세지를 합한 값
        String checkSum = Integer.toHexString((byte) checksum(cmdData) & 0xFF);
        checkSum = String.format("%02X", (byte) Integer.parseInt(checkSum, 16));
        packet[packet.length - 2] = checkSum;
        packet[packet.length - 1] = ETX;

        return packet;
    }

    public static boolean isValidChecksum(String[] data) {
        String testChecksum = data[data.length - 2];
        String[] cmdData = new String[data.length - 3];
        for (int i = 1; i < data.length - 2; i++) {
            cmdData[i - 1] = data[i];
        }
        String checkSum = Integer.toHexString((byte) checksum(cmdData) & 0xFF);
        checkSum = String.format("%02X", (byte) Integer.parseInt(checkSum, 16));
        return testChecksum.equals(checkSum);
    }

    /**
     * byte array 를 문자열로 변환한다
     *
     * @param bytes
     * @return
     */
    public static String composeString(byte[] bytes) {
        final char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] hexChars = new char[bytes.length * 3];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 3] = hexArray[v >>> 4];
            hexChars[j * 3 + 1] = hexArray[v & 0x0F];
            hexChars[j * 3 + 2] = ' ';
        }
        return new String(hexChars);
    }

    /**
     * 입력된 16진수형식의 문자열의 체크섬 구한다
     *
     * @param data 데이터
     * @return
     */
    public static int checksum(String[] data) {
        int checkSum = 0;
        for (String b : data) {
            int db = Integer.parseInt(b, 16);
            checkSum += (0xff & db);
        }
        return checkSum;
    }

    /**
     * 16진수형식의 문자열을 byte array 로 변환 한다
     *
     * @param s 문자열
     * @return
     */
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * 문자열 배열을 byte 배열로 변환 한다
     *
     * @param str
     * @return
     */
    public static byte[] hexStringArrayToByteArray(String[] str) {
        StringBuilder builder = new StringBuilder();
        for (String s : str) {
            builder.append(s);
        }
        return hexStringToByteArray(builder.toString());
    }

}