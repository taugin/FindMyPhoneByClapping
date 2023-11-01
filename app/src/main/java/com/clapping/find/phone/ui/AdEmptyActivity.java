package com.clapping.find.phone.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.clapping.find.phone.R;
import com.clapping.find.phone.app.AdHelper;
import com.clapping.find.phone.dialog.AdDialog;
import com.clapping.find.phone.remote.RCManager;
import com.hauyu.adsdk.Utils;

public class AdEmptyActivity extends Activity {
    private static Handler sHandler = new Handler(Looper.myLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final LinearLayout rootView = new LinearLayout(this);
        rootView.setGravity(Gravity.CENTER);
        setContentView(rootView);
        String maxPlace = AdHelper.getMaxInterstitial(this);
        if (!TextUtils.isEmpty(maxPlace)) {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(R.drawable.ic_loading);
            int size = Utils.px2dp(this, 360);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            rootView.addView(imageView, params);
            rootView.setBackgroundColor(Color.WHITE);
        }
        Intent intent = getIntent();
        String sceneName = null;
        if (intent != null) {
            sceneName = intent.getStringExtra(Intent.EXTRA_REFERRER_NAME);
        }
        AdHelper.showInterstitialCallback(this, sceneName, new Runnable() {
            @Override
            public void run() {
                finish();
                overridePendingTransition(0, 0);
            }
        });
        sHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                rootView.setBackgroundColor(Color.TRANSPARENT);
                rootView.setVisibility(View.INVISIBLE);
            }
        }, 1000);
    }

    public static void showInterstitialEmptyAfterLoading(Activity activity, final Intent intent, final String sceneName, final Runnable runnable) {
        if (RCManager.isShowAdLoading(activity)) {
            final AdDialog adDialog = new AdDialog(activity);
            adDialog.setCancelable(false);
            sHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (adDialog != null && adDialog.isShowing()) {
                        adDialog.dismiss();
                    }
                    try {
                        Intent adIntent = new Intent(activity, AdEmptyActivity.class);
                        adIntent.putExtra(Intent.EXTRA_REFERRER_NAME, sceneName);
                        activity.startActivities(new Intent[]{intent, adIntent});
                        activity.overridePendingTransition(0, 0);
                    } catch (Exception e) {
                        AdHelper.showInterstitialCallback(activity, sceneName, runnable);
                    }
                }
            }, 1000);
            adDialog.show();
        } else {
            try {
                Intent adIntent = new Intent(activity, AdEmptyActivity.class);
                adIntent.putExtra(Intent.EXTRA_REFERRER_NAME, sceneName);
                activity.startActivities(new Intent[]{intent, adIntent});
                activity.overridePendingTransition(0, 0);
            } catch (Exception e) {
                AdHelper.showInterstitialCallback(activity, sceneName, runnable);
            }
        }
    }
}
