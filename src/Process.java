public class Process implements Runnable {

    private int id;

    public Process(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        System.out.println("Process " + id +" is running!");
    }
}
