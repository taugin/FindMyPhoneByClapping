package com.clapping.find.phone.app;

import android.app.Application;
import android.content.Context;

import com.clapping.find.phone.stat.Stat;
import com.hauyu.adsdk.InternalStat;
import com.moon.BcSdk;
import com.moon.listener.OnDataListener;

import java.util.Map;

public class MyApplication extends Application {

    private static MyApplication app;

    public static synchronized MyApplication getInstance() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        initSdk();
    }

    private void initSdk() {
        AdHelper.init(this);
        Stat.init(this);
        BcSdk.init(this, new OnDataListener() {
            @Override
            public void onReferrerResult(String status, String mediaSource, boolean fromClick) {
                Stat.init(getApplicationContext());
            }

            @Override
            public void onReportEvent(Context context, String s, String s1, Map<String, Object> map) {
                InternalStat.reportEvent(context, s, s1, map);
            }
        });
    }
}

