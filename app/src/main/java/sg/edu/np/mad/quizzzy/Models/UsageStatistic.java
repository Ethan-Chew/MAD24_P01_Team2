package sg.edu.np.mad.quizzzy.Models;

public class UsageStatistic {
    long startTime;
    long timeElapsed;
    boolean activityChanged;

    // Setters
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
    public void setTimeElapsed(long timeElapsed) {
        this.timeElapsed = timeElapsed;
    }
    public void setActivityChanged(boolean activityChanged) { this.activityChanged = activityChanged; };

    // Getters
    public long getStartTime() { return this.startTime; }
    public long getTimeElapsed() { return this.timeElapsed; }
    public boolean getActivityChanged() { return activityChanged; };

    // Methods
    // Stores total time spent on activity and updates SQLite DB
    public void updateTimeData() {
        // Resets timeElapsed before updating
        timeElapsed = 0;
        timeElapsed = (System.currentTimeMillis() - startTime) / 1000;
        startTime = System.currentTimeMillis();
    }

    // Constructor
    public UsageStatistic() {
        this.startTime = System.currentTimeMillis();
        this.timeElapsed = 0;
        this.activityChanged = false;
    }
}
