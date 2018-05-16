package com.perfect.library.utils.system;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import com.perfect.library.utils.system.messageBean.AppInfoMessage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Canghaixiao.
 * Time : 2017/11/17 11:37.
 * Function :app信息工具类
 * 注意事项(apk安装)
 * 一、需要在manifest内application标签下植入以下代码
 * <provider
 * android:name="android.support.v4.content.FileProvider"
 * android:authorities="com.yanxiu.gphone.faceshowadmin_android.fileprovider"
 * android:exported="false"
 * android:grantUriPermissions="true">
 * <meta-data
 * android:name="android.support.FILE_PROVIDER_PATHS"
 * android:resource="@xml/file_paths" />
 * </provider>
 * 二、需要在把library的xml文件夹下file_paths文件剪切到主项目中相同位置
 */
public class AppUtils {

    /**
     * 获取包名
     */
    public static String getPackageName(Context context) {
        return context.getPackageName();
    }

    /**
     * 获取版本号
     */
    public static int getVersionCode(Context context) {
        int versionCode = -1;
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            versionCode = info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 获取版本名称
     */
    public static String getVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            versionName = info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 安装apk,普通安装
     */
    public static void installApk(Context context, String filePath) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String type = "application/vnd.android.package-archive";
        File file = new File(filePath);
        Uri uri;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            uri = Uri.fromFile(file);
        } else {
            uri = FileProvider.getUriForFile(context, getPackageName(context) + ".fileprovider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(uri, type);
        context.startActivity(intent);
    }

    /**
     * 卸载apk
     *
     * @param packageName 要卸载的目标app包名
     */
    public void unstallApp(Context context, String packageName) {
        Intent uninstall_intent = new Intent();
        uninstall_intent.setAction(Intent.ACTION_DELETE);
        uninstall_intent.setData(Uri.parse("package:" + packageName));
        context.startActivity(uninstall_intent);
    }

    /**
     * 方法描述:获取所有已安装App信息
     * AppInfoMessage（名称，图标，包名，版本号，版本Code，是否安装在SD卡，是否是用户程序)
     */
    public static List<AppInfoMessage> getAllAppsInfo(Context context) {
        List<AppInfoMessage> list = new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
        for (PackageInfo pi : installedPackages) {
            if (pi != null) {
                AppInfoMessage message = new AppInfoMessage();
                ApplicationInfo ai = pi.applicationInfo;
                message.name = ai.loadLabel(pm).toString();
                message.icon = ai.loadIcon(pm);
                message.packageName = pi.packageName;
                message.versionName = pi.versionName;
                message.versionCode = pi.versionCode;
                message.isSD = (ApplicationInfo.FLAG_SYSTEM & ai.flags) != ApplicationInfo.FLAG_SYSTEM;
                message.isUser = (ApplicationInfo.FLAG_SYSTEM & ai.flags) != ApplicationInfo.FLAG_SYSTEM;
                list.add(message);
            }
        }
        return list;
    }

    /**
     * 方法描述:根据包名判断App是否安装
     */
    public static boolean isInstallApp(Context context, String packageName) {
        return context.getPackageManager().getLaunchIntentForPackage(packageName) != null;
    }

    /**
     * 方法描述:打开指定包名的App
     */
    public static boolean openAppByPackageName(Context context, String packageName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent != null) {
            context.startActivity(intent);
            return true;
        }
        return false;
    }

    /**
     * 方法描述:打开指定包名的App应用信息界面
     */
    public static void openAppInfo(Context context, String packageName) {
        Intent intent = new Intent();
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.parse("package:" + packageName));
        context.startActivity(intent);
    }

    /**
     * App系统分享文字
     */
    public static void shareAppTxt(Context context, String info) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, info);
        context.startActivity(Intent.createChooser(intent, "share"));
    }

    /**
     * App系统分享图片
     */
    public static void shareAppImg(Context context, List<String> paths) {
        ArrayList<Uri> imageUris = new ArrayList<>();
        for (String path : paths) {
            Uri imageUri = Uri.fromFile(new File(path));
            imageUris.add(imageUri);
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
        intent.setType("image/*");
        context.startActivity(Intent.createChooser(intent, "share"));
    }

    /**
     * 判断应用前台后台
     */
    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.processName.equals(context.getPackageName())) {
                    return appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
                }
            }
        }
        return false;
    }
}
