import enums.MessageType;
import enums.Status;

import java.util.ArrayList;
import java.util.Objects;

public class Process implements Runnable {

    private int pid;
    private Process parent;
    private ArrayList<Channel> myChannels;
    private volatile boolean startNextRound;
    private int round;
    private volatile boolean leaderElected;
    private Status status;
    private int maxPid;
    private int pendingAcks;
    private boolean ackToParent;

    public Process(int pid) {
        this.pid = pid;
        this.myChannels = new ArrayList<>();
        this.round = 1;
        this.status = Status.UNKNOWN;
        this.maxPid = this.pid;
    }

    @Override
    public void run() {
        while (true) {
            waitForMasterSync(20);

            ArrayList<Message> allMessages = new ArrayList<>();

//            for (Channel neighborChannel : neighbors) {
//                allMessages.addAll(neighborChannel.read());
//                neighborChannel.purge();
//            }

            for (Channel eachMessage : myChannels) {
                allMessages.addAll(eachMessage.read());
                eachMessage.purge();
            }

            isLeaderMessagePresent(allMessages);

            if (!leaderElected) {
                FloodMax(allMessages);
            }

            if (leaderElected) {
                break;
            }

        }

    }

    private void isLeaderMessagePresent(ArrayList<Message> allMessages) {
        for(Message message: allMessages){
            if(message.getType().equals(MessageType.LEADER_DECLARATION)){
                this.status = Status.NON_LEADER;
                this.leaderElected = true;
                this.parent = message.getSenderProcess();
                broadcast(message);
                return;
            }
        }
    }

    private void FloodMax(ArrayList<Message> allMessages) {
        transition(allMessages);
    }

    private void transition(ArrayList<Message> allMessages) {
        if(round == 1){
            Message messageToSend = new Message(maxPid, this.pid, this,
                    0, 0, MessageType.EXPLORE);
            broadcast(messageToSend);
            pendingAcks = myChannels.size();
        } else {
            for (Message message : allMessages) {

                switch (message.getType()) {
                    case EXPLORE:
                        if (message.getMaxSeenPid() > maxPid) {
                            maxPid = message.getMaxSeenPid();
                            this.parent = message.getSenderProcess();
                            Message messageToSend = new Message(maxPid, this.pid, this,
                                   this.parent.pid, 0, MessageType.EXPLORE);
                            broadcast(messageToSend);
                            pendingAcks = this.parent == null? myChannels.size(): myChannels.size() -1;
                            ackToParent = false;
                        } else if (message.getMaxSeenPid() <= maxPid) {
                            Message nackMessage = new Message(maxPid, this.pid, this,
                                    message.getExplorePid(), 0, MessageType.NACK);
                            sendMessage(message.getSenderProcess(), nackMessage);
                        }
                        break;
                    case NACK:
                    case ACK:
                        // Means no one responded to my
                        if(this.parent!=null && this.parent.pid == message.getExplorePid()
                                || this.parent==null && message.getExplorePid() ==0) {
                            this.pendingAcks--;
                        }

                        // If I have received all the acks
                        if (pendingAcks == 0 && this.parent != null && status.equals(Status.UNKNOWN) && !ackToParent) {
                            Message ackMessage = new Message(maxPid, this.pid, this,
                                    this.parent.pid, 0, MessageType.ACK);
                            sendMessage(this.parent, ackMessage);
                            ackToParent = true;
                        }
                        break;
                }
            }

            if (pendingAcks == 0 && this.parent == null && status.equals(Status.UNKNOWN) && !leaderElected) {
                this.status = Status.LEADER;
                this.leaderElected = true;
                System.out.println("Leader Election Completed! Leader is -> " + pid);
                Message messageToSend = new Message(maxPid, this.pid, this,
                        0, 0, MessageType.LEADER_DECLARATION);
                broadcast(messageToSend);

            }
        }

        round++;

        this.setStartNextRound(false);
    }

    private Channel getChannel(int processId) {
        for (Channel eachChannel: myChannels) {
                if (eachChannel.getProcess().pid == processId) {
                    return eachChannel;
                }
            }
            return null;
    }


    private void broadcast(Message message) {
        for (Channel eachChannel : myChannels) {
            if (!eachChannel.getProcess().equals(parent)) {
                sendMessage(eachChannel.getProcess(), message);
            }
        }

    }

    private void sendMessage(Process process, Message message) {
        if(message!=null) {
            System.out.println(Thread.currentThread().getName()+" Round: " + round + ", Sending " + message.getType() + " from " + message.getSenderPid() + " to -> " + process.pid);
            process.addMessage(message);
        }
    }

    private void addMessage(Message message) {
        Channel channel = getChannel(message.getSenderPid());
        if(channel!=null){
            channel.add(message);
        }
    }


    private void waitForMasterSync(int time) {
        while (!isStartNextRound()) {
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    // creating a channel to the new process from this process
    public void addNeighbor(Process process) {
        this.myChannels.add(new Channel(process));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Process process = (Process) o;
        return pid == process.pid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pid);
    }

    public void setStartNextRound(boolean startNextRound) {
        this.startNextRound = startNextRound;
    }

    public boolean isStartNextRound() {
        return startNextRound;
    }
}
