package com.clapping.find.phone.ui;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import com.clapping.find.phone.R;
import com.clapping.find.phone.app.AdHelper;
import com.clapping.find.phone.databinding.ActivityDashBoardBinding;
import com.clapping.find.phone.dialog.ExitDialog;
import com.clapping.find.phone.fragment.FindFragment;
import com.clapping.find.phone.remote.RCManager;
import com.clapping.find.phone.stat.Stat;
import com.clapping.find.phone.utils.SPUtils;

public class DashBoardActivity extends BaseActivity {
    ActivityDashBoardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AdHelper.loadAllInterstitial(this);
        binding = ActivityDashBoardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (RCManager.isAdUser(this)) {
            binding.nativeAdTiny.setVisibility(View.VISIBLE);
            AdHelper.showBroccoli(binding.adIncludeLayoutTiny);
            AdHelper.loadAndShowNative(this, binding.nativeAdTiny, "tiny", "sn_dash_tiny");
            if (RCManager.isShowSlaveNative(this)) {
                AdHelper.loadAndShowNativeSlave(this, binding.nativeAdSmall, "tiny", "sn_dash_small");
            }
        } else {
            binding.nativeAdTiny.setVisibility(View.GONE);
        }
        Drawable defaultBackground = new ColorDrawable(Color.TRANSPARENT);
        Drawable pressedBackground = getResources().getDrawable(R.drawable.back_checked);
        boolean b = isMyServiceRunning(DetectionService.class);

        if (!b) {
            SPUtils.setPreference(this, "startButton", "NO");
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
                binding.phoneFinderll.setBackground(defaultBackground);
                binding.settingLl.setBackground(defaultBackground);
                binding.referLl.setBackground(defaultBackground);
                binding.privacyLl.setBackground(defaultBackground);
                binding.privacyPolicyLl.setBackground(defaultBackground);
                binding.drawer.closeDrawer(Gravity.LEFT);
                AdHelper.showInterstitialAfterLoading(DashBoardActivity.this, null, "si_navi_finder", new Runnable() {
                    @Override
                    public void run() {
                        navigateToMainActivityWithFragment(FindFragment.class, R.drawable.find_selector);
                    }
                });
            }
        });
        binding.settingLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.settingLl.setBackground(defaultBackground);
                binding.phoneFinderll.setBackground(defaultBackground);
                binding.referLl.setBackground(defaultBackground);
                binding.privacyLl.setBackground(defaultBackground);
                binding.privacyPolicyLl.setBackground(defaultBackground);
                binding.drawer.closeDrawer(Gravity.LEFT);
                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                AdHelper.showInterstitialAfterLoading(DashBoardActivity.this, intent, "si_navi_settings", new Runnable() {
                    @Override
                    public void run() {
                        startActivity(intent);
                    }
                });
            }
        });
        binding.referLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.referLl.setBackground(defaultBackground);
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
                binding.privacyLl.setBackground(defaultBackground);
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
                binding.privacyPolicyLl.setBackground(defaultBackground);
                binding.phoneFinderll.setBackground(defaultBackground);
                binding.settingLl.setBackground(defaultBackground);
                binding.referLl.setBackground(defaultBackground);
                binding.privacyLl.setBackground(defaultBackground);
                binding.drawer.closeDrawer(Gravity.LEFT);
                Intent intent = new Intent(getApplicationContext(), TermsConditionsActivity.class);
                AdHelper.showInterstitialAfterLoading(DashBoardActivity.this, intent, "si_navi_policy", new Runnable() {
                    @Override
                    public void run() {
                        startActivity(intent);
                    }
                });
            }
        });

        binding.changeLanguageLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.changeLanguageLl.setBackground(defaultBackground);
                binding.phoneFinderll.setBackground(defaultBackground);
                binding.settingLl.setBackground(defaultBackground);
                binding.referLl.setBackground(defaultBackground);
                binding.privacyLl.setBackground(defaultBackground);
                binding.drawer.closeDrawer(Gravity.LEFT);
                Intent intent = new Intent(getApplicationContext(), LanguageActivity.class);
                AdHelper.showInterstitialAfterLoading(DashBoardActivity.this, intent, "si_navi_language", new Runnable() {
                    @Override
                    public void run() {
                        startActivity(intent);
                    }
                });
            }
        });

        binding.startLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                AdHelper.showInterstitialAfterLoading(DashBoardActivity.this, intent, "si_goto_main", new Runnable() {
                    @Override
                    public void run() {
                        startActivity(intent);
                    }
                });
            }
        });
        binding.settingScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                AdHelper.showInterstitialAfterLoading(DashBoardActivity.this, intent, "si_goto_settings", new Runnable() {
                    @Override
                    public void run() {
                        startActivity(intent);
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