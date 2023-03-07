import java.util.ArrayList;

public class RoundRobin extends Scheduler {

    private int quantum;

    private static int timeCounter = 0;
    protected ArrayList<Process> roundRobinQueue;
    
    public RoundRobin() {
        this.quantum = 1; // default quantum
        /* TODO: you _may_ need to add some code here */
        algorithmUsed = 3;
    }
    
    public RoundRobin(int quantum) {
        this();
        this.quantum = quantum;
    }

    public void addProcess(Process p) {
        /* TODO: you need to add some code here */
        processes.add(p);
    }
    
    public Process getNextProcess() {
        /* TODO: you need to add some code here
         * and change the return value */
        if (timeCounter==0 && processes.size()>1) {
            //System.out.println("Scheduler before queue:");
            //for (Process p : processes) {System.out.println("PID: " + p.getPCB().getPid() + ", timeLeft: " + (p.getBurstTime() - p.getExecutionTime())+", state: " +p.getPCB().getState());}
            Process temp = processes.get(0);
            for (int i = 0; i < processes.size()-1; i++) { // Rotate the scheduler queue one time
                processes.set(i, processes.get(i+1));
            }
            processes.set(processes.size()-1, temp);
            //System.out.println("Scheduler after queue:");
            //for (Process p : processes) {System.out.println("PID: " + p.getPCB().getPid() + ", timeLeft: " + (p.getBurstTime() - p.getExecutionTime())+", state: " +p.getPCB().getState());}
        }
        //if (processes.size()!=0) {System.out.println("Selected process PID ["+processes.get(0).getPCB().getPid()+"]");}
        timeCounter = (timeCounter + 1) % (quantum+1);
        if (processes.size()!=0) {return processes.get(0);}
        return null;
    }
}
