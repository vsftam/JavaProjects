# FIX Parser

## API

### FIXParser and FIXMessage

```agsl
byte[] input = "8=FIX.4.2\u00019=65\u000135=A\u0001108=30\u000110=031\u0001".getBytes();
FIXParser parser = new FIXParser();
FIXMessage message = parser.parse(input);
System.out.println(message);
```
prints
```agsl
Header: BeginString=FIX.4.2, BodyLength=65, MessageType=A
Body: 	108=30
CheckSum: 31
```


## Implemented features
1. Validates all tags are non-negative integers
1. Validates all fields are of format [int]=[string]
1. Validates value of bodyLength is a positive integer
1. Validates the positions of the following fields:
   1. BeginString (8) is the first tag
   1. BodyLength (9) is the second tag
   1. MsgType (35) is the third tag
   1. Checksum (10) is the last tag
1. Supports parsing of one occurrence of repeating groups in the FIX message. It assumes that the metadata for repeating groups are stored in FIXGroupType class. Instances of FIXGroupType are setup in FIXUtils. FIXGroupType class keeps track of the following:
   1. groupType - msgType that supports repeating group
   1. groupCountTag - tag indicating the counts in the repeating group
   1. firstEntryTag - first tag in the group to delimit the repeating groups
   1. groupTags - all the valid tags within the repeating group outside firstEntryTag
1. Validates groupCountTag is the first tag in the repeating group
1. Validates value of groupCountTag is a positive integer
1. Validates firstEntryTag is the first tag after group count tag

## Observation from different runs
1. The first run takes a relative long time >50MM ns, despite the short message length. This is likely due to one time initialization of the classes.
2. The parsing time is roughly proportional to message size, but at a slow rate.  ~40 items takes ~4MM ns, 10-20 items ~1MM ns, and <10 items ~0.5MM.

## Future improvements
1. Support multiple or nested repeating groups. They are omitted in current version for simplicity
1. Validate body length of the FIX message
1. Validate the checksum of the FIX message
1. Add some kind of validation for the body content. Currently, all fields of the body are stored in a map of type Map<Integer,String>.
1. Add unit tests for the implemented features
1. Add more proper benchmark tests on the parser performance



