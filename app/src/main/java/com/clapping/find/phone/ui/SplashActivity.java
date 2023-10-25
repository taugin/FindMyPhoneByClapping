package com.clapping.find.phone.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.clapping.find.phone.R;
import com.clapping.find.phone.app.AdHelper;
import com.clapping.find.phone.remote.RCManager;
import com.hauyu.adsdk.AdSdk;
import com.hauyu.adsdk.SimpleAdSdkListener;

public class SplashActivity extends AppCompatActivity {

    private boolean mShowOpenSplash = false;
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
        mShowOpenSplash = RCManager.isShowOpenSplash(this);
        mShowIntSplash = RCManager.isShowIntSplash(this);
        long totalDuration = RCManager.getScanDurationWithSplashAds();
        if (mShowOpenSplash) {
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
            AdHelper.setOnSplashAdListener(this, maxSplash, new SimpleAdSdkListener() {
                @Override
                public void onDismiss(String placeName, String source, String adType, String pid, boolean complexAds) {
                    AdHelper.setOnSplashAdListener(getApplicationContext(), placeName, null);
                    openActivity();
                }

                @Override
                public void onShowFailed(String placeName, String source, String adType, String pid, int error) {
                    AdHelper.setOnSplashAdListener(getApplicationContext(), placeName, null);
                    openActivity();
                }
            });
            AdHelper.showSplash(this, "ss_open_splash");
            enableLoadingClickable();
        } else if (mShowIntSplash && !TextUtils.isEmpty(maxInterstitial)) {
            AdHelper.setOnIntAdListener(this, maxInterstitial, new SimpleAdSdkListener() {
                @Override
                public void onDismiss(String placeName, String source, String adType, String pid, boolean complexAds) {
                    AdHelper.setOnIntAdListener(getApplicationContext(), placeName, null);
                    openActivity();
                }

                @Override
                public void onShowFailed(String placeName, String source, String adType, String pid, int error) {
                    AdHelper.setOnIntAdListener(getApplicationContext(), placeName, null);
                    openActivity();
                }
            });
            AdHelper.showInterstitial(this, maxInterstitial, "si_open_splash");
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
        return !TextUtils.isEmpty(maxSplash) || !TextUtils.isEmpty(maxInt);
    }

    private void openActivity() {
        new Handler().post(() -> {
            boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true);
            if (isFirstRun) {
                startActivity(new Intent(SplashActivity.this, IntroOneActivity.class));
            } else {
                gotoMainActivity();
            }
            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("isFirstRun", false).commit();
        });
    }

    public void gotoMainActivity() {
        startActivity(new Intent(this, DashBoardActivity.class));
        finish();
    }
}