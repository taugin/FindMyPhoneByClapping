package com.clapping.find.phone.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.clapping.find.phone.R;
import com.clapping.find.phone.app.AdHelper;
import com.clapping.find.phone.remote.RCManager;


/**
 * Created by Administrator on 2019-10-11.
 */

public class ExitDialog extends Dialog implements View.OnClickListener {
    private ViewGroup mAdContainer;
    private View.OnClickListener mOnClickListener;

    public ExitDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_exit_layout);
        mAdContainer = findViewById(R.id.bass_ad_container);
        findViewById(R.id.exit_button).setOnClickListener(this);
        findViewById(R.id.cancel_button).setOnClickListener(this);
        updateWindow();
        showAdView();
    }

    private void updateWindow() {
        try {
            WindowManager.LayoutParams p = getWindow().getAttributes();
            DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
            p.width = (int) (dm.widthPixels * 0.9f);
            p.dimAmount = 0.8f;
            p.gravity = Gravity.CENTER;
            getWindow().setAttributes(p);
            getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        } catch (Exception e) {
        }
    }

    private void showAdView() {
        if (RCManager.isShowExitNativeAds(getContext())) {
            mAdContainer.setVisibility(View.VISIBLE);
            try {
                View view = findViewById(R.id.exit_tiny_ad);
                AdHelper.showBroccoli(view);
            } catch (Exception e) {
                mAdContainer.setVisibility(View.GONE);
            }
            AdHelper.loadAndShowNative(getContext(), mAdContainer, "tiny", "sn_exit_dialog");
        } else {
            mAdContainer.setVisibility(View.GONE);
        }
    }

    public void setOnExitListener(View.OnClickListener l) {
        mOnClickListener = l;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.exit_button) {
            if (mOnClickListener != null) {
                mOnClickListener.onClick(v);
            }
        } else if (v.getId() == R.id.cancel_button) {
            dismiss();
        }
    }
}
