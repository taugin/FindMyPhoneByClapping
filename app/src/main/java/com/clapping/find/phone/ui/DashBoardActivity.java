package com.clapping.find.phone.ui;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import com.clapping.find.phone.R;
import com.clapping.find.phone.app.AdHelper;
import com.clapping.find.phone.databinding.ActivityDashBoardBinding;
import com.clapping.find.phone.dialog.ExitDialog;
import com.clapping.find.phone.fragment.FindFragment;
import com.clapping.find.phone.remote.RCManager;
import com.clapping.find.phone.stat.Stat;

public class DashBoardActivity extends AppCompatActivity {
    ActivityDashBoardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AdHelper.loadAllInterstitial(this);
        binding = ActivityDashBoardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AdHelper.showBroccoli(binding.adIncludeLayoutTiny);
        AdHelper.loadAndShowNative(this, binding.nativeAdTiny, "tiny", "sn_dash_tiny");
        if (RCManager.isShowSlaveNative(this)) {
            AdHelper.loadAndShowNativeSlave(this, binding.nativeAdSmall, "small", "sn_dash_small");
        }
        Drawable defaultBackground = new ColorDrawable(Color.TRANSPARENT);
        Drawable pressedBackground = getResources().getDrawable(R.drawable.back_checked);
        boolean b = isMyServiceRunning(DetectionService.class);

        if (!b) {
            setPreference("startButton", "NO");
        }
        binding.navigationView.setItemIconTintList(ColorStateList.valueOf(Color.BLACK));
        binding.phoneFinderll.setBackground(defaultBackground);
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
                        binding.drawer.closeDrawer(Gravity.LEFT);
                        navigateToMainActivityWithFragment(FindFragment.class, R.drawable.find_selector);
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
                        binding.drawer.closeDrawer(Gravity.LEFT);
                        startActivity(new Intent(DashBoardActivity.this, SettingActivity.class));
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
                AdHelper.showInterstitialCallback(getApplicationContext(), new Runnable() {
                    @Override
                    public void run() {
                        binding.privacyPolicyLl.setBackground(pressedBackground);
                        binding.phoneFinderll.setBackground(defaultBackground);
                        binding.settingLl.setBackground(defaultBackground);
                        binding.referLl.setBackground(defaultBackground);
                        binding.privacyLl.setBackground(defaultBackground);
                        binding.drawer.closeDrawer(Gravity.LEFT);
                        startActivity(new Intent(getApplicationContext(), TermsConditionsActivity.class));
                    }
                });
            }
        });
        binding.start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdHelper.showInterstitialCallback(getApplicationContext(), new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                });
            }
        });
        binding.settingScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdHelper.showInterstitialCallback(getApplicationContext(), new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getApplicationContext(), SettingActivity.class));
                    }
                });
            }
        });
        binding.privacyScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Stat.openPrivacyPolicy(getApplicationContext());
            }
        });
        binding.shareScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share();
            }
        });
    }

    public static boolean checkPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private String PREFS_NAME = "PREFS";

    public boolean setPreference(String key, String value) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    private void navigateToMainActivityWithFragment(Class<? extends Fragment> fragmentClass, int iconResourceId) {
        Intent intent = new Intent(DashBoardActivity.this, MainActivity.class);
        intent.putExtra("fragmentClass", fragmentClass.getName());
        intent.putExtra("iconResourceId", iconResourceId);
        startActivity(intent);
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

    private void openCloseDialog() {
        final ExitDialog exitDialog = new ExitDialog(this);
        exitDialog.setOnExitListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DashBoardActivity.super.onBackPressed();
                exitDialog.dismiss();
                finishAffinity();
            }
        });
        exitDialog.setCancelable(true);
        exitDialog.setCanceledOnTouchOutside(false);
        exitDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (binding.drawer.isDrawerOpen(GravityCompat.START))
            binding.drawer.closeDrawer(GravityCompat.START);
        else {
            openCloseDialog();
        }
    }
}