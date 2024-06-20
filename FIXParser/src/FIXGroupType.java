import java.util.Set;

public class FIXGroupType {

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public Set<Integer> getAllValidTags() {
        Set<Integer> validTags = groupTags;
        validTags.add(groupCountTag);
        validTags.add(firstTag);
        return validTags;
    }

    public int getGroupCountTag() {
        return groupCountTag;
    }

    public void setGroupCountTag(int groupCountTag) {
        this.groupCountTag = groupCountTag;
    }

    public int getFirstTag() {
        return firstTag;
    }

    public void setFirstTag(int firstTag) {
        this.firstTag = firstTag;
    }

    public Set<Integer> getGroupTags() {
        return groupTags;
    }

    public void setGroupTags(Set<Integer> groupTags) {
        this.groupTags = groupTags;
    }
    public FIXGroupType(String groupType, int groupCountTag, int firstTag, Set<Integer> groupTags) {
        this.groupType = groupType;
        this.groupCountTag = groupCountTag;
        this.firstTag = firstTag;
        this.groupTags = groupTags;
    }

    private String groupType;

    private int groupCountTag;

    private int firstTag;

    // other groupTags except firstTag
    private Set<Integer> groupTags;

}
