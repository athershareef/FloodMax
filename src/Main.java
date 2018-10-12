import enums.Status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class Main {

    public static HashMap<Process, ArrayList<Process>> Tree = new HashMap<>();

    public static void main(String[] args) {
        // TODO: Read Input
        int n = 21;
        Matrix matrix = new Matrix(n);
        int[] processIds = new int[n];
        for (int i = 0; i < n; i++) {
            processIds[i] = i;
        }
//        int[][] adjacencyMatrix = new int[][]{
//                {0, 1, 1, 1, 0, 0, 0, 0},
//                {1, 0, 1, 0, 0, 1, 0, 1},
//                {1, 1, 0, 1, 1, 0, 0, 1},
//                {1, 0, 1, 0, 1, 0, 0, 0},
//                {0, 0, 1, 1, 0, 0, 1, 1},
//                {0, 1, 0, 0, 0, 0, 1, 1},
//                {0, 0, 0, 0, 1, 1, 0, 1},
//                {0, 1, 1, 0, 1, 1, 1, 0}};
        int[][] adjacencyMatrix = new int[][]{
                {0, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {0, 0, 0, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1},
                {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0}
        };



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
            threads[i].setName("Process "+processes[i].getPid());
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

            while(true) {
                boolean everyoneKnowsLeader = true;
                boolean roundComplete = true;
                for (Process process : processes) {
                    if(process.isLeaderElected()){
                        leaderElected = true;
                    }else {
                        everyoneKnowsLeader = false;
                    }
                    if(!process.isStartNextRound() && leaderElected && process.getStatus().equals(Status.UNKNOWN)){
                        process.setLeaderElected(true);
                        process.setStatus(Status.NON_LEADER);
                        process.setStartNextRound(true);
                        delay(20);
                        process.setStartNextRound(false);
                    }
                    if(process.isStartNextRound()){
                        roundComplete = false;
                        delay(10);
                        break;
                    }

                }
                if(roundComplete || everyoneKnowsLeader){
                    break;
                }
            }

            leaderElected = true;


            for(Process process: processes){
                if(!process.isLeaderElected()){
                    leaderElected = false;
                }
            }


            if (leaderElected) {
                int leaderId = 0;
                for(Process process: processes){
                    if(process.getStatus().equals(Status.LEADER)){
                        leaderId = process.getPid();
                    }
                }
                System.out.println("Leader is "+ leaderId +"-Everyone knows!");

                for(Process process : Tree.keySet()){
                    for(Process p: Tree.get(process)){
                        matrix.adjacencyTreeMatrix[p.getPid()][process.getPid()] = 1;
//                        System.out.println(p.getPid()+" -> "+process.getPid());
                    }
                }
                System.out.println("Resultant Tree Matrix with ACKS");
                for (int[] row : matrix.adjacencyTreeMatrix){
                    System.out.println(Arrays.toString(row));
                }
                System.out.println("************ Result **************");
                System.out.println();
                System.out.println("Leader is "+ leaderId +"-Everyone knows!");
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
