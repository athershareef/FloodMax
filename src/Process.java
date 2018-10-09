import enums.MessageType;
import enums.Status;

import java.util.ArrayList;
import java.util.Objects;

public class Process implements Runnable {

    private int pid;
    private Process parent;
    private ArrayList<Channel> neighbors;
    private volatile boolean startNextRound;
    private int round;
    private volatile boolean terminated;
    private volatile boolean leaderElected;
    private Status status;
    private int maxPid;
    private int pendingAcks;

    public Process(int pid) {
        this.pid = pid;
        this.neighbors = new ArrayList<>();
        this.startNextRound = false;
        this.round = 1;
        this.terminated = false;
        this.leaderElected = false;
        this.status = Status.UNKNOWN;
        this.maxPid = this.pid;
    }

    @Override
    public void run() {
        while (true) {
            waitForMasterSync(20);

            ArrayList<Message> allMessages = new ArrayList<>();

            for (Channel neighborChannel : neighbors) {
                allMessages.addAll(neighborChannel.read());
                neighborChannel.purge();
            }

            isLeaderMessagePresent(allMessages);

            if (!terminated) {
                FloodMax(allMessages);
            }


            if (leaderElected) {
                this.terminated = true;
                break;
            }

        }

    }

    private void isLeaderMessagePresent(ArrayList<Message> allMessages) {
        for(Message message: allMessages){
            if(message.getType().equals(MessageType.LEADER_DECLARATION)){
                this.status = Status.NON_LEADER;
                this.leaderElected = true;
                this.terminated = true;
                this.parent = message.getSenderProcess();
                broadcast(message);
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
            pendingAcks = neighbors.size();
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
                            pendingAcks = neighbors.size();
                        } else if (message.getMaxSeenPid() <= maxPid) {
                            Message nackMessage = new Message(maxPid, this.pid, this,
                                    message.getExplorePid(), 0, MessageType.NACK);
                            sendMessage(getChannel(message.getSenderProcess()), nackMessage);
                        }
                        break;
                    case NACK:
                    case ACK:
                        this.pendingAcks--;

                        // If I have received all the acks
                        if (pendingAcks == 0 && this.parent != null && status.equals(Status.UNKNOWN)) {
                            Message ackMessage = new Message(maxPid, this.pid, this,
                                    this.parent.pid, 0, MessageType.ACK);
                            sendMessage(getChannel(parent), ackMessage);
                        }
                        break;
                }
            }

            if (pendingAcks == 0 && this.parent == null && status.equals(Status.UNKNOWN) && !terminated &&!leaderElected) {
                this.status = Status.LEADER;
                this.leaderElected = true;
                this.terminated = true;
                System.out.println("Leader Election Completed! Leader is -> " + pid);
                Message messageToSend = new Message(maxPid, this.pid, this,
                        0, 0, MessageType.LEADER_DECLARATION);
                broadcast(messageToSend);

            }
        }

        round++;

        this.setStartNextRound(false);
    }

    private Channel getChannel(Process senderProcess) {
        for (Channel channel : neighbors) {
            for(Channel each: channel.getProcess().getNeighbors()){
                if (each.getProcess().pid == senderProcess.pid) {
                    return channel;
                }
            }
        }
        return null;
    }

    private void broadcast(Message message) {
        for (Channel neighborChannel : neighbors) {
            if (!neighborChannel.getProcess().equals(parent)) {
                sendMessage(neighborChannel, message);
            }
        }

    }

    private void sendMessage(Channel neighborChannel, Message message) {
        if(message!=null) {
            System.out.println("Round: " + round + ", SENDING " + message.getType() + " from " + message.getSenderPid() + " to -> " + neighborChannel.getProcess().pid);
            neighborChannel.add(message);
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


    // creating a channel to the new process from this process and adding neighbor
    public void addNeighbor(Process process) {
        this.neighbors.add(new Channel(process));
    }

    public ArrayList<Channel> getNeighbors() {
        return neighbors;
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
