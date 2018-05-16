package com.perfect.library.utils.system;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * Created by Canghaixiao.
 * Time : 2017/11/17 11:25.
 * Function :设备信息工具类
 */
public class SystemUtils {

    private static final String PREFS_FILE = "device_id.xml";
    private static final String PREFS_DEVICE_ID = "device_id";

    @SuppressLint("HardwareIds")
    public static String getDeviceId(Context context) {
        synchronized (SystemUtils.class) {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
            String id = prefs.getString(PREFS_DEVICE_ID, null);
            if (id != null) {
                return id;
            } else {
                UUID uuid;
                String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                try {
                    if (!"9774d56d682e549c".equals(androidId)) {
                        uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
                    } else {
                        String deviceId;
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                            deviceId = "_";
                        } else {
                            TelephonyManager telephonyManager = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));
                            if (telephonyManager != null) {
                                deviceId = telephonyManager.getDeviceId();
                            } else {
                                deviceId = "-";
                            }
                        }
                        uuid = deviceId != null ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")) : UUID.randomUUID();
                    }
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                prefs.edit().putString(PREFS_DEVICE_ID, uuid.toString()).apply();
                return uuid.toString();
            }
        }
    }

    /**
     * 判断GPS是否打开
     */
    public static boolean isGPSEnabled(Context context) {
        boolean isEnable = false;
        LocationManager locationManager = ((LocationManager) context.getSystemService(Context.LOCATION_SERVICE));
        if (locationManager != null) {
            isEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        return isEnable;
    }

}
