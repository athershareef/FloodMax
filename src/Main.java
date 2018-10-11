public class Main {

    public static void main(String[] args) {
        // TODO: Read Input
        int n = 8;
        int[] processIds = new int[n];
        for (int i = 0; i < n; i++) {
            processIds[i] = i;
        }
        int[][] adjacencyMatrix = new int[][]{
                {0, 1, 1, 1, 0, 0, 0, 0},
                {1, 0, 1, 0, 0, 1, 0, 1},
                {1, 1, 0, 1, 1, 0, 0, 1},
                {1, 0, 1, 0, 1, 0, 0, 0},
                {0, 0, 1, 1, 0, 0, 1, 1},
                {0, 1, 0, 0, 0, 0, 1, 1},
                {0, 0, 0, 0, 1, 1, 0, 1},
                {0, 1, 1, 0, 1, 1, 1, 0}};

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
            threads[i].start();
        }

        while (true) {
            boolean isAnyThreadRunning = false;

            for (Thread thread : threads) {
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

            boolean roundComplete = true;

            while(true) {
                for (Process process : processes) {
                    if(process.isStartNextRound()){
                        roundComplete = false;
                        delay(10);
                        break;
                    }
                }
                if(roundComplete){
                    break;
                }
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
