public class FIXMessageHeader {

    String beginString;
    int bodyLength;
    String messageType;

    public String getBeginString() {
        return beginString;
    }

    public void setBeginString(String beginString) {
        this.beginString = beginString;
    }

    public int getBodyLength() {
        return bodyLength;
    }

    public void setBodyLength(int bodyLength) {
        this.bodyLength = bodyLength;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String toString() {
        return String.format("BeginString=%s, BodyLength=%d, MessageType=%s", beginString, bodyLength, messageType) ;
    }
}
