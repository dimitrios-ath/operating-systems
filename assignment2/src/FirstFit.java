import java.util.ArrayList;

public class FirstFit extends MemoryAllocationAlgorithm {
    
    public FirstFit(int[] availableBlockSizes) {
        super(availableBlockSizes);
    }

    public int fitProcess(Process p, ArrayList<MemorySlot> currentlyUsedMemorySlots) {
        boolean fit = false;
        int address = -1;
        /* TODO: you need to add some code here
         * Hint: this should return the memory address where the process was
         * loaded into if the process fits. In case the process doesn't fit, it
         * should return -1. */
        for (int currentBlock=0; currentBlock<availableBlockSizes.length; currentBlock++) {  // For all blocks available
            if (updatedAvailableBlockSizes[currentBlock] >= p.getMemoryRequirements()) {
                address = checkAndLoad(p, currentlyUsedMemorySlots, currentBlock); // If process is not loaded in this block, continue to the next block
                if (address!=-1) {break;}
            }
        }
        return address;
    }

    private int checkAndLoad(Process p, ArrayList<MemorySlot> currentlyUsedMemorySlots, int currentBlock) {
        int address = -1;
        int blockIndex = 0, blockPointer = 0;
        for(int i=0; i<currentBlock; i++) {blockPointer += availableBlockSizes[i];} // Initialize blockPointer to point the start of the currentBlock
        int start = blockPointer, end = start + p.getMemoryRequirements();
        boolean validSlot = false;

        while (blockIndex <= availableBlockSizes[currentBlock]-p.getMemoryRequirements()){ // Loop through all possible free spaces with needed size in currentBlock
            start = blockPointer + blockIndex;
            end = blockPointer + blockIndex + p.getMemoryRequirements();
            validSlot = true;
            for (MemorySlot slot : currentlyUsedMemorySlots) { // Iterate through all used memory slots
                if (slot.getBlockStart() == blockPointer && slot.getBlockEnd() == blockPointer + availableBlockSizes[currentBlock]) {  // If the used slot is in the currentBlock
                    if (start < slot.getEnd() && slot.getStart() < end) {
                        validSlot = false;
                        blockIndex += (slot.getEnd()-slot.getStart()-1);
                    } // If slots overlap, mark the free space as invalid and continue to the next free space
                    //System.out.println("PID: " + p.getPCB().getPid() + " (" + p.getMemoryRequirements() + "kB), needed space " + start + "-" + end + ", used slot: " + slot.getStart() + "-" + slot.getEnd() + ", Valid: " + validSlot);
                }
                if (!validSlot) {break;}
            }
            if (validSlot) {break;} // if valid free space is found, exit while loop and load the process to memory
            blockIndex++;
        }

        if (validSlot) { // if free space with needed size is found, load the process
            address = start;
            MemorySlot newSlot = new MemorySlot(start, end, blockPointer, blockPointer + availableBlockSizes[currentBlock]);
            newSlot.setBlockID(currentBlock);
            currentlyUsedMemorySlots.add(newSlot);
            p.setSlotUsedByProcess(newSlot);
            updatedAvailableBlockSizes[currentBlock] -= p.getMemoryRequirements();
            System.out.println("[+] Process with PID [" + p.getPCB().getPid() + "] (" + p.getMemoryRequirements() + "kB) loaded at: " + start + "-" + end);
            System.out.println("[*] Changing Process ["+p.getPCB().getPid()+"] state: NEW -> READY (1 NOP cycle)");
            //System.out.println("[*] Space left on block [" + currentBlock + "]: " + updatedAvailableBlockSizes[currentBlock] + "kB");
        }
        return address;
    }

}
