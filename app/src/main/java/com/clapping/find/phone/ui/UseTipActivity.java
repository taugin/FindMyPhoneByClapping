package com.clapping.find.phone.ui;

import android.os.Bundle;
import android.view.View;

import com.clapping.find.phone.app.AdHelper;
import com.clapping.find.phone.databinding.ActivityUseBinding;
import com.clapping.find.phone.remote.RCManager;

public class UseTipActivity extends BaseActivity {
    ActivityUseBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (RCManager.isAdUser(this)) {
            binding.nativeAdTiny.setVisibility(View.VISIBLE);
            AdHelper.showBroccoli(binding.adIncludeLayout);
            AdHelper.loadAndShowNative(this, binding.nativeAdTiny, "tiny", "sn_use_tiny");
        } else {
            binding.nativeAdTiny.setVisibility(View.GONE);
        }
        binding.view1.setBackground(new DashedLineDrawable(this));
        binding.view2.setBackground(new DashedLineDrawable(this));
        binding.view3.setBackground(new DashedLineDrawable(this));
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (RCManager.isAdUser(this)) {
            AdHelper.showInterstitialCallback(getApplicationContext(), "si_back_use", new Runnable() {
                @Override
                public void run() {
                    try {
                        UseTipActivity.super.onBackPressed();
                    } catch (Exception e) {
                    }
                }
            });
        } else {
            UseTipActivity.super.onBackPressed();
        }
    }
}