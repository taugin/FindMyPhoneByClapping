package com.clapping.find.phone.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.clapping.find.phone.app.AdHelper;
import com.clapping.find.phone.databinding.ActivityIntroTwoBinding;
import com.clapping.find.phone.remote.RCManager;

public class IntroTwoActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityIntroTwoBinding binding = ActivityIntroTwoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (RCManager.isShowBottomNativeAds(this)) {
            binding.nativeAd0.setVisibility(View.VISIBLE);
            AdHelper.showBroccoli(binding.adIncludeLayout);
            AdHelper.loadAndShowNative(this, binding.nativeAd0, "tiny", "sn_intro_two");
        } else {
            binding.nativeAd0.setVisibility(View.GONE);
        }
        binding.txtNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (RCManager.isShowIntroInt(getApplicationContext())) {
                    Intent intent = new Intent(getApplicationContext(), TermsOfUseActivity.class);
                    AdHelper.showInterstitialAfterLoading(IntroTwoActivity.this, intent, "si_intro_two", new Runnable() {
                        @Override
                        public void run() {
                            startActivity(intent);
                            finish();
                        }
                    });
                } else {
                    startActivity(new Intent(getApplicationContext(), TermsOfUseActivity.class));
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
    }
}