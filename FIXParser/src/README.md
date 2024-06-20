# FIX Parser

## API

### FIXParser and FIXMessage

```agsl
    byte[] input = "8=FIX.4.2\u00019=65\u000135=A\u0001108=30\u000110=031\u0001".getBytes();
    FIXParser parser = new FIXParser();
    FIXMessage message = parser.parse(input);
    System.out.println(message);
```

## Assumptions
1. Validates all tags are non-negative integers
1. Validates all fields are of format [int]=[string]
1. Validates value of bodyLength values is an positive integer
1. Validates the positions of the following fields:
   1. BeginString (8) is the first tag
   1. BodyLength (9) is the second tag
   1. MsgType (35) is the third tag
   1. Checksum (10) is the last tag
1. Supports parsing of repeating groups. It assumes that the metadata for repeating groups are stored in FIXGroupType class. Instances of FIXGroupType are setup in FIXUtils. FIXGroupType class keeps track of the following:
   1. groupType - type that supports repeating group
   1. groupCountTag - tag indicating the counts in the repeating group
   1. firstEntryTag - first tag in the group to delimit the repeating groups
   1. groupTags - all the valid tags within the repeating group outside firstEntryTag
1. Validates groupCountTag is the first tag in the repeating group
1. Validates value of groupCountTag is a positive integer
1. Validates firstEntryTag is the first tag after group count tag
1. Does not support nested repeating group for simplicity
1. Does not validate body length of the FIX message
1. Does not validate the checksum of the FIX message
1. Does not do any validation for the body content. All fields of the body are stored in tagValueMap of type Map<Integer,String>.



