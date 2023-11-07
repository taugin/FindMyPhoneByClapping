package com.clapping.find.phone.remote;


import android.content.Context;
import android.text.TextUtils;

import com.clapping.find.phone.BuildConfig;
import com.clapping.find.phone.app.ClapApp;
import com.clapping.find.phone.log.Log;
import com.hauyu.adsdk.AdSdk;
import com.hauyu.adsdk.Utils;
import com.moon.BcSdk;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Singleton to hold the GTM Container (since it should be only created once
 * per run of the app).
 */
public class RCManager {
    private final static String TAG = RCManager.class.getSimpleName();
    private static final Random sRandom = new Random(System.currentTimeMillis());

    private RCManager() {
    }

    private static long getLongValue(String key) {
        return getFirebaseLongValue(key, 0);
    }

    private static long getLongValue(String key, long defaultValue) {
        return getFirebaseLongValue(key, defaultValue);
    }

    private static String getStringValue(String key) {
        return getFirebaseStringValue(key);
    }

    private static boolean getBooleanValue(String key) {
        return getFirebaseBooleanValue(key, false);
    }

    private static boolean getBooleanValue(String key, boolean def) {
        return getFirebaseBooleanValue(key, def);
    }

    private static String getFirebaseStringValue(String key) {
        String result = "";
        try {
            result = AdSdk.get(ClapApp.getInstance()).getString(key);
        } catch (Exception ignored) {
        }
        Log.d(TAG, "get [" + key + "] value: " + result);
        return result;
    }

    private static boolean getFirebaseBooleanValue(String key, boolean defaultValue) {
        boolean result = defaultValue;
        try {
            String str = AdSdk.get(ClapApp.getInstance()).getString(key);
            if (!TextUtils.isEmpty(str)) {
                result = Boolean.parseBoolean(str);
            }
        } catch (Exception e) {
            Log.e(Log.TAG, "error : " + e);
        }

        Log.d(TAG, "get [" + key + "] value: " + result);
        return result;
    }

    private static long getFirebaseLongValue(String key, long defaultValue) {
        long result = 0;

        try {
            String str = AdSdk.get(ClapApp.getInstance()).getString(key);
            if (!TextUtils.isEmpty(str)) {
                result = Long.parseLong(str);
            } else {
                result = defaultValue;
            }
        } catch (Exception ignored) {
            result = defaultValue;
        }

        Log.d(TAG, "get [" + key + "] value: " + result);
        return result;
    }

    public static boolean isAttr(Context context, String remoteConfigKey) {
        return isAttr(context, remoteConfigKey, null);
    }

    public static boolean isAttr(Context context, String remoteConfigKey, String defaultValue) {
        String configAttrString = getStringValue(remoteConfigKey);
        if (TextUtils.isEmpty(configAttrString)) {
            configAttrString = !TextUtils.isEmpty(defaultValue) ? defaultValue : "fc_true";
        }
        if (BuildConfig.DEBUG && false) {
            configAttrString = "non-organic,organic,fc_true,fc_false";
            Log.iv(Log.TAG, "**********debug attribution contain all**********");
        }
        List<String> configAttrList = null;
        if (configAttrString != null) {
            String splits[] = configAttrString.split(",");
            if (splits != null && splits.length > 0) {
                configAttrList = Arrays.asList(splits);
            }
        }
        String fromClick = BcSdk.isFromClick(context) ? "fc_true" : "fc_false";
        String attr = BcSdk.getAttribution(context);
        if (attr != null) {
            attr = attr.toLowerCase(Locale.getDefault());
        } else {
            attr = "fc_false";
        }
        if (configAttrList == null || configAttrList.isEmpty()) {
            return true;
        }
        return (configAttrList.contains(attr) || configAttrList.contains(fromClick)) && isNormalUser(context);
    }

    private static boolean isNormalUser(Context context) {
        if (BuildConfig.DEBUG) {
            return true;
        }
        String configAttrString = getStringValue("adb_debug_status");
        if (TextUtils.isEmpty(configAttrString)) {
            configAttrString = "adb";
        }
        if (TextUtils.equals(configAttrString, "debug")) {
            return !Utils.isDebugEnabled(context);
        }
        if (TextUtils.equals(configAttrString, "adb")) {
            return !Utils.isAdbUsbEnabled(context);
        }
        return true;
    }

    public static boolean isGclicUser(Context context) {
        return isAttr(context, "ufa");
    }

    public static boolean isAdUser(Context context) {
        return isAttr(context, "sponsored_user");
    }

    public static boolean isReloadInterstitialOnDismiss(Context context) {
        return isGclicUser(context) && getBooleanValue("reload_int_on_dismiss", true);
    }

    public static boolean isShowSplashFromBackground(Context context) {
        return isGclicUser(context) && getBooleanValue("show_splash_from_background", true);
    }

    public static boolean isShowExitNativeAds(Context context) {
        return isAdUser(context) && getBooleanValue("show_ena", true);
    }

    public static boolean isShowSlaveNative(Context context) {
        return isAdUser(context) && getBooleanValue("show_sna", true);
    }

    public static long getScanDurationWithSplashAds() {
        return getLongValue("with_splash_ads_scan_duration", 12000);
    }

    public static boolean isShowIntSplash(Context context) {
        return isGclicUser(context) && getBooleanValue("show_int_splash", true);
    }

    /**
     * 非自然量，全部展示开屏广告，自然量从launcher启动展示开屏广告
     *
     * @param context
     * @return
     */
    public static boolean isShowOpenSplash(Context context) {
        return isGclicUser(context) && getBooleanValue("show_open_splash", true);
    }

    public static boolean isShowIntroInt(Context context) {
        return isAdUser(context) && getBooleanValue("show_intro_int", true);
    }

    public static boolean isShowAdLoading(Context context) {
        return getBooleanValue("show_ad_loading", true);
    }

    public static boolean isShowLanguageNativeAds(Context context) {
        return isAdUser(context) && getBooleanValue("show_lna", true);
    }

    public static boolean isShowBottomNativeAds(Context context) {
        return isAdUser(context) && getBooleanValue("show_bna", true);
    }

    public static boolean isShowSettingNativeAds(Context context) {
        return isAttr(context, "user_settings") && getBooleanValue("show_sna", true);
    }
}