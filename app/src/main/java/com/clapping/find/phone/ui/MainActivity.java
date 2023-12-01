package com.clapping.find.phone.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.clapping.find.phone.R;
import com.clapping.find.phone.app.AdHelper;
import com.clapping.find.phone.databinding.ActivityMainBinding;
import com.clapping.find.phone.fragment.FindFragment;
import com.clapping.find.phone.fragment.SettingFragment;
import com.clapping.find.phone.remote.RCManager;
import com.clapping.find.phone.stat.Stat;
import com.clapping.find.phone.utils.PermissionsUtils;
import com.clapping.find.phone.utils.SPUtils;

public class MainActivity extends BaseActivity {
    ActivityMainBinding binding;
    private SettingFragment mSettingFragment;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AdHelper.loadAllInterstitial(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Drawable defaultBackground = new ColorDrawable(Color.TRANSPARENT);
        Drawable pressedBackground = getResources().getDrawable(R.drawable.back_checked);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(
                R.id.fragment_container, new FindFragment()
        ).commit();
        binding.menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.drawer.openDrawer(Gravity.LEFT);
            }
        });
        binding.phoneFinderll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.phoneFinderll.setBackground(pressedBackground);
                binding.settingLl.setBackground(defaultBackground);
                binding.referLl.setBackground(defaultBackground);
                binding.privacyLl.setBackground(defaultBackground);
                binding.privacyPolicyLl.setBackground(defaultBackground);
                binding.drawer.closeDrawer(Gravity.LEFT);
                AdHelper.showInterstitialCallback(getApplicationContext(), "si_navi_finder_2", new Runnable() {
                    @Override
                    public void run() {
                        try {
                            binding.find.setImageDrawable(getResources().getDrawable(R.drawable.find_icon));
                            binding.settings.setImageDrawable(getResources().getDrawable(R.drawable.setting));
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.fragment_container, new FindFragment());
                            fragmentTransaction.commit();
                        } catch (Exception e) {
                        }
                    }
                });
            }
        });
        binding.settingLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.settingLl.setBackground(pressedBackground);
                binding.phoneFinderll.setBackground(defaultBackground);
                binding.referLl.setBackground(defaultBackground);
                binding.privacyLl.setBackground(defaultBackground);
                binding.privacyPolicyLl.setBackground(defaultBackground);
                binding.drawer.closeDrawer(Gravity.LEFT);
                AdHelper.showInterstitialCallback(getApplicationContext(), "si_navi_settings_2", new Runnable() {
                    @Override
                    public void run() {
                        try {
                            startActivity(new Intent(MainActivity.this, SettingActivity.class));
                        } catch (Exception e) {
                        }
                    }
                });
            }
        });
        binding.referLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.referLl.setBackground(pressedBackground);
                binding.phoneFinderll.setBackground(defaultBackground);
                binding.settingLl.setBackground(defaultBackground);
                binding.privacyLl.setBackground(defaultBackground);
                binding.privacyPolicyLl.setBackground(defaultBackground);
                binding.drawer.closeDrawer(Gravity.LEFT);
                share();
            }
        });
        binding.privacyLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.privacyLl.setBackground(pressedBackground);
                binding.phoneFinderll.setBackground(defaultBackground);
                binding.settingLl.setBackground(defaultBackground);
                binding.referLl.setBackground(defaultBackground);
                binding.privacyPolicyLl.setBackground(defaultBackground);
                binding.drawer.closeDrawer(Gravity.LEFT);
                Stat.openPrivacyPolicy(getApplicationContext());
            }
        });
        binding.privacyPolicyLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.privacyPolicyLl.setBackground(pressedBackground);
                binding.phoneFinderll.setBackground(defaultBackground);
                binding.settingLl.setBackground(defaultBackground);
                binding.referLl.setBackground(defaultBackground);
                binding.privacyLl.setBackground(defaultBackground);
                binding.drawer.closeDrawer(Gravity.LEFT);
                AdHelper.showInterstitialCallback(getApplicationContext(), "si_navi_policy_2", new Runnable() {
                    @Override
                    public void run() {
                        try {
                            startActivity(new Intent(MainActivity.this, TermsConditionsActivity.class));
                        } catch (Exception e) {
                        }
                    }
                });
            }
        });
        binding.findLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Stat.reportEvent(getApplicationContext(), "click_frg_finder");
                binding.find.setImageDrawable(getResources().getDrawable(R.drawable.find_icon));
                binding.settings.setImageDrawable(getResources().getDrawable(R.drawable.setting));
                mSettingFragment = null;
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new FindFragment());
                fragmentTransaction.commit();
            }
        });
        binding.settingsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Stat.reportEvent(getApplicationContext(), "click_frg_settings");
                binding.find.setImageDrawable(getResources().getDrawable(R.drawable.find_unchecked));
                binding.settings.setImageDrawable(getResources().getDrawable(R.drawable.settings_checked));
                mSettingFragment = new SettingFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, mSettingFragment);
                fragmentTransaction.commit();
            }
        });
        binding.find.setImageDrawable(getResources().getDrawable(R.drawable.find_icon));
        binding.settings.setImageDrawable(getResources().getDrawable(R.drawable.setting));
        PermissionsUtils.showBatteryIgnoreTip(this);
    }

    @TargetApi(Build.VERSION_CODES.Q)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (Settings.canDrawOverlays(this)) {
            SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();
            editor.putBoolean("switch", true);
            editor.apply();
            SPUtils.setPreference(getApplicationContext(), "battery", SPUtils.VALUE_NO);
        }
    }

    @Override
    public void onBackPressed() {
        if (RCManager.isAdUser(this)) {
            AdHelper.showInterstitialCallback(getApplicationContext(), "si_back_main", new Runnable() {
                @Override
                public void run() {
                    try {
                        MainActivity.super.onBackPressed();
                    } catch (Exception e) {
                    }
                }
            });
        } else {
            MainActivity.super.onBackPressed();
        }
    }

    private void share() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name);
        String shareMessage = "\nLet me recommend you this application\n\n";
        shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + getPackageName() + "\n\n";
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        startActivity(Intent.createChooser(shareIntent, "Choose one"));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Intent stopIntent = new Intent(DetectionService.ACTION_STOP_FUNCTIONALITIES);
            sendBroadcast(stopIntent);
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) && mSettingFragment != null) {
            mSettingFragment.onKeyDown(keyCode, event);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}