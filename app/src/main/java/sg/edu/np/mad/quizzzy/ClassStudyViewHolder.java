package sg.edu.np.mad.quizzzy;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ClassStudyViewHolder extends RecyclerView.ViewHolder {
    TextView username;
    TextView studyDuration;
    ImageView circleBackground;

    public ClassStudyViewHolder(View view) {
        super(view);

        // Locations found in class_study_item.xml
        username = view.findViewById(R.id.csUsername);
        studyDuration = view.findViewById(R.id.csStudyDuration);
        circleBackground = view.findViewById(R.id.circleBackground);
    }
}
