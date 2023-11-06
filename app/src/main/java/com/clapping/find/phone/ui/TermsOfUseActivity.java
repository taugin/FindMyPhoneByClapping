package com.clapping.find.phone.ui;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.clapping.find.phone.app.AdHelper;
import com.clapping.find.phone.databinding.ActivityTermsOfUseBinding;
import com.clapping.find.phone.remote.RCManager;
import com.clapping.find.phone.stat.Stat;

public class TermsOfUseActivity extends BaseActivity {
    ActivityTermsOfUseBinding binding;
    int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTermsOfUseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (RCManager.isAdUser(this)) {
            binding.nativeAd0.setVisibility(View.VISIBLE);
            AdHelper.showBroccoli(binding.adIncludeLayout);
            AdHelper.loadAndShowNative(this, binding.nativeAd0, "tiny", "sn_terms_of_use");
        } else {
            binding.nativeAd0.setVisibility(View.GONE);
        }
        type = getIntent().getIntExtra("type", 0);
        binding.privacyPolicyTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Stat.openPrivacyPolicy(getApplicationContext());
            }
        });

        binding.termsConditionsTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), TermsConditionsActivity.class));
            }
        });

        binding.tvText.setOnClickListener(view -> mStartAct());

    }

    private void mStartAct() {
        Intent intent = new Intent(getApplicationContext(), DashBoardActivity.class);
        AdHelper.showInterstitialAfterLoading(this, intent, "si_terms_of_use", new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
    }
}