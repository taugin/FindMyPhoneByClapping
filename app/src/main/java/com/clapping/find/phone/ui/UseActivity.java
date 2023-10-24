package com.clapping.find.phone.ui;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.clapping.find.phone.app.AdHelper;
import com.clapping.find.phone.databinding.ActivityUseBinding;

public class UseActivity extends AppCompatActivity {
    ActivityUseBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AdHelper.loadAndShowNative(this, binding.nativeAdSmall, "small", "sn_use_small");
        AdHelper.loadAndShowNative(this, binding.nativeAdBig, "large", "sn_use_large");
        binding.view1.setBackground(new DashedLineDrawable(this));
        binding.view2.setBackground(new DashedLineDrawable(this));
        binding.view3.setBackground(new DashedLineDrawable(this));
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UseActivity.super.onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        UseActivity.super.onBackPressed();
    }
}