import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Channel {
    private Process process;
    private Queue<Message> queue;

    public Channel(Process process) {
        this.process = process;
        this.queue = new ConcurrentLinkedQueue<Message>();
    }

    public Process getProcess() {
        return process;
    }

    public void add(Message message) {
        this.queue.add(message);
    }

    public void purge() {
        this.queue.clear();
    }


    public ArrayList<Message> read() {
        return new ArrayList<>(this.queue);
    }


}
