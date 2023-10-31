package com.clapping.find.phone.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.clapping.find.phone.R;
import com.clapping.find.phone.app.AdHelper;
import com.clapping.find.phone.databinding.FrmLanguageSetBinding;
import com.clapping.find.phone.log.Log;
import com.clapping.find.phone.remote.RCManager;
import com.clapping.find.phone.stat.Stat;
import com.clapping.find.phone.utils.ChangeLanguage;
import com.hauyu.adsdk.OnAdSdkListener;
import com.hauyu.adsdk.Utils;

import java.util.List;
import java.util.Locale;

public class LanguageActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private FrmLanguageSetBinding binding = null;
    private int mSelectIndex = 0;
    private int mOldSelectIndex = -1;
    private ArrayAdapter<String> mArrayAdapter;
    private List<ChangeLanguage.LocaleInfo> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AdHelper.loadAllInterstitial(this);
        binding = FrmLanguageSetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
        initData();
    }

    private void initView() {
        binding.setLanguage.setOnClickListener(this);
        if (RCManager.isShowLanguageNativeAds(this)) {
            binding.adContainer.setVisibility(View.VISIBLE);
            AdHelper.showBroccoli(binding.adIncludeLayout);
            AdHelper.loadAndShowNative(this, binding.adContainer, "tiny", "sn_view_language");
        } else {
            binding.adContainer.setVisibility(View.GONE);
        }
    }

    private void initData() {
        mList = ChangeLanguage.getLocaleList();
        final int selectIndex = ChangeLanguage.findLocaleIndex(this);
        String followSystemString = ChangeLanguage.getFollowSystemTranslation(this, selectIndex);
        mSelectIndex = mOldSelectIndex = selectIndex;

        String[] arrays = new String[mList.size()];
        for (int index = 0; index < mList.size(); index++) {
            ChangeLanguage.LocaleInfo localeInfo = mList.get(index);
            if (localeInfo != null) {
                // locale为空时，跟随系统语言
                if (localeInfo.getLocale() == null) {
                    if (!TextUtils.isEmpty(followSystemString)) {
                        arrays[index] = followSystemString;
                    } else {
                        arrays[index] = localeInfo.getDisplay();
                    }
                } else {
                    arrays[index] = localeInfo.getDisplay();
                }
            }
        }
        int itemHeight = Utils.dp2px(this, 64);
        final ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(-1, itemHeight);
        layoutParams.leftMargin = Utils.dp2px(this, 8);
        layoutParams.rightMargin = Utils.dp2px(this, 8);
        mArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, arrays) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                String showText = getItem(position);
                CheckedTextView checkedTextView = (CheckedTextView) super.getView(position, convertView, parent);
                checkedTextView.setText(showText);
                checkedTextView.setChecked(mSelectIndex == position);
                checkedTextView.setLayoutParams(layoutParams);
                checkedTextView.setBackgroundResource(R.drawable.bg_language_item);
                checkedTextView.setElevation(1);
                return checkedTextView;
            }
        };
        binding.languageList.setAdapter(mArrayAdapter);
        binding.languageList.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == binding.setLanguage.getId()) {
            Stat.reportEvent(this, "click_set_language");
            showInterstitialIfNeed();
        }
    }

    private void showInterstitialIfNeed() {
        if (RCManager.isAdUser(this) && AdHelper.isInterstitialLoaded(this)) {
            String maxInterstitial = AdHelper.getMaxInterstitial(this);
            AdHelper.setOnIntAdListener(this, maxInterstitial, new OnAdSdkListener() {
                @Override
                public void onDismiss(String placeName, String source, String adType, String pid, boolean complexAds) {
                    AdHelper.setOnIntAdListener(getApplicationContext(), placeName, null);
                    onUserSetLanguage();
                }

                @Override
                public void onShowFailed(String placeName, String source, String adType, String pid, int error) {
                    AdHelper.setOnIntAdListener(getApplicationContext(), placeName, null);
                    onUserSetLanguage();
                }
            });
            AdHelper.showInterstitial(this, maxInterstitial, "si_set_language");
        } else {
            onUserSetLanguage();
        }
    }

    private void onUserSetLanguage() {
        if (mOldSelectIndex != mSelectIndex) {
            showSwitchDialog();
        } else {
            finish();
        }
    }

    private void setLocaleByUser() {
        try {
            ChangeLanguage.LocaleInfo localeInfo = mList.get(mSelectIndex);
            if (localeInfo != null) {
                Locale locale = localeInfo.getLocale();
                ChangeLanguage.setSelectLocale(this, locale);
                ChangeLanguage.changeLocale(this, locale);
            }
        } catch (Exception e) {
            Log.e(Log.TAG, "error : " + e);
        }
        ChangeLanguage.restartApp(this, null);
    }

    @Override
    public void onBackPressed() {
        AdHelper.showInterstitialCallback(this, "si_back_language", new Runnable() {
            @Override
            public void run() {
                LanguageActivity.super.onBackPressed();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mSelectIndex = position;
        if (mArrayAdapter != null) {
            mArrayAdapter.notifyDataSetChanged();
        }
    }

    private void showSwitchDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(false);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_restart_language, null);
        dialog.setContentView(dialogView);
        TextView onButton = dialogView.findViewById(R.id.btn_confirm);
        onButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                setLocaleByUser();
            }
        });
        TextView cancelButton = dialogView.findViewById(R.id.btn_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
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
}
