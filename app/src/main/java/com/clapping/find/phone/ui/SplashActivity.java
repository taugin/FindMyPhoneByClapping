package com.clapping.find.phone.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;

import com.clapping.find.phone.R;
import com.clapping.find.phone.app.AdHelper;
import com.clapping.find.phone.remote.RCManager;
import com.hauyu.adsdk.AdSdk;

public class SplashActivity extends BaseActivity {

    private boolean mShowIntSplash = false;

    private View mRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mRootView = findViewById(R.id.root_layout);
        loadAds();
        initSplashView();
    }

    private void loadAds() {
        AdHelper.loadSplash(this);
        AdHelper.loadAllInterstitial(this);
        AdHelper.loadNative(this);
    }

    private void initSplashView() {
        mShowIntSplash = RCManager.isShowIntSplash(this);
        long totalDuration = RCManager.getScanDurationWithSplashAds();
        CountDownTimer countDownTimer = new CountDownTimer(totalDuration, 2000) {
            @Override
            public void onTick(long millisUntilFinished) {
                boolean loaded = checkAdLoaded();
                if (millisUntilFinished < (totalDuration - 2000 * 2) && loaded) {
                    cancel();
                    showOpenSplash();
                }
            }

            @Override
            public void onFinish() {
                showOpenSplash();
            }
        };
        countDownTimer.start();
    }

    private void showOpenSplash() {
        String maxSplash = AdHelper.getMaxSplash(this);
        String maxInterstitial = AdHelper.getMaxInterstitial(this);
        if (mShowIntSplash && !TextUtils.isEmpty(maxSplash) && !TextUtils.isEmpty(maxInterstitial)) {
            double splashRevenue = AdSdk.get(this).getMaxRevenue(maxSplash);
            double intRevenue = AdSdk.get(this).getMaxRevenue(maxInterstitial);
            if (intRevenue > splashRevenue) {
                maxSplash = null;
            }
        }
        if (!TextUtils.isEmpty(maxSplash)) {
            AdHelper.showSplashCallback(this, "ss_open_splash", new Runnable() {
                @Override
                public void run() {
                    openActivity();
                }
            }, false);
            enableLoadingClickable();
        } else if (mShowIntSplash && !TextUtils.isEmpty(maxInterstitial)) {
            AdHelper.showInterstitialCallback(this, "si_open_splash", new Runnable() {
                @Override
                public void run() {
                    openActivity();
                }
            }, false);
            enableLoadingClickable();
        } else {
            openActivity();
        }
    }

    private void enableLoadingClickable() {
        if (mRootView != null) {
            mRootView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mRootView != null) {
                        mRootView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                openActivity();
                            }
                        });
                    }
                }
            }, 1000);
        }
    }

    private boolean checkAdLoaded() {
        String maxInt = AdHelper.getMaxInterstitial(this);
        String maxSplash = AdHelper.getMaxSplash(this);
        if (mShowIntSplash) {
            return !TextUtils.isEmpty(maxSplash) || !TextUtils.isEmpty(maxInt);
        }
        return !TextUtils.isEmpty(maxSplash);
    }

    private void openActivity() {
        boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true);
        if (isFirstRun) {
            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("isFirstRun", false).commit();
            startActivity(new Intent(SplashActivity.this, IntroOneActivity.class));
            finish();
            overridePendingTransition(0, 0);
        } else {
            gotoMainActivity();
        }
    }

    public void gotoMainActivity() {
        startActivity(new Intent(this, DashBoardActivity.class));
        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onBackPressed() {
    }
}