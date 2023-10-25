package com.clapping.find.phone.ui;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.clapping.find.phone.app.AdHelper;
import com.clapping.find.phone.databinding.ActivityIntroOneBinding;
import com.clapping.find.phone.remote.RCManager;

public class IntroOneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityIntroOneBinding binding = ActivityIntroOneBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AdHelper.showBroccoli(binding.adIncludeLayout);
        AdHelper.loadAndShowNative(this, binding.nativeAd0, "tiny", "sn_intro_one");
        binding.txtNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (RCManager.isShowIntroInt(getApplicationContext())) {
                    AdHelper.showInterstitialCallback(getApplicationContext(), new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(getApplicationContext(), IntroTwoActivity.class));
                        }
                    });
                } else {
                    startActivity(new Intent(getApplicationContext(), IntroTwoActivity.class));
                }
            }
        });
    }
}