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
        AdHelper.loadAndShowNative(this, binding.nativeAd0, "small", "sn_termsofuse");
        type = getIntent().getIntExtra("type", 0);
//        AdUtils.showNativeAd(activity, Constants.adsResponseModel.getNative_ads().getAdx(), binding.nativeAd0, 2, null);
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

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void mStartAct() {
        startActivity(new Intent(getApplicationContext(), DashBoardActivity.class));
        finish();
    }
}