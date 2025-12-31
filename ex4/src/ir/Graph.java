package ir;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {
    private final List<BasicBlock> blocks = new ArrayList<>();
    private final Map<String, BasicBlock> labelToBlock = new HashMap<>();
    private BasicBlock entryBlock;
    private BasicBlock exitBlock;

    public static Graph fromIr(Ir ir) {
        if (ir == null) {
            throw new IllegalArgumentException("IR instance can't be null");
        }
        Graph graph = new Graph();
        graph.build(ir.getCommands());
        return graph;
    }

    private void build(List<IrCommand> commands) {
        blocks.clear();
        labelToBlock.clear();
        entryBlock = null;
        exitBlock = null;

        if (commands == null || commands.isEmpty()) {
            return;
        }

        List<BasicBlock> createdBlocks = new ArrayList<>(commands.size());
        for (int i = 0; i < commands.size(); i++) {
            BasicBlock block = new BasicBlock(i, commands.get(i));
            createdBlocks.add(block);
            if (block.getLabel() != null) {
                labelToBlock.put(block.getLabel(), block);
            }
        }

        connectBlocks(createdBlocks);
        blocks.addAll(createdBlocks);
        entryBlock = blocks.get(0);
        exitBlock = blocks.get(blocks.size() - 1);
    }

    private void connectBlocks(List<BasicBlock> createdBlocks) {
        for (int i = 0; i < createdBlocks.size(); i++) {
            BasicBlock block = createdBlocks.get(i);
            IrCommand command = block.getCommand();

            if (command instanceof IrCommandJumpLabel) {
                BasicBlock target = labelToBlock.get(((IrCommandJumpLabel) command).labelName);
                if (target != null) {
                    block.addSuccessor(target);
                }
            } else if (command instanceof IrCommandJumpIfEqToZero) {
                IrCommandJumpIfEqToZero jumpIf = (IrCommandJumpIfEqToZero) command;
                BasicBlock taken = labelToBlock.get(jumpIf.labelName);
                if (taken != null) {
                    block.addSuccessor(taken);
                }
                BasicBlock fallThrough = nextBlock(createdBlocks, i);
                if (fallThrough != null) {
                    block.addSuccessor(fallThrough);
                }
            } else if (command instanceof IrCommandReturn || command instanceof IrCommandEpilogue) {
                // Terminal node. no successors.
            } else {
                BasicBlock fallThrough = nextBlock(createdBlocks, i);
                if (fallThrough != null) {
                    block.addSuccessor(fallThrough);
                }
            }
        }
    }

    private BasicBlock nextBlock(List<BasicBlock> blocks, int index) {
        int next = index + 1;
        return (next < blocks.size()) ? blocks.get(next) : null;
    }

    public List<BasicBlock> getBlocks() {
        return Collections.unmodifiableList(blocks);
    }

    public BasicBlock getEntryBlock() {
        return entryBlock;
    }

    public BasicBlock getExitBlock() {
        return exitBlock;
    }

    public BasicBlock getBlockForLabel(String label) {
        return labelToBlock.get(label);
    }
}
