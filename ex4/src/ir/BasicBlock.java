package ir;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Single node in CFG. Each block wraps exactly one IR command and exposes
 * immutable metadata (index, label, command) alongside mutable edge lists.
 */
public class BasicBlock {
    private final int index;
    private final IrCommand command;
    private final String label;
    private final List<BasicBlock> successors = new ArrayList<>();
    private final List<BasicBlock> predecessors = new ArrayList<>();

    /**
     * Every IR command becomes a block; labels are captured eagerly so we can
     * wire jump targets in a second pass.
     */
    public BasicBlock(int index, IrCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("IR command can't be null");
        }

        this.index = index;
        this.command = command;
        // Labels are preserved so Graph can look them up when resolving jumps.
        this.label = (command instanceof IrCommandLabel)
                ? ((IrCommandLabel) command).labelName
                : null;
    }

    public int getIndex() {
        return index;
    }

    public IrCommand getCommand() {
        return command;
    }

    public String getLabel() {
        return label;
    }

    public List<BasicBlock> getSuccessors() {
        return Collections.unmodifiableList(successors);
    }

    public List<BasicBlock> getPredecessors() {
        return Collections.unmodifiableList(predecessors);
    }

    /**
     * Adds a successor edge and mirrors it on the target's predecessor list.
     * Prevents self-loops and duplicate entries.
     */
    void addSuccessor(BasicBlock block) {
        if (block == null || block == this || successors.contains(block)) {
            return;
        }

        successors.add(block);
        block.addPredecessorInternal(this);
    }

    /**
     * Internal helper so only the owning block can mutate its predecessors.
     */
    private void addPredecessorInternal(BasicBlock block) {
        if (block == null || predecessors.contains(block)) {
            // Mirror guard from addSuccessor to keep the adjacency lists symmetric.
            return;
        }

        predecessors.add(block);
    }

    @Override
    public String toString() {
        String labelSuffix = (label == null) ? "" : String.format(":%s", label);
        return String.format("BasicBlock[%d%s] %s", index, labelSuffix, command);
    }
}
