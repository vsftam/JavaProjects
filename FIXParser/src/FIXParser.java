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

        String input1s = "8=FIX.4.2\u00019=65\u000135=A\u0001108=30\u000110=031\u0001";
        byte[] input1 = input1s.getBytes();

        String input2s = "8=FIX.4.2\u00019=65\u000135=A\u0001108=30\u0001109=31\u000110=031\u0001";
        byte[] input2 = input2s.getBytes();

        String input3s = "8=FIX.4.2\u00019=178\u000135=8\u000149=PHLX\u000156=PERS\u000152=20071123-05:30:00.000\u000111=ATOMNOCCC9990900\u000120=3\u0001150=E\u000139=E\u000155=MSFT\u0001167=CS\u000154=1\u000138=15\u000140=2\u000144=15\u000158=PHLX EQUITY TESTING\u000159=0\u000147=C\u000132=0\u000131=0\u0001151=15\u000114=0\u00016=0\u000110=128\u0001";
        byte[] input3 = input3s.getBytes();

        // repeating group with dummy usageTag 800, groupCountTag 801, count of 3 and groupFirstTag 802, other tags as 803, 804
        String input4s = "8=FIX.4.2\u00019=65\u000135=800\u0001801=3\u0001802=DummyGroup1a\u0001803=Party1a\u0001804=7\u0001802=DummyGroup1b\u0001803=Party1b\u0001804=2000\u0001802=DummyGroup1c\u0001803=Party1a\u0001804=50.5\u0001108=30\u0001109=31\u000110=031\u0001";
        byte[] input4 = input4s.getBytes();

        // repeating group with dummy usageTag 900, groupCountTag 901, count of 10 and groupFirstTag 902, other tags as 903, 904, 905
        String input5s = "8=FIX.4.2\u00019=65\u000135=900\u0001901=10\u0001902=DummyGroup2a\u0001903=Party2a\u0001904=7\u0001905=22\u0001902=DummyGroup2b\u0001903=Party2b\u0001904=17\u0001905=21\u0001902=DummyGroup2c\u0001903=Party2c\u0001904=27\u0001905=212\u0001902=DummyGroup2d\u0001903=Party2d\u0001904=37\u0001905=222\u0001902=DummyGroup2e\u0001903=Party2e\u0001904=47\u0001905=2112\u0001902=DummyGroup2f\u0001903=Party2f\u0001904=57\u0001905=2222\u0001902=DummyGroup2g\u0001903=Party2g\u0001904=67\u0001905=22122\u0001902=DummyGroup2h\u0001903=Party2h\u0001904=77\u0001905=22222\u0001902=DummyGroup2i\u0001903=Party2i\u0001904=87\u0001905=212112\u0001902=DummyGroup2j\u0001903=Party2j\u0001904=97\u0001905=222212\u0001108=32\u0001109=999\u000110=037\u0001";
        byte[] input5 = input5s.getBytes();

        FIXParser parser = new FIXParser();

        long start1 = System.nanoTime();
        FIXMessage message1 = parser.parse(input1);
        long end1 = System.nanoTime();

        System.out.printf("Parsing FIX message\n[%s] to \n%s", input1s, message1);
        System.out.printf("Body size=%d, group size=%d\n", message1.getTagValueMapSize(), message1.getGroupSize());
        System.out.println("Elapsed Time is nano seconds: "+ (end1-start1) + "\n");

        long start2 = System.nanoTime();
        FIXMessage message2 = parser.parse(input2);
        long end2 = System.nanoTime();

        System.out.printf("Parsing FIX message\n[%s] to \n%s", input2s, message2);
        System.out.printf("Body size=%d, group size=%d\n", message2.getTagValueMapSize(), message2.getGroupSize());
        System.out.println("Elapsed Time is nano seconds: "+ (end2-start2) + "\n");

        long start3 = System.nanoTime();
        FIXMessage message3 = parser.parse(input3);
        long end3 = System.nanoTime();

        System.out.printf("Parsing FIX message\n[%s] to \n%s", input3s, message3);
        System.out.printf("Body size=%d, group size=%d\n", message3.getTagValueMapSize(), message3.getGroupSize());
        System.out.println("Elapsed Time is nano seconds: "+ (end3-start3) + "\n");

        long start4 = System.nanoTime();
        FIXMessage message4 = parser.parse(input4);
        long end4 = System.nanoTime();

        System.out.printf("Parsing FIX message\n[%s] to \n%s", input4s, message4);
        System.out.printf("Body size=%d, group size=%d\n", message4.getTagValueMapSize(), message4.getGroupSize());
        System.out.println("Elapsed Time is nano seconds: "+ (end4-start4) + "\n");

        long start5 = System.nanoTime();
        FIXMessage message5 = parser.parse(input5);
        long end5 = System.nanoTime();

        System.out.printf("Parsing FIX message\n[%s] to \n%s", input5s, message5);
        System.out.printf("Body size=%d, group size=%d\n", message5.getTagValueMapSize(), message5.getGroupSize());
        System.out.println("Elapsed Time is nano seconds: "+ (end5-start5) + "\n");
    }
}

