package com.clapping.find.phone.ui;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.clapping.find.phone.R;
import com.clapping.find.phone.app.AdHelper;
import com.clapping.find.phone.databinding.ActivityTermsOfUseBinding;

public class TermsOfUseActivity extends AppCompatActivity {
    ActivityTermsOfUseBinding binding;
    int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_terms_of_use);
        AdHelper.loadAndShowNative(this, binding.nativeAd0, "tiny", "sn_terms_of_use");
        type = getIntent().getIntExtra("type", 0);
        binding.privacyPolicyTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), NewPrivacyPolicyActivity.class));
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
        AdHelper.showInterstitialCallback(getApplicationContext(), new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), DashBoardActivity.class));
                finish();
            }
        });
    }
}