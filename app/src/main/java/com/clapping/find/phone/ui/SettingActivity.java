package com.clapping.find.phone.ui;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.clapping.find.phone.R;
import com.clapping.find.phone.app.AdHelper;
import com.clapping.find.phone.databinding.ActivitySettingBinding;
import com.clapping.find.phone.remote.RCManager;
import com.clapping.find.phone.utils.SPUtils;

public class SettingActivity extends BaseActivity {
    ActivitySettingBinding binding;
    private Uri selectedRingtoneUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AdHelper.loadAllInterstitial(this);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AdHelper.showBroccoli(binding.adIncludeLayout);
        AdHelper.loadAndShowNative(this, binding.nativeAdLarge, "tiny", "sn_settings_activity");
        String ringtoneUriStr = SPUtils.getPreference(this, "ringtone_Name", null);
        if (!TextUtils.isEmpty(ringtoneUriStr)) {
            Uri lastSelectedRingtoneUri = Uri.parse(ringtoneUriStr);
            String ringtoneName = getRingtoneNameFromUri(lastSelectedRingtoneUri);
            binding.songItem.setText(ringtoneName);
            selectedRingtoneUri = lastSelectedRingtoneUri;
        } else {
            binding.songItem.setText(R.string.no_sound);
            selectedRingtoneUri = null;
        }
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingActivity.super.onBackPressed();
            }
        });
        if ("YES".equals(SPUtils.getPreference(this, "flash", null))) {
            binding.flashSwitch.setChecked(true);
        } else {
            binding.flashSwitch.setChecked(false);
        }
        if ("YES".equals(SPUtils.getPreference(this, "vibration", null))) {
            binding.vibrationSwitch.setChecked(true);
        } else {
            binding.vibrationSwitch.setChecked(false);
        }

        if ("YES".equals(SPUtils.getPreference(this, "ring", null))) {
            binding.ringSwitch.setChecked(true);
        } else {
            binding.ringSwitch.setChecked(false);
        }
        binding.changeRingtone.setOnClickListener(v -> {
            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, getResources().getString(R.string.select_tone));
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, selectedRingtoneUri);
            startActivityForResult(intent, 5);
        });


        binding.flashSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                SPUtils.setPreference(this, "flash", "YES");
            } else {
                SPUtils.setPreference(this, "flash", "NO");
            }
        });
        binding.vibrationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                SPUtils.setPreference(this, "vibration", "YES");
            } else {
                SPUtils.setPreference(this, "vibration", "NO");
            }
        });
        binding.ringSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                SPUtils.setPreference(this, "ring", "YES");
            } else {
                SPUtils.setPreference(this, "ring", "NO");
            }

        });

        AudioManager audioManager = (AudioManager) SettingActivity.this.getSystemService(Context.AUDIO_SERVICE);

        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        binding.volumeSeekbar.setMax(maxVolume);

        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        binding.volumeSeekbar.setProgress(currentVolume);

// Set a listener to handle volume changes
        binding.volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Update the media volume when the seekbar is moved by the user
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);

                // Show a toast message for the final volume setting when the user stops interacting with the seek bar
                if (!fromUser) {
                    String volumeLevelText = "Volume Level: " + progress + "/" + maxVolume;
                    Toast.makeText(SettingActivity.this, volumeLevelText, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Not used in this case
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Show a toast message for the final volume setting when the user stops interacting with the seek bar
                int finalVolume = seekBar.getProgress();
                String volumeLevelText = "Volume Level: " + finalVolume + "/" + maxVolume;
                Toast.makeText(SettingActivity.this, volumeLevelText, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 5 && resultCode == RESULT_OK) {
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

            if (uri != null) {
                selectedRingtoneUri = uri;
                String ringtoneName = getRingtoneNameFromUri(uri);
                binding.songItem.setText(ringtoneName);

                // Save the selected ringtone URI in SharedPreferences
                SPUtils.setPreference(this, "ringtone_Name", uri.toString());
                SPUtils.setPreference(this, "ring", "YES"); // Enable ringtone when a ringtone is selected
            } else {
                selectedRingtoneUri = null;
                binding.songItem.setText(R.string.no_sound);

                // Clear the saved ringtone URI in SharedPreferences if none selected
                SPUtils.setPreference(this, "ringtone_Name", "");
                SPUtils.setPreference(this, "ring", "NO"); // Disable ringtone if none selected
            }
        }
    }

    // Helper method to get the ringtone name from the URI
    private String getRingtoneNameFromUri(Uri uri) {
        Ringtone ringtone = RingtoneManager.getRingtone(SettingActivity.this, uri);
        if (ringtone != null) {
            return ringtone.getTitle(SettingActivity.this);
        }
        return "";
    }

    @Override
    public void onBackPressed() {
        if (RCManager.isAdUser(this)) {
            AdHelper.showInterstitialCallback(getApplicationContext(), "si_back_settings", new Runnable() {
                @Override
                public void run() {
                    SettingActivity.super.onBackPressed();
                }
            });
        } else {
            SettingActivity.super.onBackPressed();
        }
    }
}