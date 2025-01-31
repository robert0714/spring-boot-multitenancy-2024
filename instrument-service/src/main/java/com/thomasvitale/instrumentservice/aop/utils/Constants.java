package com.thomasvitale.instrumentservice.aop.utils;

import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static final String UTF8 = "UTF-8";
    public static final String HTTP = "http://";
    public static final String HTTPS = "https://";
    public static final String SUCCESS = "000000";
    public static final String SUCCESS_MSG = "Success";
    public static final String FAIL = "2000000";
    public static final String LOGIN_SUCCESS_STATUS = "0";
    public static final String LOGIN_FAIL_STATUS = "1";
    public static final String LOGIN_SUCCESS = "Success";
    public static final String LOGOUT = "Logout";
    public static final String REGISTER = "Register";
    public static final String LOGIN_FAIL = "Error";
    public static final String LOGO = "logo";
    public static final String AVATAR = "avatar";
    public static final Map<String, String> transportMap = new HashMap<>();

    static {
        transportMap.put("t-long-haul", "長途 > 4000 公里");
        transportMap.put("t-medium-haul", "中程 1500 至 4000 公里");
        transportMap.put("t-short-haul", "短程 800 至 1500 公里");
        transportMap.put("t-very-short-haul", "極短程 < 800km");
        transportMap.put("t-unspecified", "無區分距離");
        transportMap.put("t-mode-air", "空運");
        transportMap.put("t-mode-sea", "海運");
        transportMap.put("t-mode-land", "陸運");
        transportMap.put("t-mode-train", "軌道");
        transportMap.put("t-barge-inland-waterways", "內河航道、駁船");
        transportMap.put("t-barge-tanker-inland-waterways", "內河航道、駁船-儲存槽");
        transportMap.put("t-ferry", "渡輪");
        transportMap.put("t-bulk-carrier-ocean", "散貨船海運");
        transportMap.put("t-container-ship-ocean", "集裝箱船海運");
        transportMap.put("t-barge-with-cooling-inland-waterways", "內河航道、駁船-冷藏");
        transportMap.put("t-barge-with-freezing-inland-waterways", "內河航道、駁船-冷凍");
        transportMap.put("t-container-ship-with-cooling", "集裝箱船海運-冷藏");
        transportMap.put("t-container-ship-with-freezing", "集裝箱船海運-冷凍");
        transportMap.put("t-lorry-32-metric-ton", "貨車 >32噸");
        transportMap.put("t-lorry-16-32-metric-ton", "貨車 16-32噸");
        transportMap.put("t-lorry-7.5-16-metric-ton", "貨車 7.5-16噸");
        transportMap.put("t-lorry-3.5-7.5-metric-ton", "貨車 3.5-7.5噸");
        transportMap.put("t-light-commercial-vehicle", "輕型商用車");
        transportMap.put("t-lorry-unspecified", "無區分");
        transportMap.put("t-lorry-with-cooling-7.5-16-ton", "貨車 7.5-16噸-冷藏");
        transportMap.put("t-lorry-with-freezing-7.5-16-ton", "貨車 7.5-16噸-冷凍");
        transportMap.put("t-lorry-with-cooling-3.5-7.5-ton", "貨車 3.5-7.5噸-冷藏");
        transportMap.put("t-lorry-with-freezing-3.5-7.5-ton", "貨車 3.5-7.5噸-冷凍");
        transportMap.put("t-small-lorry-with-cooling", "小型貨車-冷藏");
        transportMap.put("t-small-lorry-with-freezing", "小型貨車-冷凍");
        transportMap.put("t-lorry-with-cooling", "貨車-冷藏");
        transportMap.put("t-lorry-with-freezing", "貨車-冷凍");
        transportMap.put("t-train-diesel", "火車-柴油");
        transportMap.put("t-train-electricity", "火車-電力");
        transportMap.put("t-train-steam", "火車-蒸氣");
        transportMap.put("t-train-unspecified", "無區分");
        transportMap.put("t-train-with-cooling", "火車-冷藏");
        transportMap.put("t-train-with-freezing", "火車-冷凍");
    }
}