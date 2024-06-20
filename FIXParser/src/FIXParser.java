import java.io.*;

import java.util.Scanner;
import java.util.Set;

public class FIXParser {

    public static final String FIELD_DELIM = "\u0001";
    public static final String TOKEN_DELIM = "=";
    public FIXMessage parse(byte[] input) {

        InputStream inputStream = new ByteArrayInputStream(input);
        Scanner scanner = new Scanner(inputStream);
        scanner.useDelimiter(FIELD_DELIM);

        FIXMessage message = new FIXMessage();

        int count = 1;
        while(scanner.hasNext()) {
            String field = scanner.next();
            if(!parseField(field, count, message, scanner)) {
                System.out.printf("Cannot parse field %s successfully at count %d\n", field, count);
                break;
            }
            count++;
        }

        if(message.getCheckSum() == -1) {
            System.out.println("Check sum field is missing");
            return message;
        }

        return message;
    }


    private boolean parseField(String field, int count, FIXMessage message, Scanner scanner) {
        String[] tokens = getTokens(field);
        if (tokens == null) return false;
        int tag =-1;
        try {
            tag = Integer.parseInt(tokens[0]);
        }
        catch(NumberFormatException nfe) {
            System.out.printf("Cannot parse tag for field: %s\n", field);
            return false;
        }
        if(tag < 0) {
            System.out.printf("Invalid tag: %d\n", tag);
            return false;
        }
        if(count == 1) {
            if(tag == 8) {
                FIXMessageHeader header = new FIXMessageHeader();
                header.setBeginString(tokens[1]);
                message.setHeader(header);
            }
            else {
                System.out.printf("Incorrect tag for the first field: %s\n", field);
                return false;
            }
        }
        else if(count == 2) {
            if(tag == 9) {
                int bodyLength = -1;
                try {
                    bodyLength = Integer.parseInt(tokens[1]); }
                catch(NumberFormatException nfe) {
                    System.out.printf("Cannot parse bodyLength for field: %s\n", field);
                    return false;
                }
                if(bodyLength <= 0) {
                    System.out.printf("Invalid bodyLength: %d\n", bodyLength);
                    return false;
                }
                FIXMessageHeader header = message.getHeader();
                header.setBodyLength(bodyLength);
            }
            else {
                System.out.printf("Incorrect tag for the second field: %s\n", field);
                return false;
            }
        }
        else if(count == 3) {
            if(tag == 35) {
                FIXMessageHeader header = message.getHeader();
                header.setMessageType(tokens[1]);
            }
            else {
                System.out.printf("Incorrect tag for the third field: %s\n", field);
                return false;
            }
        }
        else {
                FIXGroupType groupType = FIXUtils.getGroupType(message.getMessageType());
                // logic for repeating groups
                if(groupType != null && groupType.getAllValidTags().contains(tag)) {
                    int groupCountTag = groupType.getGroupCountTag();
                    if(tag != groupCountTag) {
                        System.out.printf("Expecting group count tag %d for msgType %s, but getting: %d\n", groupCountTag, message.getMessageType(), tag);
                        return false;
                    }
                    int groupCount = -1;
                    try {
                        groupCount = Integer.parseInt(tokens[1]); }
                    catch(NumberFormatException nfe) {
                        System.out.printf("Cannot parse groupCount for field: %s\n", field);
                        return false;
                    }
                    if(groupCount <= 0) {
                        System.out.printf("Invalid group count %d\n", groupCount);
                        return false;
                    }

                    // first message
                    String groupFirstField = scanner.next();
                    String[] groupFirstTokens = getTokens(groupFirstField);
                    if (groupFirstTokens == null) return false;

                    int groupFirstTag =-1;
                    try {
                        groupFirstTag = Integer.parseInt(groupFirstTokens[0]);
                    }
                    catch(NumberFormatException nfe) {
                        System.out.printf("Cannot parse tag for field: %s\n", groupFirstField);
                        return false;
                    }
                    if(groupFirstTag != groupType.getFirstTag()) {
                        System.out.printf("Expecting first group tag %d for repeating group with type %s, but getting %d\n", groupType.getFirstTag(), message.getMessageType(), groupFirstTag);
                        return false;
                    }

                    int groupEntryIdx = 0;
                    message.addGroupEntry(groupCountTag, groupEntryIdx, groupFirstTag, groupFirstTokens[1]);

                    Set<Integer> groupTags = groupType.getGroupTags();
                    while(groupEntryIdx < groupCount) {
                        String nextField = scanner.next();
                        String[] nextTokens = getTokens(nextField);
                        if (nextTokens == null) return false;
                        int nextTag =-1;
                        try {
                            nextTag = Integer.parseInt(nextTokens[0]);
                        }
                        catch(NumberFormatException nfe) {
                            System.out.printf("Cannot parse tag for field: %s\n", nextField);
                            return false;
                        }
                        if(nextTag < 0) {
                            System.out.printf("Invalid nextTag: %d\n", nextTag);
                            return false;
                        }
                        while(nextTag != groupType.getFirstTag() && groupTags.contains(nextTag)) {
                            message.addGroupEntry(groupCountTag, groupEntryIdx, nextTag, nextTokens[1]);

                            nextField = scanner.next();
                            nextTokens = getTokens(nextField);
                            if (nextTokens == null) return false;
                            try {
                                nextTag = Integer.parseInt(nextTokens[0]);
                            }
                            catch(NumberFormatException nfe) {
                                System.out.printf("Cannot parse tag for field: %s\n", nextField);
                                return false;
                            }
                            if(nextTag < 0) {
                                System.out.printf("Invalid nextTag: %d\n", nextTag);
                                return false;
                            }
                        }
                        if(nextTag == groupType.getFirstTag()) {
                            groupEntryIdx++;
                            message.addGroupEntry(groupCountTag, groupEntryIdx, nextTag, nextTokens[1]);
                        }
                        else {
                            boolean ret = message.addTagValuePair(nextTag, nextTokens[1], scanner.hasNext());
                            if(!ret)
                                return ret;
                            else
                                break;
                        }
                    }
                }
                else {
                    boolean ret = message.addTagValuePair(tag, tokens[1], scanner.hasNext());
                    if(!ret) return ret;
                }
            }
        return true;
    }

