package ir;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BasicBlock {
    private final int index;
    private final IrCommand command;
    private final String label;
    private final List<BasicBlock> successors = new ArrayList<>();
    private final List<BasicBlock> predecessors = new ArrayList<>();

    public BasicBlock(int index, IrCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("IR command cannot be null");
        }
        this.index = index;
        this.command = command;
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

    void addSuccessor(BasicBlock block) {
        if (block == null || block == this || successors.contains(block)) {
            return;
        }
        successors.add(block);
        block.addPredecessorInternal(this);
    }

    private void addPredecessorInternal(BasicBlock block) {
        if (block == null || predecessors.contains(block)) {
            return;
        }
        predecessors.add(block);
    }

    public String toString() {
        String labelName = (label == null) ? String.format("cmd_%d", index) : label;
        return String.format("BasicBlock[%s -> %s]", labelName, command.getClass().getSimpleName());
    }
}
