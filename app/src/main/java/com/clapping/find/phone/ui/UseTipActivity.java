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
            if (RCManager.isShowSlaveNative(this)) {
                AdHelper.loadAndShowNativeSlave(this, binding.nativeAdSmall, "tiny", "sn_use_small");
            }
        } else {
            binding.nativeAdTiny.setVisibility(View.GONE);
        }
        binding.view1.setBackground(new DashedLineDrawable(this));
        binding.view2.setBackground(new DashedLineDrawable(this));
        binding.view3.setBackground(new DashedLineDrawable(this));
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UseTipActivity.super.onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        UseTipActivity.super.onBackPressed();
    }
}