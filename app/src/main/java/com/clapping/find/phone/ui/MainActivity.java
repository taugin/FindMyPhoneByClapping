package com.clapping.find.phone.ui;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.clapping.find.phone.R;
import com.clapping.find.phone.app.AdHelper;
import com.clapping.find.phone.databinding.ActivityMainBinding;
import com.clapping.find.phone.fragment.FindFragment;
import com.clapping.find.phone.fragment.SettingFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    public Intent intent;
    List<Intent> POWERMANAGER_INTENTS = new ArrayList<Intent>();

    String b, b2;
    private String PREFS_NAME = "PREFS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                AdHelper.showInterstitialCallback(getApplicationContext(), new Runnable() {
                    @Override
                    public void run() {
                        binding.phoneFinderll.setBackground(pressedBackground);
                        binding.settingLl.setBackground(defaultBackground);
                        binding.referLl.setBackground(defaultBackground);
                        binding.privacyLl.setBackground(defaultBackground);
                        binding.privacyPolicyLl.setBackground(defaultBackground);
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container, new FindFragment());
                        fragmentTransaction.commit();
                  /*  binding.find.setImageDrawable(getResources().getDrawable(R.drawable.find_icon));
                    binding.findTxt.setTextColor(getResources().getColor(R.color.app_color));
                    binding.settings.setImageDrawable(getResources().getDrawable(R.drawable.setting));
                    binding.settingTxt.setTextColor(getResources().getColor(R.color.gray_light));*/
                        binding.drawer.closeDrawer(Gravity.LEFT);
                    }
                });
            }
        });
        binding.settingLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdHelper.showInterstitialCallback(getApplicationContext(), new Runnable() {
                    @Override
                    public void run() {
                        binding.settingLl.setBackground(pressedBackground);
                        binding.phoneFinderll.setBackground(defaultBackground);
                        binding.referLl.setBackground(defaultBackground);
                        binding.privacyLl.setBackground(defaultBackground);
                        binding.privacyPolicyLl.setBackground(defaultBackground);
                   /* binding.settings.setImageDrawable(getResources().getDrawable(R.drawable.settings_checked));
                    binding.settingTxt.setTextColor(getResources().getColor(R.color.app_color));
                    binding.find.setImageDrawable(getResources().getDrawable(R.drawable.find_unchecked));
                    binding.findTxt.setTextColor(getResources().getColor(R.color.gray_light));*/
                        binding.drawer.closeDrawer(Gravity.LEFT);
                        startActivity(new Intent(MainActivity.this, SettingActivity.class));
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
                AdHelper.showInterstitialCallback(getApplicationContext(), new Runnable() {
                    @Override
                    public void run() {
                        binding.privacyLl.setBackground(pressedBackground);
                        binding.phoneFinderll.setBackground(defaultBackground);
                        binding.settingLl.setBackground(defaultBackground);
                        binding.referLl.setBackground(defaultBackground);
                        binding.privacyPolicyLl.setBackground(defaultBackground);
                        binding.drawer.closeDrawer(Gravity.LEFT);
                        startActivity(new Intent(MainActivity.this, NewPrivacyPolicyActivity.class));
                    }
                });
            }
        });
        binding.privacyPolicyLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdHelper.showInterstitialCallback(getApplicationContext(), new Runnable() {
                    @Override
                    public void run() {
                        binding.privacyPolicyLl.setBackground(pressedBackground);
                        binding.phoneFinderll.setBackground(defaultBackground);
                        binding.settingLl.setBackground(defaultBackground);
                        binding.referLl.setBackground(defaultBackground);
                        binding.privacyLl.setBackground(defaultBackground);
                        binding.drawer.closeDrawer(Gravity.LEFT);
                        startActivity(new Intent(MainActivity.this, TermsConditionsActivity.class));
                    }
                });
            }
        });
        binding.findLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdHelper.showInterstitialCallback(getApplicationContext(), "navigation_bottom_find", new Runnable() {
                    @Override
                    public void run() {
                        binding.find.setImageDrawable(getResources().getDrawable(R.drawable.find_icon));
                        binding.settings.setImageDrawable(getResources().getDrawable(R.drawable.setting));
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container, new FindFragment());
                        fragmentTransaction.commit();
                    }
                });
            }
        });
        binding.settingsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdHelper.showInterstitialCallback(getApplicationContext(), "navigation_bottom_settings", new Runnable() {
                    @Override
                    public void run() {
                        binding.find.setImageDrawable(getResources().getDrawable(R.drawable.find_unchecked));
                        binding.settings.setImageDrawable(getResources().getDrawable(R.drawable.settings_checked));
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container, new SettingFragment());
                        fragmentTransaction.commit();
                    }
                });
            }
        });

        POWERMANAGER_INTENTS.add(new Intent().setComponent(
                new ComponentName(
                        "com.miui.securitycenter",
                        "com.miui.permcenter.autostart.AutoStartManagementActivity"
                )
        ));
        POWERMANAGER_INTENTS.add(new Intent().setComponent(
                new ComponentName(
                        "com.letv.android.letvsafe",
                        "com.letv.android.letvsafe.AutobootManageActivity"
                )
        ));
        POWERMANAGER_INTENTS.add(new Intent().setComponent(
                new ComponentName(
                        "com.huawei.systemmanager",
                        "com.huawei.systemmanager.optimize.process.ProtectActivity"
                )
        ));
        POWERMANAGER_INTENTS.add(new Intent().setComponent(
                new ComponentName(
                        "com.huawei.systemmanager",
                        "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity"
                )
        ));
        POWERMANAGER_INTENTS.add(new Intent().setComponent(
                new ComponentName(
                        "com.huawei.systemmanager",
                        "com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity"
                )
        ));
        POWERMANAGER_INTENTS.add(new Intent().setComponent(
                new ComponentName(
                        "com.coloros.safecenter",
                        "com.coloros.safecenter.permission.startup.StartupAppListActivity"
                )
        ));

        POWERMANAGER_INTENTS.add(new Intent().setComponent(
                new ComponentName(
                        "com.coloros.safecenter",
                        "com.coloros.safecenter.startupapp.StartupAppListActivity"
                )
        ));
        POWERMANAGER_INTENTS.add(new Intent().setComponent(
                new ComponentName(
                        "com.oppo.safe",
                        "com.oppo.safe.permission.startup.StartupAppListActivity"
                )
        ));
        POWERMANAGER_INTENTS.add(new Intent().setComponent(
                new ComponentName(
                        "com.iqoo.secure",
                        "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity"
                )
        ));
        POWERMANAGER_INTENTS.add(new Intent().setComponent(
                new ComponentName(
                        "com.iqoo.secure",
                        "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager"
                )
        ));
        POWERMANAGER_INTENTS.add(new Intent().setComponent(
                new ComponentName(
                        "com.vivo.permissionmanager",
                        "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
                )
        ));
        POWERMANAGER_INTENTS.add(new Intent().setComponent(
                new ComponentName(
                        "com.samsung.android.lool",
                        "com.samsung.android.sm.ui.battery.BatteryActivity"
                )
        ));
        POWERMANAGER_INTENTS.add(new Intent().setComponent(
                new ComponentName(
                        "com.htc.pitroad",
                        "com.htc.pitroad.landingpage.activity.LandingPageActivity"
                )
        ));
        POWERMANAGER_INTENTS.add(new Intent().setComponent(
                new ComponentName(
                        "com.asus.mobilemanager",
                        "com.asus.mobilemanager.MainActivity"
                )
        ));
        POWERMANAGER_INTENTS.add(new Intent().setComponent(
                new ComponentName(
                        "com.transsion.phonemanager",
                        "com.itel.autobootmanager.activity.AutoBootMgrActivity"
                )
        ));

        if (!Build.BRAND.equalsIgnoreCase("oppo")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
                b2 = getPreference("switchBattery");
                if (!"YES".equals(b2)) {
                    if (!powerManager.isIgnoringBatteryOptimizations(getPackageName())) {
                        for (Intent intent : POWERMANAGER_INTENTS) {
                            if (getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                                startActivity(intent);
                                setPreference("switchBattery", "YES");
                                break;
                            }
                        }
                    }
                }
            }
        }
        intent = new Intent(this, DetectionService.class);
    }

    @TargetApi(Build.VERSION_CODES.Q)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (Settings.canDrawOverlays(this)) {
            SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();
            editor.putBoolean("switch", true);
            editor.apply();
            setPreference("battery", "NO");
        }
    }

    @Override
    public void onBackPressed() {
        MainActivity.super.onBackPressed();
    }

    public boolean setPreference(String key, String value) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    public String getPreference(String key) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return settings.getString(key, "true");
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
}