package com.clapping.find.phone.stat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import com.clapping.find.phone.BuildConfig;
import com.clapping.find.phone.log.Log;
import com.hauyu.adsdk.InternalStat;
import com.hauyu.adsdk.Utils;
import com.moon.BcSdk;
import com.tendcloud.tenddata.TalkingDataSDK;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.commonsdk.debug.I;

import java.util.Locale;
import java.util.Map;

public class Stat {

    public static void init(Context appContext) {
        initUmeng(appContext);
        initTalkingData(appContext);
    }

    private static void initUmeng(Context context) {
        String atStatus = BcSdk.getAttribution(context);
        if (!TextUtils.isEmpty(atStatus)) {
            atStatus = atStatus.replace("-", "");
            atStatus = atStatus.toLowerCase(Locale.ENGLISH);
            String appKey = BuildConfig.UMENG_APP_KEY;
            String channel = atStatus + "_" + getChannel(context);
            Log.iv(Log.TAG, "umeng init app key : " + appKey + " , channel : " + channel);
            AnalyticsConfig.turnOnRealTimeDebug(null);
            UMConfigure.setLogEnabled(BuildConfig.DEBUG);
            UMConfigure.init(context, appKey, channel, UMConfigure.DEVICE_TYPE_PHONE, null);
            // 场景类型设置接口
            MobclickAgent.setCatchUncaughtExceptions(true);
            MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.LEGACY_MANUAL);
//            if (RCManager.isGclicUser(context)) {
//                Sync.createSyncAccount(context);
//            }
        }
    }

    private static void initTalkingData(Context context) {
        String atStatus = BcSdk.getAttribution(context);
        if (!TextUtils.isEmpty(atStatus)) {
            atStatus = atStatus.replace("-", "");
            atStatus = atStatus.toLowerCase(Locale.ENGLISH);
            String appKey = BuildConfig.TALKING_DATA_APP_KEY;
            String channel = atStatus + "_" + getChannel(context);
            if (!BuildConfig.DEBUG) {
                TalkingDataSDK.setVerboseLogDisable();
            }
            TalkingDataSDK.setReportUncaughtExceptions(true);
            TalkingDataSDK.init(context, appKey, channel, "");
        }
    }

    private static String getChannel(Context context) {
        String channel = null;
        try {
            Locale locale = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                locale = context.getResources().getConfiguration().getLocales().get(0);
            } else {
                locale = context.getResources().getConfiguration().locale;
            }
            channel = locale.getCountry().toLowerCase(Locale.getDefault());
        } catch (Exception e) {
            channel = Utils.getMetaData(context, "UMENG_CHANNEL");
        }
        return channel;
    }

    public static void onResume(Activity activity) {
        try {
            MobclickAgent.onResume(activity);
            TalkingDataSDK.onPageBegin(activity, activity.getClass().getSimpleName());
        } catch (Exception e) {
        }
    }

    public static void onPause(Activity activity) {
        try {
            MobclickAgent.onPause(activity);
            TalkingDataSDK.onPageEnd(activity, activity.getClass().getSimpleName());
        } catch (Exception e) {
        }
    }

    public static void reportEvent(Context context, String event,
                                   String value, Map<String, Object> extra) {
        Log.iv(Log.TAG, "event id : " + event + " , value : " + value + " , extra : " + extra);
        InternalStat.reportEvent(context, event, value, extra);
    }

    public static void reportEvent(Context context, String event) {
        reportEvent(context, event, "", null);
    }

    public static void reportEvent(Context context, String event, String value) {
        reportEvent(context, event, value, null);
    }

    public static void reportEventOnce(Context context, String event) {
        if (!Utils.getBoolean(context, event + "_reported", false)) {
            Utils.putBoolean(context, event + "_reported", true);
            InternalStat.reportEvent(context, event, null, null);
        }
    }

    public static void openPrivacyPolicy(Context context) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.PRIVACY_URL));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
        }
    }
}
