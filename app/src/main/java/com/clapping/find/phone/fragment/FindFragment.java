package com.clapping.find.phone.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.clapping.find.phone.R;
import com.clapping.find.phone.app.AdHelper;
import com.clapping.find.phone.databinding.FragmentFindBinding;
import com.clapping.find.phone.ui.DetectionService;
import com.clapping.find.phone.ui.UseActivity;
import com.hauyu.adsdk.Utils;
import com.moon.BcSdk;
import com.moon.listener.OnPermissionListener;

import java.util.Arrays;
import java.util.List;

public class FindFragment extends Fragment {
    TextView use, tap;
    ImageView checkbox;
    private String PREFS_NAME = "PREFS";

    private FragmentFindBinding binding;

    public boolean setPreference(String key, String value) {
        SharedPreferences settings = requireActivity().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    public String getPreference(String key) {
        SharedPreferences settings = requireActivity().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return settings.getString(key, "true");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_find, container, false);
        binding = FragmentFindBinding.bind(view);
        ViewGroup native_ad_large = view.findViewById(R.id.native_ad_tiny);
        AdHelper.showBroccoli(native_ad_large.findViewById(R.id.ad_include_layout));
        AdHelper.loadAndShowNative(getActivity(), native_ad_large, "tiny", "sn_find_fragment");
        use = view.findViewById(R.id.use);
        tap = view.findViewById(R.id.tap);
        checkbox = view.findViewById(R.id.enable_disable);
        checkbox.setBackground(getResources().getDrawable(R.drawable.tap_unchecked));

        use.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdHelper.showInterstitialCallback(requireActivity(), new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(requireActivity(), UseActivity.class));
                    }
                });
            }
        });

        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ("NO".equals(getPreference("startButton"))) {
                    if (isAllPermissionGrant()) {
                        activeClap();
                    } else {
                        checkPermissionDialog();
                    }
                } else {
                    tap.setText(R.string.tap_to_active);
                    setPreference("startButton", "NO");
                    checkbox.setBackground(getResources().getDrawable(R.drawable.tap_unchecked));
                    requireActivity().stopService(new Intent(getContext(), DetectionService.class));
                }
            }
        });

        // Set UI state based on the 'startButton' preference value
        boolean isServiceActive = getPreference("startButton").equals("YES");
        if (isServiceActive) {
            tap.setText(R.string.tap_to_inactive);
            checkbox.setBackground(getResources().getDrawable(R.drawable.tap_checked));
        } else {
            tap.setText(R.string.tap_to_active);
            checkbox.setBackground(getResources().getDrawable(R.drawable.tap_unchecked));
        }

        if (getPreference("flash").equals("YES")) {
            binding.flashSwitch.setChecked(true);
        } else {
            binding.flashSwitch.setChecked(false);
        }
        if (getPreference("vibration").equals("YES")) {
            binding.vibrationSwitch.setChecked(true);
        } else {
            binding.vibrationSwitch.setChecked(false);
        }

        binding.flashSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                setPreference("flash", "YES");
            } else {
                setPreference("flash", "NO");
            }
        });
        binding.vibrationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                setPreference("vibration", "YES");
            } else {
                setPreference("vibration", "NO");
            }
        });

        return view;
    }

    private void activeClap() {
        tap.setText(R.string.tap_to_inactive);
        checkbox.setBackground(getResources().getDrawable(R.drawable.tap_checked));
        setPreference("startButton", "YES");
        ContextCompat.startForegroundService(getContext(), new Intent(getContext(), DetectionService.class));
    }

    private boolean isAllPermissionGrant() {
        return isPermissionGrant(Manifest.permission.CAMERA) && isPermissionGrant(Manifest.permission.RECORD_AUDIO) && isPermissionGrant(Manifest.permission.POST_NOTIFICATIONS);
    }

    private TextView mCameraTipView;
    private TextView mMicTipView;
    private TextView mNotificationTipView;

    private void checkPermissionDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(false);
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_permission_layout, null);
        dialog.setContentView(dialogView);
        mCameraTipView = dialogView.findViewById(R.id.camera_permission_tip);
        mMicTipView = dialogView.findViewById(R.id.mic_permission_tip);
        mNotificationTipView = dialogView.findViewById(R.id.notification_permission_tip);
        updateViewStatus(mCameraTipView, isPermissionGrant(Manifest.permission.CAMERA));
        updateViewStatus(mMicTipView, isPermissionGrant(Manifest.permission.RECORD_AUDIO));
        updateViewStatus(mNotificationTipView, isPermissionGrant(Manifest.permission.POST_NOTIFICATIONS));
        dialogView.findViewById(R.id.grant_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissions(dialog);
            }
        });
        dialogView.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        try {
            WindowManager.LayoutParams p = dialog.getWindow().getAttributes();
            DisplayMetrics dm = getResources().getDisplayMetrics();
            p.width = (int) (dm.widthPixels * 0.9f);
            p.dimAmount = 0.8f;
            p.gravity = Gravity.CENTER;
            dialog.getWindow().setAttributes(p);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        } catch (Exception e) {
        }
    }

    private void requestPermissions(final Dialog dialog) {
        List<String> permissions = Arrays.asList(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.POST_NOTIFICATIONS);
        BcSdk.requestPermissions(getActivity(), permissions, true, new OnPermissionListener() {
            @Override
            public void onPermissionResult(List<String> grantList, List<String> deniedList, boolean goSettings) {
                updateViewStatus(mCameraTipView, isPermissionGrant(Manifest.permission.CAMERA));
                updateViewStatus(mMicTipView, isPermissionGrant(Manifest.permission.RECORD_AUDIO));
                updateViewStatus(mNotificationTipView, isPermissionGrant(Manifest.permission.POST_NOTIFICATIONS));
                if (isAllPermissionGrant()) {
                    activeClap();
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            }
        });
    }

    private boolean isPermissionGrant(String permission) {
        return ContextCompat.checkSelfPermission(getActivity(), permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void updateViewStatus(TextView textView, boolean grant) {
        if (textView != null) {
            final int drawableSize = Utils.dp2px(getActivity(), 18);
            final int drawablePadding = Utils.dp2px(getActivity(), 8);
            final Drawable drawableGrant = getActivity().getResources().getDrawable(R.drawable.ic_select);
            drawableGrant.setBounds(0, 0, drawableSize, drawableSize);
            final Drawable drawableDenied = getActivity().getResources().getDrawable(R.drawable.dialog_cancel);
            drawableDenied.setBounds(0, 0, drawableSize, drawableSize);
            textView.setCompoundDrawablePadding(drawablePadding);
            Drawable drawable = grant ? drawableGrant : drawableDenied;
            textView.setCompoundDrawables(drawable, null, null, null);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Update UI state based on the current service status
        boolean isServiceActive = getPreference("startButton").equals("YES");
        if (isServiceActive) {
            tap.setText(R.string.tap_to_inactive);
            checkbox.setBackground(getResources().getDrawable(R.drawable.tap_checked));
        } else {
            tap.setText(R.string.tap_to_active);
            checkbox.setBackground(getResources().getDrawable(R.drawable.tap_unchecked));
        }
    }
}
