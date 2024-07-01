package sg.edu.np.mad.quizzzy.Search.Recycler;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import sg.edu.np.mad.quizzzy.Models.RecyclerViewInterface;
import sg.edu.np.mad.quizzzy.Models.SQLiteRecentSearchesManager;
import sg.edu.np.mad.quizzzy.R;

public class RecentSearchesAdapter extends RecyclerView.Adapter<RecentSearchesViewHolder> {
    private final RecyclerViewInterface recyclerViewInterface;
    private OnEmptyListener onEmptyListener;

    private ArrayList<String> recentSearches;
    private SQLiteRecentSearchesManager db;

    public interface OnEmptyListener {
        void isRecentsEmpty(Boolean isEmpty);
    }

    public RecentSearchesAdapter(RecyclerViewInterface recyclerViewInterface, ArrayList<String> recentSearches, OnEmptyListener onEmptyListener) {
        this.recyclerViewInterface = recyclerViewInterface;
        this.recentSearches = recentSearches;
        this.onEmptyListener = onEmptyListener;
    }

    @NonNull
    @Override
    public RecentSearchesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_searches_recycler_adapter, parent, false);
        db = SQLiteRecentSearchesManager.instanceOfDatabase(parent.getContext());

        return new RecentSearchesViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentSearchesViewHolder holder, int position) {
        String listItem = recentSearches.get(position);

        // Set Text of Elements on the UI
        holder.titleLabel.setText(listItem);

        // Handle onClick of Remove Item
        holder.removeSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.dropSearchQuery(listItem);
                recentSearches.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
                notifyItemRangeChanged(holder.getAdapterPosition(), getItemCount());

                if (recentSearches.size() == 0) {
                    onEmptyListener.isRecentsEmpty(true);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return recentSearches.size();
    }
}