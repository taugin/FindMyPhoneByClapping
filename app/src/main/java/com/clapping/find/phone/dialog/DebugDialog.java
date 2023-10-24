package com.clapping.find.phone.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.Html;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.clapping.find.phone.utils.ChangeLanguage;
import com.hauyu.adsdk.AdImpData;
import com.hauyu.adsdk.Utils;
import com.hauyu.adsdk.core.db.DBManager;
import com.hauyu.adsdk.core.framework.ActivityMonitor;
import com.clapping.find.phone.log.Log;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DebugDialog extends Dialog implements AdapterView.OnItemClickListener {

    private Map<String, View.OnClickListener> mFunctionMap = new HashMap<>();
    private ArrayAdapter<String> mAdapter = null;

    private static int sFunctionIndex = 0;

    public DebugDialog(Context context) {
        super(context);
        mAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, android.R.id.text1);
    }

    public void setDebugFunction(String function, View.OnClickListener listener) {
        if (!TextUtils.isEmpty(function) && listener != null) {
            mFunctionMap.put(function, listener);
            mAdapter.add(function);
        } else {
            Log.iv(Log.TAG, "function name or listener is null");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListView listView = new ListView(getContext());
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);
        setContentView(listView);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String function = mAdapter.getItem(position);
        dismiss();
        if (!TextUtils.isEmpty(function)) {
            View.OnClickListener listener = mFunctionMap.get(function);
            if (listener != null) {
                listener.onClick(view);
            }
        }
    }

    public static void showDebugDialog(View view) {
        final long[] mClicks = new long[10];
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //每次点击时，数组向前移动一位
                System.arraycopy(mClicks, 1, mClicks, 0, mClicks.length - 1);
                //为数组最后一位赋值
                mClicks[mClicks.length - 1] = SystemClock.uptimeMillis();
                //当点击到底10次的时候，拿到点击第一次的时间，获取点击到底10次的时间，看两者之间的差值是否在5s之内，如果是连续点击成功，反之失败。
                if (mClicks[0] >= (SystemClock.uptimeMillis() - 5000)) {
                    displayDialogOnTopActivityLocked(view.getContext());
                }
            }
        });
    }

    private static void displayDialogOnTopActivityLocked(Context context) {
        Activity activity = ActivityMonitor.get(context).getTopActivity();
        if (activity != null) {
            DebugDialog dialog = new DebugDialog(activity);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            dialog.setDebugFunction("Add App Detail Shortcut", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addAppDetailEntry(activity);
                }
            });
            dialog.setDebugFunction("Remove App Detail Shortcut", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeAppDetailEntry(activity);
                }
            });
            dialog.setDebugFunction("Show Ad Impression", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAdImpression(activity);
                }
            });
            dialog.setDebugFunction("Change Language", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ChangeLanguage.showLanguageDialog(true);
                }
            });
        }
    }

    private static void addAppDetailEntry(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                ShortcutInfo.Builder builder = new ShortcutInfo.Builder(context, "shortcut_app_detail");
                builder.setShortLabel("App Detail");
                builder.setIcon(Icon.createWithResource(context, context.getApplicationInfo().icon));
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.fromParts("package", context.getPackageName(), (String) null));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                builder.setIntent(intent);
                ShortcutInfo shortcutInfo = builder.build();
                ShortcutManager shortcutManager = (ShortcutManager) context.getSystemService(Context.SHORTCUT_SERVICE);
                shortcutManager.setDynamicShortcuts(Arrays.asList(shortcutInfo));
            }
        } catch (Exception | Error e) {
        }
    }

    private static void removeAppDetailEntry(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                ShortcutManager shortcutManager = (ShortcutManager) context.getSystemService(Context.SHORTCUT_SERVICE);
                shortcutManager.removeDynamicShortcuts(Arrays.asList("shortcut_app_detail"));
            }
        } catch (Exception | Error e) {
        }
    }

    public static void showAdImpression(Context context) {
        try {
            Activity activity = ActivityMonitor.get(context).getTopActivity();
            Dialog dialog = new Dialog(activity, android.R.style.Theme_DeviceDefault_Light_NoActionBar);
            dialog.setContentView(createDialogView(context));
            dialog.show();
        } catch (Exception e) {
        }
    }

    private static View createDialogView(Context context) {
        List<Map<String, Object>> mapList = DBManager.get(context).queryAllAdType();
        LinearLayout rootLayout = new LinearLayout(context);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout titleLayout = new LinearLayout(context);
        rootLayout.addView(titleLayout, -1, -2);
        titleLayout.setOrientation(LinearLayout.VERTICAL);
        int height = Utils.dp2px(context, 24);
        double totalRevenue = DBManager.get(context).queryAdRevenue();
        TextView totalTextView = null;
        totalTextView = new TextView(context);
        totalTextView.setGravity(Gravity.CENTER);
        totalTextView.setTextColor(Color.BLACK);
        totalTextView.setText("REVENUE : " + BigDecimal.valueOf(totalRevenue).setScale(3, RoundingMode.HALF_EVEN).toPlainString());
        totalTextView.setBackgroundColor(Color.GREEN);
        titleLayout.addView(totalTextView, -1, height);

        LinearLayout adTypeLayout = new LinearLayout(context);
        adTypeLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.addView(adTypeLayout, -1, -2);

        if (mapList != null && !mapList.isEmpty()) {
            int size = mapList.size();
            int sizeWithHeader = size + 1;
            for (int index = 0; index < sizeWithHeader; index++) {
                LinearLayout rowLayout = new LinearLayout(context);
                adTypeLayout.addView(rowLayout, -1, -1);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, height);
                params.weight = 1;
                TextView typeView = new TextView(context);
                typeView.setGravity(Gravity.CENTER);
                TextView revenueView = new TextView(context);
                revenueView.setGravity(Gravity.CENTER);
                TextView impView = new TextView(context);
                impView.setGravity(Gravity.CENTER);
                rowLayout.addView(typeView, params);
                rowLayout.addView(revenueView, params);
                rowLayout.addView(impView, params);
                if (index == 0) {
                    typeView.setText("AdType");
                    revenueView.setText("Revenue");
                    impView.setText("Impression");
                } else {
                    Map<String, Object> map = mapList.get(index - 1);
                    if (map != null) {
                        double revenue = (double) map.get("ad_type_revenue");
                        typeView.setText(String.valueOf(map.get("ad_type")));
                        revenueView.setText(BigDecimal.valueOf(revenue).setScale(3, RoundingMode.HALF_EVEN).toPlainString());
                        impView.setText(String.valueOf(map.get("ad_type_impression")));
                    }
                }
            }
        }
        ImageView splitView = new ImageView(context);
        height = Utils.dp2px(context, 1);
        splitView.setBackgroundColor(Color.LTGRAY);
        rootLayout.addView(splitView, -1, height);
        ListView listView = new ListView(context);
        rootLayout.addView(listView, -1, -1);
        List<AdImpData> list = DBManager.get(context).queryAllImps();
        int size = list != null ? list.size() : 0;
        totalTextView.setText(totalTextView.getText() + " , IMP : " + size + "");
        if (list != null && !list.isEmpty()) {
            ArrayAdapter<AdImpData> adapter = new ArrayAdapter<AdImpData>(context, android.R.layout.simple_list_item_1, list) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    TextView adapterView = (TextView) super.getView(position, convertView, parent);
                    adapterView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                    AdImpData adImpData = getItem(position);
                    String str = "<font color=red><big>" + (position + 1) + ".</big></font> " + adImpData.getPlacement()
                            + " | " + adImpData.getUnitName() + "<font color=red>*</font></br>"
                            + "<br>[<font color=red>" + adImpData.getPlatform() + "</font>]" + "[<font color=red>" + adImpData.getAdType() + "</font>]" + "[<font color='#a00'>" + adImpData.getNetwork() + "</font>]"
                            + "<br>" + adImpData.getUnitId()
                            + "<br>" + (adImpData.getNetworkPid() != null ? adImpData.getNetworkPid() : "-")
                            + "<br><font color=red>REVENUE : </font>" + adImpData.getValue()
                            + "<br><font color=red>TIME : </font>" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(adImpData.getAdImpTime()));
                    adapterView.setText(Html.fromHtml(str));
                    return adapterView;
                }
            };
            listView.setAdapter(adapter);
        }
        return rootLayout;
    }
}
