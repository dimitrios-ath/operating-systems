import java.util.ArrayList;

public class SRTF extends Scheduler {

    public SRTF() {
        /* TODO: you _may_ need to add some code here */
        algorithmUsed = 2;
    }

    public void addProcess(Process p) {
        /* TODO: you need to add some code here */
        processes.add(p);
    }

    private void sort(ArrayList<Process> array) { // Sort given array based on remaining execution time
        for (int i = 0; i < array.size(); i++) {
            for (int j = array.size() - 1; j > i; j--) {
                if (array.get(i).getBurstTime()-array.get(i).getExecutionTime() > array.get(j).getBurstTime()-array.get(j).getExecutionTime()) {
                    Process tmp = array.get(i);
                    array.set(i,array.get(j)) ;
                    array.set(j,tmp);
                }
            }
        }
    }
    
    public Process getNextProcess() {
        /* TODO: you need to add some code here
         * and change the return value */
        sort(processes); // Sort the processes queue
        //System.out.println("Scheduler queue:");
        //for (Process p : processes) {System.out.println("PID: " + p.getPCB().getPid() + ", timeLeft: " + (p.getBurstTime() - p.getExecutionTime()));}
        //if (processes.size()!=0) { System.out.println("Selected process PID [" + processes.get(0).getPCB().getPid() + "]");}
        if (processes.size()!=0) {return processes.get(0);}
        return null;
    }
}
