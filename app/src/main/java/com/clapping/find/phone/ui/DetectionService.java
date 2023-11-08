package com.clapping.find.phone.ui;

import android.Manifest;
import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Process;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.widget.RemoteViews;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.clapping.find.phone.R;
import com.clapping.find.phone.log.Log;
import com.clapping.find.phone.utils.ChangeLanguage;
import com.clapping.find.phone.utils.SPUtils;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public class DetectionService extends Service implements OnSignalsDetectedListener {
    public static final String TAG = "DetectionService";
    public int DETECT_NONE = 0;
    public int DETECT_WHISTLE = 1;
    CameraManager cameraManager;
    int delay = 1200;
    private DetectorThread detectorThread;
    long flash_value;
    HandlerThread handlerThread;
    Camera.Parameters mParams;
    boolean on = false;
    private Timer otherAppAudioTimer;
    private RecorderThread recorderThread;
    private Ringtone ringtone;
    Camera screen_camera;
    public int selectedDetection = 0;
    long vib_value;
    private boolean isVibrating = false;
    private Handler handler = new Handler();
    private Runnable runnable;
    public static final String ACTION_STOP_FUNCTIONALITIES = DetectionService.class.getName() + ".STOP_FUNCTIONALITIES";
    private long lastClapTime = 0;
    private boolean isDoubleClap = false;
    private final long DOUBLE_CLAP_THRESHOLD = 500;
    public static String CHANNEL_ID = "001";
    public static int NOTIFICATION_ID = 0x9685;

    private BroadcastReceiver stopFunctionalityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Stop flashlight
            turnOff();

            // Stop ringtone
            stopRinging();

            // Stop vibration
            stopVibrating();
        }
    };

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ChangeLanguage.createConfigurationContext(newBase));
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(CHANNEL_ID);
            if (notificationChannel == null) {
                notificationChannel = new NotificationChannel(CHANNEL_ID, "ClippingChannel", NotificationManager.IMPORTANCE_LOW);
                notificationChannel.setDescription("xyz");
                notificationChannel.enableLights(true);
                notificationChannel.setShowBadge(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }

    public void onCreate() {
        IntentFilter filter = new IntentFilter(ACTION_STOP_FUNCTIONALITIES);
        registerReceiver(stopFunctionalityReceiver, filter);
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        startDetection();
        createChannel();
        Notification notification = generateNotification();
        startForeground(NOTIFICATION_ID, notification);
        return START_STICKY;
    }

    /* access modifiers changed from: package-private */
    public void microphoneState() {
        if (((AppOpsManager) getSystemService(Context.APP_OPS_SERVICE)).checkOpNoThrow("android:get_usage_stats", Process.myUid(), getPackageName()) != 0) {
            startActivity(new Intent("android.settings.USAGE_ACCESS_SETTINGS"));
        }
        Timer timer = this.otherAppAudioTimer;
        if (timer != null) {
            timer.cancel();
        }
        Timer timer2 = new Timer();
        this.otherAppAudioTimer = timer2;
        timer2.scheduleAtFixedRate(new TimerTask() {
            /* class com.wisetechapps.whistle.find.phone.service.DetectionServiceForeground.AnonymousClass1 */

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public void run() {
                long currentTimeMillis = System.currentTimeMillis();
                List<UsageStats> queryUsageStats = ((UsageStatsManager) DetectionService.this.getSystemService(Context.USAGE_STATS_SERVICE)).queryUsageStats(3, currentTimeMillis - 3600000, currentTimeMillis);
                if (queryUsageStats != null && queryUsageStats.size() > 0) {
                    TreeMap treeMap = new TreeMap();
                    for (UsageStats usageStats : queryUsageStats) {
                        treeMap.put(Long.valueOf(usageStats.getLastTimeUsed()), usageStats);
                    }
                    if (!treeMap.isEmpty()) {
                        String packageName = ((UsageStats) treeMap.get(treeMap.lastKey())).getPackageName();
                        boolean z = false;
                        boolean z2 = DetectionService.this.getPackageManager().checkPermission(Manifest.permission.RECORD_AUDIO, packageName) == PackageManager.PERMISSION_GRANTED;
                        if (!DetectionService.this.getApplicationContext().getPackageName().equals(packageName)) {
                            z = z2;
                        }
                        if (z) {
                            if (DetectionService.this.recorderThread != null) {
                                DetectionService.this.recorderThread.stopRecording();
                                new Handler().removeCallbacksAndMessages(DetectionService.this.recorderThread);
                                DetectionService.this.recorderThread = null;
                            }
                            if (DetectionService.this.detectorThread != null) {
                                DetectionService.this.detectorThread.stopDetection();
                                new Handler().removeCallbacksAndMessages(DetectionService.this.detectorThread);
                                DetectionService.this.detectorThread = null;
                            }
                            Log.iv(Log.TAG, "stop");
                            return;
                        }
                        DetectionService.this.startDetection();
                        Log.iv(Log.TAG, "start");
                    }
                }
            }
        }, 0, 3000);
    }

    private Bitmap getRoundedBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }

        // 创建一个圆形的 Bitmap
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = bitmap.getWidth() / 2;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    private void setNotifyIcon(RemoteViews remoteViews) {
        try {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            bitmap = getRoundedBitmap(bitmap);
            if (bitmap != null) {
                remoteViews.setImageViewBitmap(R.id.ivNotificationImage, bitmap);
            }
        } catch (Exception e) {
        }
    }

    public Notification generateNotification() {
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.custom_notification);
        remoteViews.setTextViewText(R.id.tvNotificationTitle, getResources().getString(R.string.whistle_detection));
        remoteViews.setTextColor(R.id.tvNotificationTitle, ContextCompat.getColor(this, R.color.app_color));
        setNotifyIcon(remoteViews);
        Intent notificationIntent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        if (notificationIntent == null) {
            notificationIntent = new Intent(getApplicationContext(), SplashActivity.class);
        }
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setSmallIcon(R.mipmap.ic_launcher_notify)
                .setOngoing(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(Notification.PRIORITY_LOW)
                .setCustomContentView(remoteViews)
                .setContentIntent(pendingIntent);
        builder.setCustomBigContentView(remoteViews);
        builder.setStyle(new NotificationCompat.DecoratedCustomViewStyle());
        return builder.build();
    }

    private PendingIntent getPendingIntent(Context context, String str) {
        Intent intent = new Intent(context, NotificationListener.class);
        intent.setAction(str);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void startDetection() {
        this.selectedDetection = this.DETECT_WHISTLE;
        RecorderThread recorderThread2 = new RecorderThread();
        this.recorderThread = recorderThread2;
        recorderThread2.start();
        DetectorThread detectorThread2 = new DetectorThread(this.recorderThread, SPUtils.getPreference(this, SPUtils.SP_NAME_START_BUTTON, SPUtils.VALUE_NO));
        this.detectorThread = detectorThread2;
        detectorThread2.setOnSignalsDetectedListener(this);
        this.detectorThread.start();
    }

    public void onDestroy() {
        SPUtils.setPreference(this, SPUtils.SP_NAME_START_BUTTON, SPUtils.VALUE_NO);
        RecorderThread recorderThread2 = this.recorderThread;
        if (recorderThread2 != null) {
            recorderThread2.stopRecording();
            this.recorderThread = null;
        }
        DetectorThread detectorThread2 = this.detectorThread;
        if (detectorThread2 != null) {
            detectorThread2.stopDetection();
            this.detectorThread = null;
        }
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(NOTIFICATION_ID);
        this.selectedDetection = this.DETECT_NONE;
        stopVibrating();
        unregisterReceiver(stopFunctionalityReceiver);

    }

    @Override
    public void onWhistleDetected() {
        String flashValue = SPUtils.getPreference(this, "flash_value", "true");
        if ("slow".equals(flashValue)) {
            this.flash_value = 400;
        } else if ("medium".equals(flashValue)) {
            this.flash_value = 800;
        } else if ("fast".equals(flashValue)) {
            this.flash_value = 1200;
        }
        String vibrationValue = SPUtils.getPreference(this, "vibration_value", "true");
        if ("slow".equals(vibrationValue)) {
            this.vib_value = 300;
        } else if ("medium".equals(vibrationValue)) {
            this.vib_value = 600;
        } else if ("fast".equals(vibrationValue)) {
            this.vib_value = 900;
        }
        long currentTime = System.currentTimeMillis();
        long timeSinceLastClap = currentTime - lastClapTime;
        lastClapTime = currentTime;

        if (timeSinceLastClap <= DOUBLE_CLAP_THRESHOLD) {
            // Double clap detected
            isDoubleClap = true;
        } else {
            // Single clap detected, reset the double clap flag
            isDoubleClap = false;
        }
        Log.iv(TAG, "isDoubleClap : " + isDoubleClap);
        if (isDoubleClap) {
            String startStatus = SPUtils.getPreference(this, SPUtils.SP_NAME_START_BUTTON, SPUtils.VALUE_NO);
            Log.iv(TAG, "startStatus : " + startStatus);
            if (SPUtils.VALUE_YES.equals(startStatus)) {
                String ringStatus = SPUtils.getPreference(this, SPUtils.SP_NAME_RING, SPUtils.DEFAULT_RING_VALUE);
                Log.iv(TAG, "ringStatus : " + ringStatus);
                if (SPUtils.VALUE_YES.equals(ringStatus)) {
                    String ringtoneName = SPUtils.getPreference(this, SPUtils.SP_NAME_RINGTONE_NAME, null);
                    Uri ringtoneUri = null;
                    if (!TextUtils.isEmpty(ringtoneName)) {
                        ringtoneUri = Uri.parse(ringtoneName);
                    }
                    if (ringtoneUri == null) {
                        ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    }

                    MediaPlayer mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(getApplicationContext(), ringtoneUri);
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        Thread.sleep(3000); // Adjust the delay as needed
                        // Stop the media player and release resources
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        mediaPlayer.release();
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                String vibrationStatus = SPUtils.getPreference(this, SPUtils.SP_NAME_VIBRATION, SPUtils.DEFAULT_VIBRATION_VALUE);
                Log.iv(TAG, "vibrationStatus : " + vibrationStatus);
                if (SPUtils.VALUE_YES.equals(vibrationStatus)) {
                    // Inside your method
                    if (!isVibrating) {
                        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        if (vibrator != null && vibrator.hasVibrator()) {
                            // Check if the device has a vibrator and it's enabled
                            if (Build.VERSION.SDK_INT >= 26) {
                                vibrator.vibrate(VibrationEffect.createWaveform(new long[]{3000, 2000, 3000, 2000}, -1));
                            } else {
                                vibrator.vibrate(new long[]{3000, 2000, 3000, 2000}, -1);
                            }
                            isVibrating = true;

                            // Schedule a runnable to stop the vibration after the desired duration (e.g., 5 seconds)
                            runnable = new Runnable() {
                                @Override
                                public void run() {
                                    vibrator.cancel(); // Stop the vibration
                                    isVibrating = false; // Reset the flag
                                }
                            };
                            handler.postDelayed(runnable, 5000); // 5000 milliseconds = 5 seconds
                        }
                    }
                }
                String flashStatus = SPUtils.getPreference(this, SPUtils.SP_NAME_FLASH, SPUtils.DEFAULT_FLASH_VALUE);
                Log.iv(TAG, "flashStatus : " + flashStatus);
                if (SPUtils.VALUE_YES.equals(flashStatus)) {
                    new Thread() {
                        public void run() {
                            try {
                                if (Build.VERSION.SDK_INT >= 23) {
                                    DetectionService.this.cameraManager = (CameraManager) DetectionService.this.getSystemService(Context.CAMERA_SERVICE);
                                } else if (DetectionService.this.screen_camera == null) {
                                    DetectionService.this.screen_camera = Camera.open();
                                    try {
                                        DetectionService.this.screen_camera.setPreviewDisplay(null);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    DetectionService.this.screen_camera.startPreview();
                                }
                                for (int i = 0; i < 3; i++) {
                                    DetectionService.this.toggleFlashLight();
                                    sleep((long) DetectionService.this.delay);
                                }
                                if (Build.VERSION.SDK_INT >= 23) {
                                    try {
                                        DetectionService.this.cameraManager.setTorchMode(DetectionService.this.cameraManager.getCameraIdList()[0], false);
                                    } catch (CameraAccessException e2) {
                                        e2.printStackTrace();
                                    }
                                } else if (DetectionService.this.screen_camera != null) {
                                    DetectionService.this.screen_camera.stopPreview();
                                    DetectionService.this.screen_camera.release();
                                    DetectionService.this.screen_camera = null;
                                }
                            } catch (Exception e3) {
                                e3.printStackTrace();
                            }
                        }
                    }.start();
                }
            }
        }
    }

    public void toggleFlashLight() {
        if (!this.on) {
            turnOn();
        } else {
            turnOff();
        }
    }

    public void turnOn() {
        if (Build.VERSION.SDK_INT >= 23) {
            CameraManager cameraManager2 = this.cameraManager;
            if (cameraManager2 != null) {
                try {
                    cameraManager2.setTorchMode(cameraManager2.getCameraIdList()[0], true);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
            this.on = true;
            return;
        }
        Camera camera = this.screen_camera;
        if (camera != null) {
            Camera.Parameters parameters = camera.getParameters();
            this.mParams = parameters;
            parameters.setFlashMode("torch");
            this.screen_camera.setParameters(this.mParams);
            this.on = true;
        }
    }

    public void turnOff() {
        if (Build.VERSION.SDK_INT >= 23) {
            CameraManager cameraManager2 = this.cameraManager;
            if (cameraManager2 != null) {
                try {
                    cameraManager2.setTorchMode(cameraManager2.getCameraIdList()[0], false);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
            this.on = false;
            return;
        }
        Camera camera = this.screen_camera;
        if (camera != null) {
            Camera.Parameters parameters = camera.getParameters();
            this.mParams = parameters;
            if (parameters.getFlashMode().equals("torch")) {
                this.mParams.setFlashMode("off");
                this.screen_camera.setParameters(this.mParams);
            }
            this.on = false;
        }
    }

    public void stopRinging() {
        try {
            if (this.ringtone.isPlaying()) {
                Log.d("Test", "stopped");
                this.ringtone.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void stopVibrating() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && isVibrating) {
            vibrator.cancel();
            isVibrating = false;
        }
    }

}