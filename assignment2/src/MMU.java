import java.util.ArrayList;

public class MMU {

    private final int[] availableBlockSizes;
    private MemoryAllocationAlgorithm algorithm;
    private ArrayList<MemorySlot> currentlyUsedMemorySlots;

    private ArrayList<MemorySlot> memorySlotsToFree;
    
    public MMU(int[] availableBlockSizes, MemoryAllocationAlgorithm algorithm) {
        this.availableBlockSizes = availableBlockSizes;
        this.algorithm = algorithm;
        this.currentlyUsedMemorySlots = new ArrayList<MemorySlot>();
    }

    public boolean loadProcessIntoRAM(Process p) {
        boolean fit = false;
        /* TODO: you need to add some code here
         * Hint: this should return true if the process was able to fit into memory
         * and false if not */
        if (memorySlotsToFree!=null) {
            int memorySlotsToFreeSize = memorySlotsToFree.size();
            int currentlyUsedMemorySlotsSize = currentlyUsedMemorySlots.size();
            for (int i = 0; i < memorySlotsToFreeSize; i++) {
                for (int j = 0; j < currentlyUsedMemorySlotsSize; j++) {
                    if (currentlyUsedMemorySlots.get(j) == memorySlotsToFree.get(i)) {
                        System.out.println("[*] MemorySlot at offset " + currentlyUsedMemorySlots.get(j).getStart() + "-" + currentlyUsedMemorySlots.get(j).getEnd() + " cleared");
                        //System.out.println("[*] Available space before free: "+ algorithm.updatedAvailableBlockSizes[currentlyUsedMemorySlots.get(j).getBlockID()]);
                        algorithm.updatedAvailableBlockSizes[currentlyUsedMemorySlots.get(j).getBlockID()] += (currentlyUsedMemorySlots.get(j).getEnd()-currentlyUsedMemorySlots.get(j).getStart());
                        //System.out.println("[*] Available space after free: "+ algorithm.updatedAvailableBlockSizes[currentlyUsedMemorySlots.get(j).getBlockID()]);
                        currentlyUsedMemorySlots.remove(j);
                        memorySlotsToFree.remove(i);
                        memorySlotsToFreeSize--;
                        currentlyUsedMemorySlotsSize--;
                        break;
                    }
                }
            }
        }
        if (p!=null) {
            if (algorithm.fitProcess(p, this.currentlyUsedMemorySlots) != -1) {
                fit = true;
            }
        }
        return fit;
    }

    public int[] getAvailableBlockSizes() {return availableBlockSizes;}
    public void setMemorySlotsToFree(ArrayList<MemorySlot> memorySlotsToFree) {this.memorySlotsToFree = memorySlotsToFree;}
    public ArrayList<MemorySlot> getMemorySlotsToFree() {return memorySlotsToFree;}
}
