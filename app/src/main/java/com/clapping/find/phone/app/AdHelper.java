package com.clapping.find.phone.app;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import com.clapping.find.phone.dialog.AdDialog;
import com.clapping.find.phone.remote.RCManager;
import com.clapping.find.phone.ui.DashBoardActivity;
import com.hauyu.adsdk.AdParams;
import com.hauyu.adsdk.AdSdk;
import com.hauyu.adsdk.OnAdEventListener;
import com.hauyu.adsdk.OnAdSdkListener;
import com.hauyu.adsdk.SimpleAdSdkListener;

import java.util.Arrays;
import java.util.List;

import me.samlss.broccoli.Broccoli;
import me.samlss.broccoli.BroccoliGradientDrawable;
import me.samlss.broccoli.PlaceholderParameter;

public class AdHelper {
    public static final String PID_SPLASH_COMMON = "common_splash";
    public static final String PID_INT_COMMON = "common_int";
    private static final String PID_INT_COMMON_SLAVE = "common_int_slave";
    private static final String PID_NATIVE_COMMON = "common_native";
    private static final String PID_NATIVE_COMMON_SLAVE = "common_native_slave";

    private static final Handler sHandler = new Handler(Looper.getMainLooper());

    public static void init(Context context) {
        AdSdk.get(context).setOnAdEventListener(new OnAdEventListener() {
            @Override
            public void onDismiss(String placeName, String sdkName, String adType, String pid) {
                if (TextUtils.equals(adType, AdSdk.AD_TYPE_INTERSTITIAL) && RCManager.isReloadInterstitialOnDismiss(context)) {
                    AdHelper.loadAllInterstitial(context);
                }
                if (TextUtils.equals(adType, AdSdk.AD_TYPE_SPLASH) && RCManager.isShowSplashFromBackground(context)) {
                    AdHelper.loadSplash(context);
                }
            }
        });
        AdSdk.get(context).init();
    }

    public static void setOnSplashAdListener(Context context, String placeName, OnAdSdkListener l) {
        AdSdk.get(context).setOnAdSdkListener(placeName, l, true);
    }

    public static void setOnIntAdListener(Context context, String placeName, OnAdSdkListener l) {
        AdSdk.get(context).setOnAdSdkListener(placeName, l, true);
    }

    public static void loadSplash(Context context) {
        AdSdk.get(context).loadSplash(PID_SPLASH_COMMON);
    }

    public static boolean isSplashLoaded(Context context) {
        return AdSdk.get(context).isSplashLoaded(PID_SPLASH_COMMON);
    }

    public static String getMaxSplash(Context context) {
        return AdSdk.get(context).getMaxPlaceName(AdSdk.AD_TYPE_SPLASH);
    }

    public static void showSplash(Context context, String sceneName) {
        AdSdk.get(context).showSplash(PID_SPLASH_COMMON, null, sceneName);
    }

    public static void loadAllInterstitial(Context context) {
        loadInterstitial(context);
        loadInterstitialSlave(context);
    }

    public static void loadInterstitial(Context context) {
        AdSdk.get(context).loadInterstitial(PID_INT_COMMON);
    }

    public static void loadInterstitialSlave(Context context) {
        AdSdk.get(context).loadInterstitial(PID_INT_COMMON_SLAVE);
    }

    public static boolean isInterstitialLoaded(Context context, String placeName) {
        return AdSdk.get(context).isInterstitialLoaded(placeName);
    }

    public static boolean isInterstitialLoaded(Context context) {
        String maxInterstitial = getMaxInterstitial(context);
        return !TextUtils.isEmpty(maxInterstitial);
    }

    public static String getMaxInterstitial(Context context) {
        return AdSdk.get(context).getMaxPlaceName(AdSdk.AD_TYPE_INTERSTITIAL);
    }

    public static void showInterstitial(Context context, String sceneName) {
        String maxInterstitial = getMaxInterstitial(context);
        if (!TextUtils.isEmpty(maxInterstitial)) {
            AdSdk.get(context).showInterstitial(maxInterstitial, sceneName);
        }
    }

    public static void showInterstitial(Context context, String placeName, String sceneName) {
        AdSdk.get(context).showInterstitial(placeName, sceneName);
    }

    public static void loadNative(Context context) {
        AdSdk.get(context).loadAdView(PID_NATIVE_COMMON);
        AdSdk.get(context).loadAdView(PID_NATIVE_COMMON_SLAVE);
    }

    public static boolean isNativeLoaded(Context context) {
        return AdSdk.get(context).isAdViewLoaded(PID_NATIVE_COMMON);
    }

    public static void loadAndShowNativeSlave(Context context, ViewGroup viewGroup, String cardStyle, String sceneName) {
        AdSdk.get(context).loadAdView(PID_NATIVE_COMMON_SLAVE, new SimpleAdSdkListener() {
            @Override
            public void onLoaded(String placeName, String source, String adType, String pid) {
                if (!(context instanceof Activity) || !((Activity) context).isFinishing()) {
                    AdParams adParams = new AdParams.Builder().setAdCardStyle(cardStyle).setSceneName(sceneName).build();
                    AdSdk.get(context).showAdView(placeName, adParams, viewGroup);
                }
            }

            @Override
            public void onImp(String placeName, String source, String adType, String network, String pid) {
                if (!TextUtils.equals(sceneName, "sn_exit_dialog")) {
                    AdSdk.get(context).loadAdView(placeName);
                }
            }
        });
    }

