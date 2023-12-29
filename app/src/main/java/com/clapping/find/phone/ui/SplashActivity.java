package com.clapping.find.phone.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatCheckBox;

import com.clapping.find.phone.BuildConfig;
import com.clapping.find.phone.R;
import com.clapping.find.phone.app.AdHelper;
import com.clapping.find.phone.remote.RCManager;
import com.pumob.adsdk.AdSdk;

public class SplashActivity extends BaseActivity {

    private boolean mShowIntSplash = false;

    private View mRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initView();
        mRootView = findViewById(R.id.root_layout);
    }

    private void initView() {
        loadAds();
        boolean policyAgreed = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isPolicyAgreed", false);
        if (policyAgreed) {
            initSplashView();
        } else {
            initStartView();
        }
    }

    private void initStartView() {
        findViewById(R.id.privacy_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.loading_layout).setVisibility(View.GONE);
        TextView textView = findViewById(R.id.ad_notice);
        SpannableString content = new SpannableString(getString(R.string.privacy_policy));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textView.setText(content);
        textView.setOnClickListener(v -> {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.PRIVACY_URL));
                        startActivity(intent);
                    } catch (Exception e) {
                    }
                }
        );
        findViewById(R.id.open_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("isPolicyAgreed", true).commit();
                loadAds();
                initSplashView();
            }
        });
        AppCompatCheckBox cbx = findViewById(R.id.checkBox);
        cbx.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                findViewById(R.id.open_btn).setEnabled(true);
            } else {
                findViewById(R.id.open_btn).setEnabled(false);
            }
        });
        cbx.setChecked(true);
    }

    private void loadAds() {
        AdHelper.loadSplash(this);
        AdHelper.loadAllInterstitial(this);
        AdHelper.loadNative(this);
    }

    private void initSplashView() {
        findViewById(R.id.privacy_layout).setVisibility(View.GONE);
        findViewById(R.id.loading_layout).setVisibility(View.VISIBLE);
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