    private static String[] getTokens(String field) {
        String[] tokens = field.split(TOKEN_DELIM);
        if(tokens.length != 2) {
            System.out.printf("Incorrect format for field: %s\n", field);
            return null;
        }
        return tokens;
    }


    public static void main(String[] args) {

        byte[] input = "8=FIX.4.2\u00019=65\u000135=A\u0001108=30\u000110=031\u0001".getBytes();

        byte[] input2 = "8=FIX.4.2\u00019=178\u000135=8\u000149=PHLX\u000156=PERS\u000152=20071123-05:30:00.000\u000111=ATOMNOCCC9990900\u000120=3\u0001150=E\u000139=E\u000155=MSFT\u0001167=CS\u000154=1\u000138=15\u000140=2\u000144=15\u000158=PHLX EQUITY TESTING\u000159=0\u000147=C\u000132=0\u000131=0\u0001151=15\u000114=0\u00016=0\u000110=128\u0001".getBytes();

        // repeating group with dummy usageTag 800, groupCountTag 801 and groupFirstTag 802, other tags as 803, 804
        byte[] input3 = "8=FIX.4.2\u00019=65\u000135=800\u0001801=3\u0001802=DummyGroup1a\u0001803=Party1a\u0001804=7\u0001802=DummyGroup1b\u0001803=Party1b\u0001804=2000\u0001802=DummyGroup1c\u0001803=Party1a\u0001804=50.5\u0001108=30\u000110=031\u0001".getBytes();

        FIXParser parser = new FIXParser();
        FIXMessage message = parser.parse(input3);

        int s = message.getTagValueMapSize();
        System.out.printf("FIX Message with body size %d\n\n%s\n", s, message);
    }
}

