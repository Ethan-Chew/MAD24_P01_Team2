package sg.edu.np.mad.quizzzy;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
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

import sg.edu.np.mad.quizzzy.Flashlets.CreateFlashlet;
import sg.edu.np.mad.quizzzy.Flashlets.FlashletList;
import sg.edu.np.mad.quizzzy.Models.SQLiteManager;

import java.util.HashMap;

public class StatisticsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_statistics);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Configure Back Button
        Toolbar toolbar = findViewById(R.id.statsToolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Configure Bottom Navigation Bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.create);
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
                } else if (itemId == R.id.create) {
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

        SQLiteManager localDB = new SQLiteManager(this);
        // Get data from DB and call function to calculate all statistics
        HashMap<String, Integer> statistics = localDB.calculateStatistics();

        // Get all statistics from statistics HashMap
        int todayFlashcardUsage = statistics.get("todayFlashcardUsage");
        int todayFlashletUsage = statistics.get("todayFlashletUsage");
        int todayClassUsage = statistics.get("todayClassUsage");

        int totalFlashcardUsage = statistics.get("totalFlashcardUsage");
        int averageFlashcardUsage = statistics.get("averageFlashcardUsage");

        int totalFlashletUsage = statistics.get("totalFlashletUsage");
        int averageFlashletUsage = statistics.get("averageFlashletUsage");

        int totalClassUsage = statistics.get("totalClassUsage");
        int averageClassUsage = statistics.get("averageClassUsage");

        // Update text details into relevant TextViews
        TextView todayTotal = findViewById(R.id.statsTodayTotal);
        todayTotal.setText(String.valueOf(todayFlashcardUsage + todayFlashletUsage + todayClassUsage));

        TextView todayFlashcard = findViewById(R.id.statsTodayFlashcard);
        TextView todayFlashlet = findViewById(R.id.statsTodayFlashlet);
        TextView todayClass = findViewById(R.id.statsTodayClass);

        todayFlashcard.setText(String.valueOf(todayFlashcardUsage));
        todayFlashlet.setText(String.valueOf(todayFlashletUsage));
        todayClass.setText(String.valueOf(todayClassUsage));

        TextView weekTotal = findViewById(R.id.statsWeekTotal);
        TextView weekAverage = findViewById(R.id.statsWeekAverage);

        weekTotal.setText(String.valueOf(totalFlashcardUsage + totalFlashletUsage + totalClassUsage));
        weekAverage.setText(String.valueOf(averageFlashcardUsage + averageFlashletUsage + averageClassUsage));

        TextView weekFlashcardTotal = findViewById(R.id.statsWeekTotalFlashcard);
        TextView weekFlashletTotal = findViewById(R.id.statsWeekTotalFlashlet);
        TextView weekClassTotal = findViewById(R.id.statsWeekTotalClass);

        weekFlashcardTotal.setText(String.valueOf(totalFlashcardUsage));
        weekFlashletTotal.setText(String.valueOf(totalFlashletUsage));
        weekClassTotal.setText(String.valueOf(totalClassUsage));

        TextView weekFlashcardAvg = findViewById(R.id.statsWeekAvgFlashcard);
        TextView weekFlashletAvg = findViewById(R.id.statsWeekAvgFlashlet);
        TextView weekClassAvg = findViewById(R.id.statsWeekAvgClass);

        weekFlashcardAvg.setText(String.valueOf(averageFlashcardUsage));
        weekFlashletAvg.setText(String.valueOf(averageFlashletUsage));
        weekClassAvg.setText(String.valueOf(averageClassUsage));
    }
}