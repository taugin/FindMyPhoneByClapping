package com.clapping.find.phone.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.clapping.find.phone.R;
import com.clapping.find.phone.app.AdHelper;
import com.clapping.find.phone.remote.RCManager;
import com.hauyu.adsdk.AdSdk;
import com.hauyu.adsdk.SimpleAdSdkListener;
import com.hauyu.adsdk.Utils;

public class AdEmptyActivity extends BaseActivity {
    private static Handler sHandler = new Handler(Looper.myLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final LinearLayout rootView = new LinearLayout(this);
        rootView.setGravity(Gravity.CENTER);
        rootView.setBackgroundColor(Color.WHITE);
        setContentView(rootView);
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.ic_loading);
        int size = Utils.px2dp(this, 360);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
        rootView.addView(imageView, params);
        Intent intent = getIntent();
        String sceneName = null;
        if (intent != null) {
            sceneName = intent.getStringExtra(Intent.EXTRA_REFERRER_NAME);
        }
        Context context = getApplicationContext();
        String maxPlace = AdSdk.get(context).getMaxPlaceName(AdSdk.AD_TYPE_INTERSTITIAL);
        if (!TextUtils.isEmpty(maxPlace)) {
            AdSdk.get(context).setOnAdSdkListener(maxPlace, new SimpleAdSdkListener() {
                @Override
                public void onDismiss(String placeName, String source, String adType, String pid, boolean complexAds) {
                    AdSdk.get(context).setOnAdSdkListener(placeName, null, true);
                    if (!isFinishing()) {
                        finish();
                        overridePendingTransition(0, 0);
                    }
                }

                @Override
                public void onShowFailed(String placeName, String source, String adType, String pid, int error) {
                    AdSdk.get(context).setOnAdSdkListener(placeName, null, true);
                    if (!isFinishing()) {
                        finish();
                        overridePendingTransition(0, 0);
                    }
                }
            }, true);
            AdSdk.get(context).showInterstitial(maxPlace, sceneName);
            finish();
            overridePendingTransition(0, 0);
        } else {
            if (!isFinishing()) {
                finish();
                overridePendingTransition(0, 0);
            }
        }
    }

    public static void showInterstitialAfterLoading(Activity activity, final Intent intent, final String sceneName, final Runnable runnable) {
        if (RCManager.isShowAdLoading(activity)) {
            final AdDialog adDialog = new AdDialog(activity);
            adDialog.setCancelable(false);
            sHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (adDialog != null && adDialog.isShowing()) {
                        adDialog.dismiss();
                    }
                    if (intent != null) {
                        try {
                            Intent adIntent = new Intent(activity, AdEmptyActivity.class);
                            adIntent.putExtra(Intent.EXTRA_REFERRER_NAME, sceneName);
                            activity.startActivities(new Intent[]{intent, adIntent});
                            activity.overridePendingTransition(0, 0);
                        } catch (Exception e) {
                            AdHelper.showInterstitialCallback(activity, sceneName, runnable);
                        }
                    } else {
                        AdHelper.showInterstitialCallback(activity, sceneName, runnable);
                    }
                }
            }, 1000);
            adDialog.show();
        } else {
            if (intent != null) {
                try {
                    Intent adIntent = new Intent(activity, AdEmptyActivity.class);
                    adIntent.putExtra(Intent.EXTRA_REFERRER_NAME, sceneName);
                    activity.startActivities(new Intent[]{intent, adIntent});
                    activity.overridePendingTransition(0, 0);
                } catch (Exception e) {
                    AdHelper.showInterstitialCallback(activity, sceneName, runnable);
                }
            } else {
                AdHelper.showInterstitialCallback(activity, sceneName, runnable);
            }
        }
    }

    public static class AdDialog extends Dialog {
        private static final Handler sHandler = new Handler(Looper.myLooper());

        public AdDialog(Context context) {
            super(context);
        }

        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.dialog_ad_loading);
            updateWindow();
        }

        private void updateWindow() {
            try {
                WindowManager.LayoutParams p = getWindow().getAttributes();
                p.dimAmount = 0.8f;
                p.gravity = Gravity.CENTER;
                getWindow().setAttributes(p);
                getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            } catch (Exception e) {
            }
        }
    }
}
