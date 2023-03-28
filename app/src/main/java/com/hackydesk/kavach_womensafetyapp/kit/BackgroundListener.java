package com.hackydesk.kavach_womensafetyapp.kit;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.work.Configuration;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.hackydesk.kavach_womensafetyapp.Home;
import com.hackydesk.kavach_womensafetyapp.R;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class BackgroundListener extends Service implements RecognitionListener, SensorEventListener {
    private SpeechRecognizer speechRecognizer;
    NotificationManager notificationManager;
    Notification.Builder notification = null;
    private Intent recognizerIntent;
    private Sensor mAccelerometer;

    private static final float SHAKE_THRESHOLD_GRAVITY = 1.7F;
    private static final int SHAKE_SLOP_TIME_MS = 500;
    private static final int SHAKE_COUNT_RESET_TIME_MS = 3000;
    private long lastShakeTime = 0;
    private SensorManager sensorManager;
    private long mShakeTimestamp;
    private int mShakeCount;
    private Sensor accelerometer;
    private final float shakeThreshold = 1.5f;
    private long lastUpdate;

    private static final int REQUEST_RECORD_PERMISSION = 100;
    private int maxLinesInput = 10;
    private SpeechRecognizer speech = null;
    private String LOG_TAG = "VoiceRecognitionActivity";
    boolean listening = false;

    @Override
    public void onCreate() {
        super.onCreate();
        listening = true;
        start();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final String CHANNELID = "Stealth Mode";
        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(
                    CHANNELID,
                    CHANNELID,
                    NotificationManager.IMPORTANCE_HIGH
            );
        }

        notificationManager = getSystemService(NotificationManager.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(channel);
        }

        Notification.Builder notification = null;
        Intent SafeHomeIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            SafeHomeIntent = new Intent(BackgroundListener.this, Home.class);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = new Notification.Builder(BackgroundListener.this, CHANNELID)
                    .setContentText("Kavach Mode Active")
                    .setContentTitle("Kavach")
                    .setSmallIcon(R.drawable.female_avatar_girl_face_woman_user_2_svgrepo_com__2_)
                    .setColorized(true).setColor(Color.WHITE)
                    .setAutoCancel(false);
        }
        assert notification != null;
        lastUpdate = System.currentTimeMillis();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
      //  speech.startListening(recognizerIntent);
        startForeground(1002, notification.build());
        Toast.makeText(this, "Service is Running ", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }

    public void start(){

        speech = SpeechRecognizer.createSpeechRecognizer(this);
        Log.i(LOG_TAG, "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(this));
        speech.setRecognitionListener(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, maxLinesInput);
    }

    public void turnOf(){
        speech.stopListening();
        speech.destroy();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onReadyForSpeech(Bundle bundle) {
        Log.i(LOG_TAG, "onReadyForSpeech");

    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBeginningOfSpeech");
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
        if(!listening){
            turnOf();
        }
    }

    @Override
    public void onBufferReceived(byte[] bytes) {
        Log.i(LOG_TAG, "onBufferReceived: " + bytes);

    }

    @Override
    public void onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech");
    }

    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        Log.d(LOG_TAG, "FAILED " + errorMessage);

        Toast.makeText(BackgroundListener.this, errorMessage, Toast.LENGTH_SHORT).show();
        // speech.startListening(recognizerIntent);

    }

    @Override
    public void onResults(Bundle results) {
        Log.i(LOG_TAG, "onResults");
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        for (String result : matches)
            text += result + "\n";
        Log.i(LOG_TAG, "onResults="+text);
        Toast.makeText(BackgroundListener.this, text, Toast.LENGTH_SHORT).show();
        // speech.startListening(recognizerIntent);
    }

    @Override
    public void onPartialResults(Bundle results) {
        Log.i(LOG_TAG, "onPartialResults");
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        for (String result : matches)
            text += result + "\n";
        Log.i(LOG_TAG, "onPartialResults="+text);

    }

    @Override
    public void onEvent(int i, Bundle bundle) {
        Log.i(LOG_TAG, "onEvent");

    }

    public String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                turnOf();
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }

    public void onSensorChanged(SensorEvent event) {

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float gX = x / SensorManager.GRAVITY_EARTH;
        float gY = y / SensorManager.GRAVITY_EARTH;
        float gZ = z / SensorManager.GRAVITY_EARTH;

        // gForce will be close to 1 when there is no movement.
        float gForce = (float)Math.sqrt(gX * gX + gY * gY + gZ * gZ);

        if (gForce > SHAKE_THRESHOLD_GRAVITY) {
            final long now = System.currentTimeMillis();
            // ignore shake events too close to each other (500ms)
            if (mShakeTimestamp + SHAKE_SLOP_TIME_MS > now) {
                return;
            }
            // reset the shake count after 3 seconds of no shakes
            if (mShakeTimestamp + SHAKE_COUNT_RESET_TIME_MS < now) {
                mShakeCount = 0;
            }
            mShakeTimestamp = now;
            mShakeCount++;
            if (mShakeCount>3) {
                shakeEvent();
                mShakeCount=0;
            }
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not implemented
    }
    private void shakeEvent()
    {
        Toast.makeText(BackgroundListener.this, "Shake Event Starting Sos ", Toast.LENGTH_SHORT).show();
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(BackgroundListener.this, notification);
        r.play();
         Intent serviceIntent = new Intent(BackgroundListener.this,DangerModeService.class);
        BackgroundListener.this.startForegroundService(serviceIntent);
        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service is Destroyed ", Toast.LENGTH_SHORT).show();
        //speechRecognizer.destroy();
        sensorManager.unregisterListener(this);
        Intent broadcastIntent = new Intent(this, SensorRestarterBroadcastReceiver.class);
        sendBroadcast(broadcastIntent);
    }

}
