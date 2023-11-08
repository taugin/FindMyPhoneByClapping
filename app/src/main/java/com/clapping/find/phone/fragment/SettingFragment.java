package com.clapping.find.phone.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.clapping.find.phone.R;
import com.clapping.find.phone.app.AdHelper;
import com.clapping.find.phone.databinding.FragmentSettingBinding;
import com.clapping.find.phone.remote.RCManager;
import com.clapping.find.phone.utils.SPUtils;


public class SettingFragment extends Fragment {
    FragmentSettingBinding binding;
    private Uri selectedRingtoneUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSettingBinding.inflate(getLayoutInflater());
        if (RCManager.isShowSettingNativeAds(getActivity())) {
            binding.nativeAd0.setVisibility(View.VISIBLE);
            AdHelper.showBroccoli(binding.adIncludeLayout);
            AdHelper.loadAndShowNative(getActivity(), binding.nativeAd0, "tiny", "sn_settings_fragment");
        } else {
            binding.nativeAd0.setVisibility(View.GONE);
        }
        String ringtoneUriStr = SPUtils.getPreference(getActivity(), SPUtils.SP_NAME_RINGTONE_NAME, null);
        if (!TextUtils.isEmpty(ringtoneUriStr)) {
            Uri lastSelectedRingtoneUri = Uri.parse(ringtoneUriStr);
            String ringtoneName = getRingtoneNameFromUri(lastSelectedRingtoneUri);
            binding.songItem.setText(ringtoneName);
            selectedRingtoneUri = lastSelectedRingtoneUri;
        } else {
            Uri defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            String defaultName = getRingtoneNameFromUri(defaultUri);
            if (!TextUtils.isEmpty(defaultName)) {
                binding.songItem.setText(defaultName);
            } else {
                binding.songItem.setText(R.string.no_sound);
            }
            selectedRingtoneUri = null;
        }
        if (SPUtils.VALUE_YES.equals(SPUtils.getPreference(getActivity(), SPUtils.SP_NAME_FLASH, SPUtils.DEFAULT_FLASH_VALUE))) {
            binding.flashSwitch.setChecked(true);
        } else {
            binding.flashSwitch.setChecked(false);
        }
        if (SPUtils.VALUE_YES.equals(SPUtils.getPreference(getActivity(), SPUtils.SP_NAME_VIBRATION, SPUtils.DEFAULT_VIBRATION_VALUE))) {
            binding.vibrationSwitch.setChecked(true);
        } else {
            binding.vibrationSwitch.setChecked(false);
        }

        if (SPUtils.VALUE_YES.equals(SPUtils.getPreference(getActivity(), SPUtils.SP_NAME_RING, SPUtils.DEFAULT_RING_VALUE))) {
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
                SPUtils.setPreference(getActivity(), SPUtils.SP_NAME_FLASH, SPUtils.VALUE_YES);
            } else {
                SPUtils.setPreference(getActivity(), SPUtils.SP_NAME_FLASH, SPUtils.VALUE_NO);
            }
        });
        binding.vibrationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                SPUtils.setPreference(getActivity(), SPUtils.SP_NAME_VIBRATION, SPUtils.VALUE_YES);
            } else {
                SPUtils.setPreference(getActivity(), SPUtils.SP_NAME_VIBRATION, SPUtils.VALUE_NO);
            }
        });
        binding.ringSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                SPUtils.setPreference(getActivity(), SPUtils.SP_NAME_RING, SPUtils.VALUE_YES);
            } else {
                SPUtils.setPreference(getActivity(), SPUtils.SP_NAME_RING, SPUtils.VALUE_NO);
            }

        });

        AudioManager audioManager = (AudioManager) requireContext().getSystemService(Context.AUDIO_SERVICE);

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
                    Toast.makeText(requireContext(), volumeLevelText, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(requireContext(), volumeLevelText, Toast.LENGTH_SHORT).show();
            }
        });

        return binding.getRoot();
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
                SPUtils.setPreference(getActivity(), SPUtils.SP_NAME_RINGTONE_NAME, uri.toString());
                SPUtils.setPreference(getActivity(), SPUtils.SP_NAME_RING, SPUtils.VALUE_YES); // Enable ringtone when a ringtone is selected
            } else {
                selectedRingtoneUri = null;
                binding.songItem.setText(R.string.no_sound);

                // Clear the saved ringtone URI in SharedPreferences if none selected
                SPUtils.setPreference(getActivity(), SPUtils.SP_NAME_RINGTONE_NAME, "");
                SPUtils.setPreference(getActivity(), SPUtils.SP_NAME_RING, SPUtils.VALUE_NO); // Disable ringtone if none selected
            }
        }
    }

    // Helper method to get the ringtone name from the URI
    private String getRingtoneNameFromUri(Uri uri) {
        Ringtone ringtone = RingtoneManager.getRingtone(requireActivity(), uri);
        if (ringtone != null) {
            return ringtone.getTitle(requireActivity());
        }
        return "";
    }
}