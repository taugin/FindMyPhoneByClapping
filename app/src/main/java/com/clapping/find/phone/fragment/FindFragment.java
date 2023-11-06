package com.clapping.find.phone.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
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
import com.clapping.find.phone.remote.RCManager;
import com.clapping.find.phone.ui.DetectionService;
import com.clapping.find.phone.ui.UseTipActivity;
import com.clapping.find.phone.utils.SPUtils;
import com.hauyu.adsdk.Utils;
import com.moon.BcSdk;
import com.moon.listener.OnPermissionListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FindFragment extends Fragment {
    TextView tap;
    View use;
    ImageView checkbox;
    private FragmentFindBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_find, container, false);
        binding = FragmentFindBinding.bind(view);
        ViewGroup native_ad_large = view.findViewById(R.id.native_ad_tiny);
        if (RCManager.isAdUser(getActivity())) {
            native_ad_large.setVisibility(View.VISIBLE);
            AdHelper.showBroccoli(native_ad_large.findViewById(R.id.ad_include_layout));
            AdHelper.loadAndShowNative(getActivity(), native_ad_large, "tiny", "sn_find_fragment");
        } else {
            native_ad_large.setVisibility(View.GONE);
        }
        use = view.findViewById(R.id.use);
        tap = view.findViewById(R.id.tap);
        checkbox = view.findViewById(R.id.enable_disable);
        checkbox.setBackground(getResources().getDrawable(R.drawable.tap_unchecked));

        use.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), UseTipActivity.class);
                AdHelper.showInterstitialAfterLoading(requireActivity(), intent, "si_goto_use", new Runnable() {
                    @Override
                    public void run() {
                        startActivity(intent);
                    }
                });
            }
        });

        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ("NO".equals(SPUtils.getPreference(getActivity(), "startButton", "NO"))) {
                    if (isAllPermissionGrant()) {
                        activeClap();
                    } else {
                        checkPermissionDialog();
                    }
                } else {
                    tap.setText(R.string.tap_to_active);
                    SPUtils.setPreference(getActivity(), "startButton", "NO");
                    checkbox.setBackground(getResources().getDrawable(R.drawable.tap_unchecked));
                    requireActivity().stopService(new Intent(getContext(), DetectionService.class));
                }
            }
        });

        // Set UI state based on the 'startButton' preference value
        boolean isServiceActive = "YES".equals(SPUtils.getPreference(getActivity(), "startButton", "NO"));
        if (isServiceActive) {
            tap.setText(R.string.tap_to_inactive);
            checkbox.setBackground(getResources().getDrawable(R.drawable.tap_checked));
        } else {
            tap.setText(R.string.tap_to_active);
            checkbox.setBackground(getResources().getDrawable(R.drawable.tap_unchecked));
        }

        if ("YES".equals(SPUtils.getPreference(getActivity(), "flash", null))) {
            binding.flashSwitch.setChecked(true);
        } else {
            binding.flashSwitch.setChecked(false);
        }
        if ("YES".equals(SPUtils.getPreference(getActivity(), "vibration", null))) {
            binding.vibrationSwitch.setChecked(true);
        } else {
            binding.vibrationSwitch.setChecked(false);
        }

        binding.flashSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                SPUtils.setPreference(getActivity(), "flash", "YES");
            } else {
                SPUtils.setPreference(getActivity(), "flash", "NO");
            }
        });
        binding.vibrationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                SPUtils.setPreference(getActivity(), "vibration", "YES");
            } else {
                SPUtils.setPreference(getActivity(), "vibration", "NO");
            }
        });

        return view;
    }

    private void activeClap() {
        tap.setText(R.string.tap_to_inactive);
        checkbox.setBackground(getResources().getDrawable(R.drawable.tap_checked));
        SPUtils.setPreference(getActivity(), "startButton", "YES");
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
        List<String> permissions = new ArrayList<>();
        for (String permission : Arrays.asList(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.POST_NOTIFICATIONS)) {
            if (!isPermissionGrant(permission)) {
                permissions.add(permission);
            }
        }
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
        boolean isServiceActive = "YES".equals(SPUtils.getPreference(getActivity(), "startButton", "NO"));
        if (isServiceActive) {
            tap.setText(R.string.tap_to_inactive);
            checkbox.setBackground(getResources().getDrawable(R.drawable.tap_checked));
        } else {
            tap.setText(R.string.tap_to_active);
            checkbox.setBackground(getResources().getDrawable(R.drawable.tap_unchecked));
        }
    }
}
