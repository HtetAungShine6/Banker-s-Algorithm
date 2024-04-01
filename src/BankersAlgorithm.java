import java.util.Arrays;
import java.util.Scanner;

public class BankersAlgorithm {
    private int[][] allocation;
    private int[][] maxDemand;
    private int[] totalResources;
    private int[] available;
    private int[][] need;
    private int numProcesses;
    private int numResources;

    public BankersAlgorithm(int[][] allocation, int[][] maxDemand, int[] totalResources) {
        this.allocation = allocation;
        this.maxDemand = maxDemand;
        this.totalResources = totalResources;
        this.numProcesses = allocation.length;
        this.numResources = allocation[0].length;
        this.need = new int[numProcesses][numResources];
        calculateNeed();
        calculateAvailable();
    }

    private void calculateNeed() {
        for (int i = 0; i < numProcesses; i++) {
            for (int j = 0; j < numResources; j++) {
                need[i][j] = maxDemand[i][j] - allocation[i][j];
            }
        }
    }

    private void calculateAvailable() {
        available = Arrays.copyOf(totalResources, numResources);
        for (int i = 0; i < numProcesses; i++) {
            for (int j = 0; j < numResources; j++) {
                available[j] -= allocation[i][j];
            }
        }
    }

    public boolean isSafeState() {
        int[] work = Arrays.copyOf(available, numResources);
        boolean[] finish = new boolean[numProcesses];
        int[] safeSequence = new int[numProcesses];
        int[] unsafeSequence = new int[numProcesses];
        int safeCount = 0;
        int unsafeCount = 0;

        System.out.println("Initial available resources: " + Arrays.toString(available));
        System.out.println("Initial allocation matrix:");
        printMatrix(allocation);
        System.out.println("Initial maxAllocation matrix:");
        printMatrix(maxDemand);
        System.out.println("Initial need matrix:");
        printMatrix(need);

        while (safeCount + unsafeCount < numProcesses) {
            boolean found = false;
            for (int i = 0; i < numProcesses; i++) {
                if (!finish[i] && isProcessSafe(i, work)) {
                    for (int j = 0; j < numResources; j++) {
                        work[j] += allocation[i][j];
                    }
                    safeSequence[safeCount++] = i;
                    finish[i] = true;
                    found = true;
                    System.out.println("Allocated resources for process P" + (i + 1) + ": " + Arrays.toString(allocation[i]));
                    System.out.println("Current available resources: " + Arrays.toString(work));
                }
            }
            if (!found) {
                for (int i = 0; i < numProcesses; i++) {
                    if (!finish[i]) {
                        unsafeSequence[unsafeCount++] = i;
                        finish[i] = true;
                        break;
                    }
                }
            }
        }

        if (unsafeCount == 0) {
            System.out.println("No deadlock exists.");
        } else {
            System.out.print("Deadlock detected. Unsafe Sequence: " + Arrays.toString(unsafeSequence));
            System.out.print(" (Processes that cannot be completed: ");
            for (int i = 0; i < unsafeCount; i++) {
                System.out.print("P" + (unsafeSequence[i] + 1) + " ");
            }
            System.out.println(")");
        }

        if (safeCount == numProcesses) {
            System.out.println("Safe Sequence: " + Arrays.toString(safeSequence));
            return true;
        } else {
            return false;
        }
    }

    private boolean isProcessSafe(int process, int[] work) {
        for (int i = 0; i < numResources; i++) {
            if (need[process][i] > work[i]) {
                return false;
            }
        }
        return true;
    }

    public boolean isProcessCompleted(int process) {
        for (int i = 0; i < numResources; i++) {
            if (need[process][i] > 0) {
                return false;
            }
        }
        return true;
    }

    public void printNeedMatrix() {
        System.out.println("Need Matrix:");
        printMatrix(need);
    }

    private void printMatrix(int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            System.out.println(Arrays.toString(matrix[i]));
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the number of processes: ");
        int numProcesses = scanner.nextInt();

        System.out.print("Enter the number of resources: ");
        int numResources = scanner.nextInt();

        int[][] allocation = new int[numProcesses][numResources];
        int[][] maxDemand = new int[numProcesses][numResources];
        int[] totalResources = new int[numResources];

        System.out.println("Enter allocation matrix:");
        for (int i = 0; i < numProcesses; i++) {
            for (int j = 0; j < numResources; j++) {
                allocation[i][j] = scanner.nextInt();
            }
        }

        System.out.println("Enter maxAllocation matrix:");
        for (int i = 0; i < numProcesses; i++) {
            for (int j = 0; j < numResources; j++) {
                maxDemand[i][j] = scanner.nextInt();
            }
        }

        System.out.println("Enter total resources:");
        for (int i = 0; i < numResources; i++) {
            totalResources[i] = scanner.nextInt();
        }

        BankersAlgorithm banker = new BankersAlgorithm(allocation, maxDemand, totalResources);
        banker.printNeedMatrix();
        boolean safeState = banker.isSafeState();
        if (safeState) {
            System.out.println("System is in a safe state.");
            System.out.println("Processes status:");
            for (int i = 0; i < numProcesses; i++) {
                if (banker.isProcessCompleted(i)) {
                    System.out.println("Process P" + (i + 1) + " is completed.");
//                    System.out.println("Process P" + (i + 1) + " is not completed.");
                } else {
//                	System.out.println("Process P" + (i + 1) + " is completed.");
                    System.out.println("Process P" + (i + 1) + " is not completed.");
                }
            }
        } else {
            System.out.println("System is not in a safe state. Processes status:");
            for (int i = 0; i < numProcesses; i++) {
                if (!banker.isProcessCompleted(i)) {
                    System.out.println("Process P" + (i + 1) + " cannot be completed.");
                }else {
                	System.out.println("Process P" + (i + 1) + " completed.");
                }
            }
        }
        scanner.close();
    }
}
