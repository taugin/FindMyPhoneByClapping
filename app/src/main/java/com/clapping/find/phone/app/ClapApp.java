package com.clapping.find.phone.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.clapping.find.phone.stat.Stat;
import com.clapping.find.phone.utils.ChangeLanguage;
import com.pumob.adsdk.InternalStat;
import com.pumob.adsdk.core.framework.ActivityMonitor;
import com.pumob.bcsdk.BcSdk;
import com.pumob.bcsdk.OnDataListener;

import java.lang.ref.WeakReference;
import java.util.Map;

public class ClapApp extends Application {

    private static ClapApp app;

    public static synchronized ClapApp getInstance() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        ChangeLanguage.init(this);
        initSdk();
        registerLifeCycle();
    }

    private void initSdk() {
        AdHelper.init(this);
        Stat.init(this);
        BcSdk.init(this, new OnDataListener() {
            @Override
            public void onReferrerResult(String status, String mediaSource, boolean fromClick) {
                Stat.init(getApplicationContext());
            }
        });
        Stat.initOneSignal(this);
    }

    private void registerLifeCycle() {
        try {
            registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                    if (AdHelper.isShowSplashOnActivity(activity)) {
                        AdHelper.loadSplash(activity);
                    }
                }

                @Override
                public void onActivityStarted(Activity activity) {
                }

                @Override
                public void onActivityResumed(Activity activity) {
                    Stat.onResume(activity);
                }

                @Override
                public void onActivityPaused(Activity activity) {
                    Stat.onPause(activity);
                }

                @Override
                public void onActivityStopped(Activity activity) {
                }

                @Override
                public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                }

                @Override
                public void onActivityDestroyed(Activity activity) {
                }
            });
        } catch (Exception e) {
        }
        ActivityMonitor.get(this).addOnAppMonitorCallback(new ActivityMonitor.OnAppMonitorCallback() {
            @Override
            public void onForeground(boolean fromBackground, WeakReference<Activity> activityWeakReference) {
                if (fromBackground) {
                    if (activityWeakReference != null) {
                        Activity activity = activityWeakReference.get();
                        if (activity != null && !activity.isFinishing() && AdHelper.isShowSplashOnActivity(activity)) {
                            AdHelper.showSplash(activity, "ss_back_to_front");
                        }
                    }
                }
            }
        });
    }
}

