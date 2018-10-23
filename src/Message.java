public class Message {
    private int maxSeenPid;
    private int senderPid;
    private Process senderProcess;
    private int explorePid;
    private int timeStamp;
    private MessageType type;

    public Message(int maxSeenPid, int senderPid, Process senderProcess, int explorePid, int timeStamp, MessageType type) {
        this.maxSeenPid = maxSeenPid;
        this.senderPid = senderPid;
        this.senderProcess = senderProcess;
        this.explorePid = explorePid;
        this.timeStamp = timeStamp;
        this.type = type;
    }

    public int getMaxSeenPid() {
        return maxSeenPid;
    }

    public void setMaxSeenPid(int maxSeenPid) {
        this.maxSeenPid = maxSeenPid;
    }

    public int getSenderPid() {
        return senderPid;
    }

    public void setSenderPid(int senderPid) {
        this.senderPid = senderPid;
    }

    public Process getSenderProcess() {
        return senderProcess;
    }

    public void setSenderProcess(Process senderProcess) {
        this.senderProcess = senderProcess;
    }

    public int getExplorePid() {
        return explorePid;
    }

    public void setExplorePid(int explorePid) {
        this.explorePid = explorePid;
    }

    public int getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(int timeStamp) {
        this.timeStamp = timeStamp;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }
}
