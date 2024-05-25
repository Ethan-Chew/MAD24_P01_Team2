package sg.edu.np.mad.quizzzy.Models;

import java.util.ArrayList;

public class UserClass {
    String classId;
    String classTitle;
    ArrayList<String> creatorId;
    ArrayList<String> memberId;
    long lastUpdatedUnix;

    // Setters
    public void setClassId(String classId) {
        this.classId = classId;
    }
    public void setClassTitle(String classTitle) {
        this.classTitle = classTitle;
    }
    public void setCreatorID(ArrayList<String> creatorId) {
        this.creatorId = creatorId;
    }
    public void addMember(String id) {
        this.memberId.add(id);
    }
    public void removeMember(String id) {
        // Implement this
        this.memberId.remove(id);
    }


    // Getters
    public ArrayList<String> getMembers() { return this.memberId; }
    public String getClassTitle() { return this.classTitle; }
    public ArrayList<String> getCreators() { return this.creatorId; }

    public UserClass(String classId, String classTitle, ArrayList<String> creatorId, ArrayList<String> memberId, long lastUpdatedUnix) {
        this.classId = classId;
        this.classTitle = classTitle;
        this.creatorId = creatorId;
        this.memberId = memberId;
        this.lastUpdatedUnix = lastUpdatedUnix;
    }
}
