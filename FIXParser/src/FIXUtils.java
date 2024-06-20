import java.util.*;

public class FIXUtils {

    private static Map<String, FIXGroupType> groupTypes = new HashMap<>();

    static {
        Set<Integer> groupTags = new HashSet<>(Arrays.asList(803, 804));
        FIXGroupType dummyGroupType1 = new FIXGroupType("dummyGroup1", 801, 802, groupTags);
        groupTypes.put("800", dummyGroupType1);

        groupTags = new HashSet<>(Arrays.asList(903, 904));
        FIXGroupType dummyGroupType2 = new FIXGroupType("dummyGroup1", 901, 902, groupTags);
        groupTypes.put("900", dummyGroupType2);
    }

    public static FIXGroupType getGroupType(String msgTag) {
        return groupTypes.get(msgTag);
    }
}
