package com.clapping.find.phone.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.WindowManager;

import com.clapping.find.phone.R;
import com.clapping.find.phone.app.AdHelper;

public class AdDialog extends Dialog {
    private static final Handler sHandler = new Handler(Looper.myLooper());

    public AdDialog(Context context) {
        super(context);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_ad_loading);
        updateWindow();
    }

    private void updateWindow() {
        try {
            WindowManager.LayoutParams p = getWindow().getAttributes();
            p.dimAmount = 0.8f;
            p.gravity = Gravity.CENTER;
            getWindow().setAttributes(p);
            getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        } catch (Exception e) {
        }
    }
}
