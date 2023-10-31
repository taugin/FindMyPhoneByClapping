package com.clapping.find.phone.ui;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.clapping.find.phone.app.AdHelper;
import com.clapping.find.phone.databinding.ActivityTermsOfUseBinding;
import com.clapping.find.phone.stat.Stat;

public class TermsOfUseActivity extends BaseActivity {
    ActivityTermsOfUseBinding binding;
    int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTermsOfUseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AdHelper.showBroccoli(binding.adIncludeLayout);
        AdHelper.loadAndShowNative(this, binding.nativeAd0, "tiny", "sn_terms_of_use");
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
        AdHelper.showInterstitialAfterLoading(this, "si_terms_of_use", new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), DashBoardActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
    }
}