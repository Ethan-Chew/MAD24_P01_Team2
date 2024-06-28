package sg.edu.np.mad.quizzzy.Search;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import sg.edu.np.mad.quizzzy.Flashlets.FlashletList;
import sg.edu.np.mad.quizzzy.HomeActivity;
import sg.edu.np.mad.quizzzy.Models.RecyclerViewInterface;
import sg.edu.np.mad.quizzzy.Models.SQLiteRecentSearchesManager;
import sg.edu.np.mad.quizzzy.R;
import sg.edu.np.mad.quizzzy.Search.Recycler.RecentSearchesAdapter;
import sg.edu.np.mad.quizzzy.StatisticsActivity;

public class SearchActivity extends AppCompatActivity implements RecyclerViewInterface {

    // Search Result Items
    private TabLayout searchResultTabs;
    private ViewPager2 searchResultViewPager;
    private LinearLayout noRecentsContainer;
    private RecyclerView recentsContainer;
    private SearchAdapter searchAdapter;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.aSConstrainLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Bottom Navigation View
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.search);
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
                    return true;
                } else if (itemId == R.id.flashlets) {
                    startActivity(new Intent(getApplicationContext(), FlashletList.class));
                    overridePendingTransition(0,0);
                    return true;
                } else if (itemId == R.id.stats) {
                    startActivity(new Intent(getApplicationContext(), StatisticsActivity.class));
                    overridePendingTransition(0,0);
                    return true;
                }
                return false;
            }
        });

        // Initialise SQLite Database
        SQLiteRecentSearchesManager localDB = SQLiteRecentSearchesManager.instanceOfDatabase(SearchActivity.this);
        ArrayList<String> recentSearches = localDB.getSearchQueries();

        /// Hide Search Result Container
        searchResultTabs.setVisibility(View.GONE);
        searchResultViewPager.setVisibility(View.GONE);

        /// Display list of Recents or 'No Recent Searches'
        noRecentsContainer = findViewById(R.id.aSNoRecentsList);
        recentsContainer = findViewById(R.id.aSRecentsRecyclerView);
        if (recentSearches.isEmpty()) {
            recentsContainer.setVisibility(View.GONE);
        } else {
            noRecentsContainer.setVisibility(View.GONE);
        }

        // Handle Search View Searches
        searchView = findViewById(R.id.aSSearchField);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!recentSearches.contains(query)) {
                    localDB.addSearchQueries(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // Handle Search Result Pages
        searchResultTabs = findViewById(R.id.aSResultsTabBar);
        searchResultViewPager = findViewById(R.id.aSResultsViewPager);

        FragmentManager fragmentManager = getSupportFragmentManager();
        searchAdapter = new SearchAdapter(fragmentManager, getLifecycle());
        searchResultViewPager.setAdapter(searchAdapter);

        searchResultTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                searchResultViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        searchResultViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                searchResultTabs.selectTab(searchResultTabs.getTabAt(position));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Update Recent RecyclerView with Items
        SQLiteRecentSearchesManager localDB = SQLiteRecentSearchesManager.instanceOfDatabase(SearchActivity.this);
        ArrayList<String> recentSearches = localDB.getSearchQueries();

        recentsContainer = findViewById(R.id.aSRecentsRecyclerView);
        RecentSearchesAdapter searchesAdapter = new RecentSearchesAdapter(SearchActivity.this, recentSearches);
        LinearLayoutManager layoutManager = new LinearLayoutManager(SearchActivity.this);
        recentsContainer.setLayoutManager(layoutManager);
        recentsContainer.setItemAnimator(new DefaultItemAnimator());
        recentsContainer.setAdapter(searchesAdapter);
    }

    // Handle Recent RecyclerView Item onClick
    @Override
    public void onItemClick(int position) {
        // TODO
    }
}