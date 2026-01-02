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
            throw new IllegalArgumentException("IR command can't be null");
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

    @Override
    public String toString() {
        String labelSuffix = (label == null) ? "" : String.format(":%s", label);
        return String.format("BasicBlock[%d%s] %s", index, labelSuffix, command);
    }
}
