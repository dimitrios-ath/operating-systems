import java.util.ArrayList;

public abstract class Scheduler {

    protected ArrayList<Process> processes; // the list of processes to be executed

    protected int algorithmUsed; // 1=FCFS, 2=SRTF, 3=RoundRobin
    
    public Scheduler() {
        this.processes = new ArrayList<Process>();
    }

    /* the addProcess() method should add a new process to the list of
     * processes that are candidates for execution. This will probably
     * differ for different schedulers */
    public abstract void addProcess(Process p);
    
    /* the removeProcess() method should remove a process from the list
     * of processes that are candidates for execution. Common for all
     * schedulers. */
    public void removeProcess(Process p) {
        /* TODO: you need to add some code here */
        this.processes.remove(p);
    }
    
    /* the getNextProcess() method should return the process that should
     * be executed next by the CPU */
    public abstract Process getNextProcess();

    public int getAlgorithmUsed() {return algorithmUsed;}
    public ArrayList<Process> getProcesses() {return processes;}
}