    public static void loadAndShowNative(Context context, ViewGroup viewGroup, String cardStyle, String sceneName) {
        String maxPlace = AdSdk.get(context).getMaxPlaceName(AdSdk.AD_TYPE_NATIVE);
        if (!TextUtils.isEmpty(maxPlace)) {
            if (!(context instanceof Activity) || !((Activity) context).isFinishing()) {
                AdParams adParams = new AdParams.Builder().setAdCardStyle(cardStyle).setSceneName(sceneName).build();
                AdSdk.get(context).showAdView(maxPlace, adParams, viewGroup);
            }
            return;
        }
        AdSdk.get(context).loadAdView(PID_NATIVE_COMMON, new SimpleAdSdkListener() {
            @Override
            public void onLoaded(String placeName, String source, String adType, String pid) {
                if (!(context instanceof Activity) || !((Activity) context).isFinishing()) {
                    AdParams adParams = new AdParams.Builder().setAdCardStyle(cardStyle).setSceneName(sceneName).build();
                    AdSdk.get(context).showAdView(placeName, adParams, viewGroup);
                }
            }

            @Override
            public void onImp(String placeName, String source, String adType, String network, String pid) {
                if (!TextUtils.equals(sceneName, "sn_exit_dialog")) {
                    AdSdk.get(context).loadAdView(placeName);
                }
            }
        });
    }

    public static boolean isShowSplashOnActivity(Activity activity) {
        if (RCManager.isShowSplashFromBackground(activity)) {
            List<Class<?>> classList = Arrays.asList(DashBoardActivity.class);
            try {
                for (Class<?> clazz : classList) {
                    if (TextUtils.equals(clazz.getName(), activity.getClass().getName())) {
                        return true;
                    }
                }
            } catch (Exception e) {
            }
        }
        return false;
    }

    public static void showBroccoli(View adLayoutView) {
        try {
            Broccoli broccoli = new Broccoli();
            broccoli.addPlaceholders(adLayoutView.findViewById(com.android.widget.R.id.rab_native_icon)
                    , adLayoutView.findViewById(com.android.widget.R.id.rab_native_title)
                    , adLayoutView.findViewById(com.android.widget.R.id.rab_native_detail)
                    , adLayoutView.findViewById(com.android.widget.R.id.rab_native_action_btn));

            broccoli.addPlaceholder(new PlaceholderParameter.Builder()
                    .setDrawable(new BroccoliGradientDrawable(Color.parseColor("#DDDDDD"),
                            Color.parseColor("#CCCCCC"), 0, 1000, new LinearInterpolator()))
                    .setView(adLayoutView.findViewById(com.android.widget.R.id.rab_native_cover_info))
                    .build());
            broccoli.show();
        } catch (Exception e) {
        }
    }

    public static String getString(Context context, String key) {
        return AdSdk.get(context).getString(key);
    }

    public static void showInterstitialCallback(Context context, String sceneName, Runnable runnable) {
        String maxPlace = getMaxInterstitial(context);
        if (!TextUtils.isEmpty(maxPlace)) {
            AdSdk.get(context).setOnAdSdkListener(maxPlace, new SimpleAdSdkListener() {
                @Override
                public void onDismiss(String placeName, String source, String adType, String pid, boolean complexAds) {
                    AdSdk.get(context).setOnAdSdkListener(placeName, null, true);
                    sHandler.post(runnable);
                }

                @Override
                public void onShowFailed(String placeName, String source, String adType, String pid, int error) {
                    AdSdk.get(context).setOnAdSdkListener(placeName, null, true);
                    sHandler.post(runnable);
                }
            }, true);
            sHandler.post(new Runnable() {
                @Override
                public void run() {
                    AdSdk.get(context).showInterstitial(maxPlace, sceneName);
                }
            });
        } else {
            sHandler.post(runnable);
        }
    }

    public static void showSplashCallback(Context context, String sceneName, Runnable runnable) {
        String maxPlace = getMaxSplash(context);
        if (!TextUtils.isEmpty(maxPlace)) {
            AdSdk.get(context).setOnAdSdkListener(maxPlace, new SimpleAdSdkListener() {
                @Override
                public void onDismiss(String placeName, String source, String adType, String pid, boolean complexAds) {
                    AdSdk.get(context).setOnAdSdkListener(placeName, null, true);
                    sHandler.post(runnable);
                }

                @Override
                public void onShowFailed(String placeName, String source, String adType, String pid, int error) {
                    AdSdk.get(context).setOnAdSdkListener(placeName, null, true);
                    sHandler.post(runnable);
                }
            }, true);
            sHandler.post(new Runnable() {
                @Override
                public void run() {
                    AdSdk.get(context).showSplash(maxPlace, null, sceneName);
                }
            });
        } else {
            sHandler.post(runnable);
        }
    }


    public static void showInterstitialAfterLoading(Activity activity, final String sceneName, final Runnable runnable) {
        if (RCManager.isShowAdLoading(activity)) {
            final AdDialog adDialog = new AdDialog(activity);
            adDialog.setCancelable(false);
            sHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    AdHelper.showInterstitialCallback(activity, sceneName, runnable);
                    if (adDialog != null && adDialog.isShowing()) {
                        adDialog.dismiss();
                    }
                }
            }, 1000);
            adDialog.show();
        } else {
            AdHelper.showInterstitialCallback(activity, sceneName, runnable);
        }
    }
}
