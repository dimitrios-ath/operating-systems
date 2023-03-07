
public class Process {
    private ProcessControlBlock pcb;
    private int arrivalTime;
    private int burstTime;
    private int memoryRequirements;

    private MemorySlot slotUsedByProcess;
    protected int totalExecutionTime = 0;
    protected int firstTimeExecuted = -1;
    protected int actualArrivalTime = -1;
    protected boolean addedToScheduler = false;
    protected int addedToSchedulerTime = -1;
    protected int waitInBackgroundTime = 0;
    protected int terminatedTime = -1;
    
    public Process(int arrivalTime, int burstTime, int memoryRequirements) {
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.memoryRequirements = memoryRequirements;
        this.pcb = new ProcessControlBlock();
    }
    
    public ProcessControlBlock getPCB() {
        return this.pcb;
    }
   
    public void run() {
        /* TODO: you need to add some code here
         * Hint: this should run every time a process starts running */
        totalExecutionTime++;
    }
    
    public void waitInBackground() {
        /* TODO: you need to add some code here
         * Hint: this should run every time a process stops running */
        waitInBackgroundTime++;
    }

    public double getWaitingTime() { // time spent waiting in the scheduler queue while in READY state for getting the CPU
        /* TODO: you need to add some code here
         * and change the return value */
        return this.waitInBackgroundTime;
    }
    
    public double getResponseTime() { // amount of time after which a process gets the CPU for the first time after entering the scheduler queue while in READY state
        /* TODO: you need to add some code here
         * and change the return value */
        if (this.firstTimeExecuted!=-1 && addedToSchedulerTime!=-1) {
            return (this.firstTimeExecuted - this.addedToSchedulerTime);
        }
        return -1;
    }
    
    public double getTurnAroundTime() { // amount of time spent in the system
        /* TODO: you need to add some code here
         * and change the return value */
        if (terminatedTime!=-1 && actualArrivalTime!=-1) {
            return terminatedTime - actualArrivalTime;
        }
        return -1;
    }

    public int getMemoryRequirements() {return memoryRequirements;}
    public int getArrivalTime() {return arrivalTime;}
    public int getBurstTime() {return burstTime;}
    public int getExecutionTime() {return totalExecutionTime;}
    public void setSlotUsedByProcess(MemorySlot slotUsedByProcess) {this.slotUsedByProcess = slotUsedByProcess;}
    public MemorySlot getSlotUsedByProcess() {return slotUsedByProcess;}
}
