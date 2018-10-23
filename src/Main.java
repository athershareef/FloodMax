import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Main {

    public static HashMap<Process, ArrayList<Process>> Tree = new HashMap<>();

    public static void main(String[] args) throws FileNotFoundException {
        String inputFile;
        int n = 0;
        int[] processIds = new int[0];
        int[][] adjacencyMatrix = new int[0][0];
        if (args.length == 0) {
            inputFile = "input3.txt";
        } else {
            inputFile = args[0];
        }
        System.setOut(new PrintStream(new File("output.txt")));

        try {
            File file = new File(inputFile);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            n = Integer.parseInt(bufferedReader.readLine());
            processIds = new int[n];
            adjacencyMatrix = new int[n][n];
            int i = 0;
            for (String s : bufferedReader.readLine().split(" ")) {
                processIds[i] = Integer.parseInt(s);
                i++;
            }

            for (i = 0; i < n; i++) {
                int j = 0;
                for (String s : bufferedReader.readLine().split(", ")) {
                    adjacencyMatrix[i][j] = Integer.parseInt(s);
                    j++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Matrix matrix = new Matrix(n);

        Process[] processes = new Process[n];


        for (int i = 0; i < processes.length; i++) {
            processes[i] = new Process(processIds[i]);
        }

        for (int i = 0; i < processes.length; i++) {
            for (int j = 0; j < processes.length; j++) {
                if (adjacencyMatrix[i][j] == 1)
                    processes[i].addNeighbor(processes[j]);
            }
        }

        Thread[] threads = new Thread[n];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(processes[i]);
            threads[i].setName("Process " + processes[i].getPid());
            threads[i].start();
        }

        while (true) {
            boolean isAnyThreadRunning = false;

            for (Thread thread : threads) {
                delay(10);
                if (thread.isAlive()) {
                    isAnyThreadRunning = true;
                    break;
                }
            }

            if (!isAnyThreadRunning) {
                System.out.println("Leader is Selected!");
                break;
            }

            for (Process process : processes) {
                process.setStartNextRound(true);
            }

            delay(50);

            boolean leaderElected = false;

            while (true) {
                boolean everyoneKnowsLeader = true;
                boolean roundComplete = true;
                for (Process process : processes) {
                    if (process.isLeaderElected()) {
                        leaderElected = true;
                    } else {
                        everyoneKnowsLeader = false;
                    }
                    if (!process.isStartNextRound() && leaderElected && process.getStatus().equals(Status.UNKNOWN)) {
                        process.setLeaderElected(true);
                        process.setStatus(Status.NON_LEADER);
                        process.setStartNextRound(true);
                        delay(20);
                        process.setStartNextRound(false);
                    }
                    if (process.isStartNextRound()) {
                        roundComplete = false;
                        delay(10);
                        break;
                    }

                }
                if (roundComplete || everyoneKnowsLeader) {
                    break;
                }
            }

            leaderElected = true;


            for (Process process : processes) {
                if (!process.isLeaderElected()) {
                    leaderElected = false;
                }
            }


            if (leaderElected) {
                int leaderId = 0;
                for (Process process : processes) {
                    if (process.getStatus().equals(Status.LEADER)) {
                        leaderId = process.getPid();
                    }
                }
                System.out.println("Leader is " + leaderId + "-Everyone knows!");


                // To make sure that we can map the higher ids within the Matrix
                HashMap<Integer, Integer> valMap = new HashMap<>();

                for (int i = 0; i < processIds.length; i++) {
                    valMap.put(processIds[i], i);
                }

                for (Process process : Tree.keySet()) {
                    for (Process p : Tree.get(process)) {
                        matrix.adjacencyTreeMatrix[valMap.get(p.getPid())][valMap.get(process.getPid())] = 1;
//                        System.out.println(p.getPid()+" -> "+process.getPid());
                    }
                }
                System.out.println("Resultant Tree Matrix with ACKS");
                for (int[] row : matrix.adjacencyTreeMatrix) {
                    System.out.println(Arrays.toString(row));
                }
                System.out.println("************ Result **************");
                System.out.println();
                System.out.println("Leader is " + leaderId + "-Everyone knows!");
                System.out.println();
                System.out.println("************ END *****************");


                break;
            }

        }


    }

    private static void delay(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
