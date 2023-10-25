package com.clapping.find.phone.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.clapping.find.phone.R;
import com.clapping.find.phone.app.AdHelper;
import com.clapping.find.phone.ui.DetectionService;
import com.clapping.find.phone.ui.UseActivity;

public class FindFragment extends Fragment {
    TextView use, tap;
    ImageView checkbox;
    private String PREFS_NAME = "PREFS";

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_find, container, false);
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
                startActivity(new Intent(requireActivity(), UseActivity.class));
            }
        });

        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getPreference("startButton").equals("NO")) {
                    tap.setText("Tap to Inactive");
                    checkbox.setBackground(getResources().getDrawable(R.drawable.tap_checked));
                    setPreference("startButton", "YES");
                    ContextCompat.startForegroundService(getContext(), new Intent(getContext(), DetectionService.class));
                } else {
                    tap.setText("Tap to Active");
                    setPreference("startButton", "NO");
                    checkbox.setBackground(getResources().getDrawable(R.drawable.tap_unchecked));
                    requireActivity().stopService(new Intent(getContext(), DetectionService.class));
                }
            }
        });

        // Set UI state based on the 'startButton' preference value
        boolean isServiceActive = getPreference("startButton").equals("YES");
        if (isServiceActive) {
            tap.setText("Tap to Inactive");
            checkbox.setBackground(getResources().getDrawable(R.drawable.tap_checked));
        } else {
            tap.setText("Tap to Active");
            checkbox.setBackground(getResources().getDrawable(R.drawable.tap_unchecked));
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Update UI state based on the current service status
        boolean isServiceActive = getPreference("startButton").equals("YES");
        if (isServiceActive) {
            tap.setText("Tap to Inactive");
            checkbox.setBackground(getResources().getDrawable(R.drawable.tap_checked));
        } else {
            tap.setText("Tap to Active");
            checkbox.setBackground(getResources().getDrawable(R.drawable.tap_unchecked));
        }
    }
}
