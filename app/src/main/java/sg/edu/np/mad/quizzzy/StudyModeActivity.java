package sg.edu.np.mad.quizzzy;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.Locale;

import sg.edu.np.mad.quizzzy.Flashlets.FlashletList;
import sg.edu.np.mad.quizzzy.Models.AppLifecycleObserver;
import sg.edu.np.mad.quizzzy.Search.SearchActivity;

public class StudyModeActivity extends AppCompatActivity implements SensorEventListener {
    private int studyDuration = 0;
    private boolean studyTimerRunning = false;
    private boolean wasStudyTimerRunning = false;
    private SensorManager sensorManager;
    private Sensor accelerometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_study_mode);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Log.d("TAG", "run: " + AppLifecycleObserver.getAppInForeground() + AppLifecycleObserver.getScreenOn());

        // Configure Back Button
        Toolbar toolbar = findViewById(R.id.studyToolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Configure Bottom Navigation Bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.stats);
        bottomNavigationView.setOnApplyWindowInsetsListener(null);
        bottomNavigationView.setPadding(0,0,0,0);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                if (itemId == R.id.home) {
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    overridePendingTransition(0,0);
                    return true;
                } else if (itemId == R.id.search) {
                    startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                    overridePendingTransition(0,0);
                    return true;
                } else if (itemId == R.id.flashlets) {
                    startActivity(new Intent(getApplicationContext(), FlashletList.class));
                    overridePendingTransition(0,0);
                    return true;
                } else if (itemId == R.id.stats) {
                    return true;
                }
                return false;
            }
        });

        TextView studyTime = findViewById(R.id.studyTime);
        Button pause = findViewById(R.id.startStopStudyTimer);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        if (accelerometer == null) {
            Log.e("Gyroscope", "No accelerometer sensor found");
        }

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!studyTimerRunning) {
                    studyTimerRunning = true;

                    // Prevents creating more timer instances
                    if (!wasStudyTimerRunning) {
                        runTimer();
                        wasStudyTimerRunning = true;
                    }
                } else {
                    studyTimerRunning = false;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float z = event.values[2]; // Acceleration in the Z-axis

            // If gyroscope is face down
            if (z < -9.0) {
                Log.d("Gyroscope", "Screen is face down");
                dimScreen();
            } else {
                restoreScreenBrightness();
            }
        }
    }

    // Required method for SensorEventListener
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    // Method to dim the screen
    private void dimScreen() {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.screenBrightness = 0; // Value range from 0 (dark) to 1 (bright)
        getWindow().setAttributes(params);
    }

    // Method to restore screen brightness
    private void restoreScreenBrightness() {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.screenBrightness = -1; // Use default brightness
        getWindow().setAttributes(params);
    }

    private void runTimer() {
        TextView studyTime = findViewById(R.id.studyTime);
        Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                int hours = studyDuration / 3600;
                int minutes = (studyDuration % 3600) / 60;
                int seconds = studyDuration % 60;

                // Formatted in Hours:Minutes:Seconds, with leading 0 if there is only 1 digit in the value
                String studyDurationFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
                studyTime.setText(studyDurationFormatted);

                Log.d("TAG", "run: " + AppLifecycleObserver.getAppInForeground() + AppLifecycleObserver.getScreenOn());
                if (!AppLifecycleObserver.getAppInForeground()) {
                    Log.d("TAG", "run: " + AppLifecycleObserver.getAppInForeground() + AppLifecycleObserver.getScreenOn());
                    if (AppLifecycleObserver.getScreenOn()) {
                        studyTimerRunning = false;
                        Log.d("Study", "run: " + studyTimerRunning);
                    }
                }
                if (studyTimerRunning) { studyDuration++; }

                handler.postDelayed(this, 1000);
            }
        });
    }
}