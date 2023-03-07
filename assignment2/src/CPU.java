import java.util.ArrayList;

public class CPU {

    public static int clock = 0; // this should be incremented on every CPU cycle
    
    private Scheduler scheduler;
    private MMU mmu;
    private Process[] processes;
    private int currentProcess;

    private int totalProcesses = 0, totalTerminatedProcesses = 0;
    private ArrayList<MemorySlot> memorySlotsToFree;
    private Process previousProcess = null;
    private boolean roundRobinSwapping = false;

    public CPU(Scheduler scheduler, MMU mmu, Process[] processes) {
        this.scheduler = scheduler;
        this.mmu = mmu;
        this.processes = processes;

        memorySlotsToFree = new ArrayList<>();
    }
    
    public void run() {
        /* TODO: you need to add some code here
         * Hint: you need to run tick() in a loop, until there is nothing else to do... */
        for (Process p: processes) {totalProcesses++;}
        currentProcess = -1;

        while(totalTerminatedProcesses != totalProcesses) {tick();}

        System.out.println("[*] Total CPU cycles: " + clock);
    }
    
    public void tick() {
        /* TODO: you need to add some code here
         * Hint: this method should run once for every CPU cycle */
        System.out.println("-----------------------------------------------------------------------");
        System.out.println("                             CPU clock: " + clock);
        System.out.println("-----------------------------------------------------------------------");

        mmu.setMemorySlotsToFree(memorySlotsToFree);


        if (currentProcess == -1) { // All algorithms, if no process is running
            for (Process process : processes) { // Load processes into memory
                if (process.getPCB().getState() == ProcessState.NEW && clock >= process.getArrivalTime()) {
                    process.actualArrivalTime = clock;
                    System.out.println("[*] Process [" + process.getPCB().getPid() + "] arrived, desired arrival time: " + process.getArrivalTime());
                    if (process.getMemoryRequirements() > maxOfArray(mmu.getAvailableBlockSizes())) {
                        process.getPCB().setState(ProcessState.TERMINATED, clock);
                        totalProcesses--;
                        System.out.println("[-] Process [" + process.getPCB().getPid() + "] (" + process.getMemoryRequirements() +
                                "kB) exceeds maximum block capacity (" + maxOfArray(mmu.getAvailableBlockSizes()) + "kB)");
                    } else { // Load process (with valid memory requirements) into memory (only one each time, due to 1 NOP cycle)
                        if (mmu.loadProcessIntoRAM(process)) {
                            process.getPCB().setState(ProcessState.READY, clock);
                            clock++;
                            return;
                        }
                    }
                }
            }
        }
        else {
            if (scheduler.getAlgorithmUsed() == 2) { // Only for SRTF, if a new process has less remaining time than the current process, try to load and swap processes
                for (Process process : processes) { // Load processes into memory
                    if (process.getPCB().getState() == ProcessState.NEW && clock >= process.getArrivalTime()) {
                        process.actualArrivalTime = clock;
                        System.out.println("[*] Process [" + process.getPCB().getPid() + "] arrived, desired arrival time: " + process.getArrivalTime());
                        if (process.getMemoryRequirements() > maxOfArray(mmu.getAvailableBlockSizes())) {
                            process.getPCB().setState(ProcessState.TERMINATED, clock);
                            totalProcesses--;
                            System.out.println("[-] Process [" + process.getPCB().getPid() + "] (" + process.getMemoryRequirements() +
                                    "kB) exceeds maximum block capacity (" + maxOfArray(mmu.getAvailableBlockSizes()) + "kB)");
                        } else { // Load process (with valid memory requirements) into memory (only one each time, due to 1 NOP cycle)
                            if (process.getBurstTime() < (scheduler.getProcesses().get(0).getBurstTime() - scheduler.getProcesses().get(0).getExecutionTime())) {
                                System.out.println("[*] Process [" + process.getPCB().getPid() + "] has the shortest remaining execution time");
                                System.out.println("[!] Stopping process [" + currentProcess + "] and loading the new process [" + process.getPCB().getPid() + "] in memory");
                                System.out.println("[*] Changing Process [" + currentProcess + "] state: RUNNING -> READY (2 NOP cycles)");  // Stop the previous process in order to load the new one in memory
                                clock++;
                                System.out.println("-----------------------------------------------------------------------");
                                System.out.println("                             CPU clock: " + clock);
                                System.out.println("-----------------------------------------------------------------------");
                                System.out.println("[*] Changing Process [" + currentProcess + "] state: RUNNING -> READY (1 NOP cycle)");
                                clock++;
                                previousProcess.getPCB().setState(ProcessState.READY, clock);
                                System.out.println("-----------------------------------------------------------------------");
                                System.out.println("                             CPU clock: " + clock);
                                System.out.println("-----------------------------------------------------------------------");
                                if (mmu.loadProcessIntoRAM(process)) { // Load the new process in memory
                                    process.getPCB().setState(ProcessState.READY, clock);
                                    clock++;
                                    return;
                                }
                            }
                        }
                    }
                }
            }
            if (scheduler.getAlgorithmUsed() == 3 && roundRobinSwapping) { // Only for RoundRobin, load new process when process swapping takes place
                for (Process process : processes) { // Load processes into memory
                    if (process.getPCB().getState() == ProcessState.NEW && clock >= process.getArrivalTime()) {
                        process.actualArrivalTime = clock;
                        System.out.println("[*] Process [" + process.getPCB().getPid() + "] arrived, desired arrival time: " + process.getArrivalTime());
                        if (process.getMemoryRequirements() > maxOfArray(mmu.getAvailableBlockSizes())) {
                            process.getPCB().setState(ProcessState.TERMINATED, clock);
                            totalProcesses--;
                            System.out.println("[-] Process [" + process.getPCB().getPid() + "] (" + process.getMemoryRequirements() +
                                    "kB) exceeds maximum block capacity (" + maxOfArray(mmu.getAvailableBlockSizes()) + "kB)");
                        } else { // Load process (with valid memory requirements) into memory (only one each time, due to 1 NOP cycle)
                            if (mmu.loadProcessIntoRAM(process)) { // Load the new process in memory
                                process.getPCB().setState(ProcessState.READY, clock);
                                clock++;
                                return;
                            }

                        }
                    }
                }
            }
        }

        for (Process readyProcess : processes) { // Add to scheduler queue
            if (!readyProcess.addedToScheduler && readyProcess.getPCB().getState()==ProcessState.READY) {
                scheduler.addProcess(readyProcess);
                readyProcess.addedToScheduler = true;
                readyProcess.addedToSchedulerTime = clock;
                System.out.println("[+] Added Process [" + readyProcess.getPCB().getPid() + "] to the scheduler queue");
            }
        }

        Process nextProcess = scheduler.getNextProcess();

        for (Process process : scheduler.processes) { // If process is READY and not executed increase waiting time
            if (process.getPCB().getState()==ProcessState.READY && currentProcess != process.getPCB().getPid()) {
                process.waitInBackground();
            }
        }

        if (previousProcess!= null && previousProcess!=nextProcess) { // Swap processes in 2 cycles in total
            if (previousProcess.getPCB().getState()==ProcessState.RUNNING && nextProcess.getPCB().getState()==ProcessState.READY){
                System.out.println("[*] Changing Process ["+previousProcess.getPCB().getPid()+"] state: RUNNING -> READY (2 NOP cycles)");
                System.out.println("[*] Changing Process ["+nextProcess.getPCB().getPid()+"] state: READY -> RUNNING (2 NOP cycles)");
                clock++;
                System.out.println("-----------------------------------------------------------------------");
                System.out.println("                             CPU clock: " + clock);
                System.out.println("-----------------------------------------------------------------------");
                System.out.println("[*] Changing Process ["+previousProcess.getPCB().getPid()+"] state: RUNNING -> READY (1 NOP cycle)");
                System.out.println("[*] Changing Process ["+nextProcess.getPCB().getPid()+"] state: READY -> RUNNING (1 NOP cycle)");
                clock++;
                previousProcess.getPCB().setState(ProcessState.READY, clock); // Update process states
                nextProcess.getPCB().setState(ProcessState.RUNNING, clock);
                roundRobinSwapping = true; // set to true, in order to load new arriving process after swapping ends
                return;
            }
        }
        roundRobinSwapping = false;
        if (nextProcess!= null) {
            if (nextProcess.getPCB().getState()==ProcessState.READY) {  // READY -> RUNNING, CPU cannot do anything else
                if (nextProcess.firstTimeExecuted == -1) {nextProcess.firstTimeExecuted=clock;}
                System.out.println("[*] Changing Process ["+nextProcess.getPCB().getPid()+"] state: READY -> RUNNING (2 NOP cycles)");
                clock++;
                System.out.println("-----------------------------------------------------------------------");
                System.out.println("                             CPU clock: " + clock);
                System.out.println("-----------------------------------------------------------------------");
                System.out.println("[*] Changing Process ["+nextProcess.getPCB().getPid()+"] state: READY -> RUNNING (1 more NOP cycle)");
                nextProcess.getPCB().setState(ProcessState.RUNNING, clock);
                currentProcess = nextProcess.getPCB().getPid();
                previousProcess = nextProcess;
                clock++;
                return;
            }
            currentProcess = nextProcess.getPCB().getPid();
            //System.out.println("currentProcess " + nextProcess.getPCB().getPid());
            //if (previousProcess!=null) {System.out.println("previousProcess " + previousProcess.getPCB().getPid());}
            nextProcess.run();
            previousProcess = nextProcess;
            System.out.println("[*] Running Process [" + nextProcess.getPCB().getPid() + "], execution time left: " + (nextProcess.getBurstTime() - nextProcess.getExecutionTime()));
            if (nextProcess.getExecutionTime() == nextProcess.getBurstTime()) { // If process has finished execution
                nextProcess.getPCB().setState(ProcessState.TERMINATED, clock);
                nextProcess.terminatedTime = clock;
                scheduler.processes.remove(nextProcess);
                memorySlotsToFree.add(nextProcess.getSlotUsedByProcess());
                previousProcess = null;
                currentProcess = -1;
                totalTerminatedProcesses++;
                System.out.println("[*] Changing Process [" + nextProcess.getPCB().getPid() + "] state: RUNNING -> TERMINATED");
                System.out.println("[+] Process [" + nextProcess.getPCB().getPid() + "] Terminated");
                System.out.println("[*] Process [" + nextProcess.getPCB().getPid() + "] Waiting time: " + nextProcess.getWaitingTime());
                System.out.println("[*] Process [" + nextProcess.getPCB().getPid() + "] Response time: " + nextProcess.getResponseTime());
                System.out.println("[*] Process [" + nextProcess.getPCB().getPid() + "] TurnAround time: " + nextProcess.getTurnAroundTime());
                mmu.loadProcessIntoRAM(null); // Free up used space
            }
        }

        boolean allProcessesTerminated = true;
        for (Process process : processes) { // Check if every process is terminated
            if (process.getPCB().getState() != ProcessState.TERMINATED) {
                allProcessesTerminated = false;
                break;
            }
        }
        if (allProcessesTerminated) {
            System.out.println("-----------------------------------------------------------------------\n[*] All processes Terminated");
            return;
        }
        clock++;
    }

    protected int maxOfArray(int[] array){
        int max = 0;
        for (int j : array) {if (j > max) {max = j;}}
        return max;
    }
}
