package com.clapping.find.phone.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.clapping.find.phone.app.AdHelper;
import com.clapping.find.phone.databinding.ActivityIntroTwoBinding;
import com.clapping.find.phone.remote.RCManager;

public class IntroTwoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityIntroTwoBinding binding = ActivityIntroTwoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AdHelper.showBroccoli(binding.adIncludeLayout);
        AdHelper.loadAndShowNative(this, binding.nativeAd0, "tiny", "sn_intro_two");
        binding.txtNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (RCManager.isShowIntroInt(getApplicationContext())) {
                    AdHelper.showInterstitialAfterLoading(IntroTwoActivity.this, "si_intro_two", new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(getApplicationContext(), TermsOfUseActivity.class));
                        }
                    });
                } else {
                    startActivity(new Intent(getApplicationContext(), TermsOfUseActivity.class));
                }
            }
        });
    }
}