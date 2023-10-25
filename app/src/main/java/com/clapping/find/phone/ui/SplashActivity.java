package com.clapping.find.phone.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.clapping.find.phone.R;
import com.clapping.find.phone.app.AdHelper;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        loadAds();
        openActivity();
    }

    private void loadAds() {
        AdHelper.loadSplash(this);
        AdHelper.loadAllInterstitial(this);
        AdHelper.loadNative(this);
    }

    private void openActivity() {

        new Handler().postDelayed(() -> {
            boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .getBoolean("isFirstRun", true);

            if (isFirstRun) {
                startActivity(new Intent(SplashActivity.this, IntroOneActivity.class));
            } else {
                gotoMainActivity();
            }

            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                    .putBoolean("isFirstRun", false).commit();


        }, 2000);


    }

    public void gotoMainActivity() {
        startActivity(new Intent(this, DashBoardActivity.class));
        finish();
    }
}