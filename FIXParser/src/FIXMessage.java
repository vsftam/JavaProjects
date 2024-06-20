import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FIXMessage {

    FIXMessageHeader header;

    Map<Integer, String> tagValueMap = new HashMap<>();
    Map<Integer, List<Map<Integer, String>>> groups = new HashMap<>();

    private int checkSum = -1;

    public String getMessageType() {
        return getHeader().getMessageType();
    }

    public FIXMessageHeader getHeader() {
        return header;
    }

    public boolean addTagValuePair(int tag, String value, boolean hasNext) {
        if(tag == 10) {
            if(hasNext) {
                System.out.println("Already reach checksum but still has remaining fields");
                return false;
            }
            else {
                int checkSum = -1;
                try {
                    checkSum = Integer.parseInt(value); }
                catch(NumberFormatException nfe) {
                    System.out.printf("Cannot parse checkSum for value: %s\n", value);
                    return false;
                }
                setCheckSum(checkSum);
            }
        }
        else
            tagValueMap.put(tag, value);
        return true;
    }

    public void addGroupEntry(int groupCountTag, int groupEntryIdx, int groupEntryTag, String groupEntryValue) {
        List<Map<Integer, String>> groupList = null;
        if (groups.containsKey(groupCountTag)) {
            groupList = groups.get(groupCountTag);
        } else {
            groupList = new ArrayList<>();
            groups.put(groupCountTag, groupList);
        }

        if (groupEntryIdx >= groupList.size()) {
            Map<Integer, String> group = new HashMap<>();
            group.put(groupEntryTag, groupEntryValue);
            groupList.add(groupEntryIdx, group);
        } else {
            Map<Integer, String> group = groupList.get(groupEntryIdx);
            group.put(groupEntryTag, groupEntryValue);
        }
    }

    public int getTagValueMapSize() {
        return tagValueMap.size();
    }

    public int getGroupSize() {
        List<Map<Integer, String>> ls = new ArrayList<>();
        groups.values().forEach(ls::addAll);
        return ls.size();
    }

    public int getCheckSum() {
        return checkSum;
    }

    public void setHeader(FIXMessageHeader header) {
        this.header = header;
    }

    public void setCheckSum(int checkSum) {
        this.checkSum = checkSum;
    }

    public String groupString() {
        String groupStr = "";
        if(groups.size() > 0) {
            groupStr = groups.entrySet().stream().map(
                    entry -> String.format("groupCountTag=%d", entry.getKey()) +
                            entry.getValue().stream().map( listEntry ->
                                    listEntry.entrySet().stream().map(
                                            mapEntry ->String.format("\t%d=%s", mapEntry.getKey(), mapEntry.getValue())
                                    ).reduce( "", (a, b) -> a+b)
                            ).reduce( "", (a, b) -> a+"\n"+b)
            ).reduce( "", (a, b) -> a+"\n"+b);
        };
        return groupStr;
    }

    public String bodyString() {
        return tagValueMap.entrySet().stream().map(
                entry -> String.format("%d=%s", entry.getKey(), entry.getValue()))
                        .reduce( "", (a, b) -> a+"\t"+b);
    }
    public String toString() {
        String headerStr = "Header: "+ header.toString();
        String checksumStr = String.format("\nCheckSum: %d\n", checkSum);
        String bodyStr = bodyString() == "" ? "" : "\nBody: " + bodyString();
        String groupStr = groupString() == "" ? "" : "\nGroups: " + groupString();
        return headerStr + bodyStr +  groupStr + checksumStr;
    }
}
