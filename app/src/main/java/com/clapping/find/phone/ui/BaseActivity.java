package com.clapping.find.phone.ui;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.clapping.find.phone.utils.ChangeLanguage;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ChangeLanguage.createConfigurationContext(newBase));
    }
}
