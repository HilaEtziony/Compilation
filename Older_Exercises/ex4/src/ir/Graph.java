package ir;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Thin wrapper that takes a linear IR command list and wires it into a
 * bidirectional control-flow graph. The graph is intentionally simple so it
 * can later plug into different DFA engines without extra translation.
 */
public class Graph {
    private final List<BasicBlock> blocks = new ArrayList<>();
    private final Map<String, BasicBlock> labelToBlock = new HashMap<>();
    private BasicBlock entryBlock;
    private BasicBlock exitBlock;

    /**
     * Entry point for the CFG builder. Receives the singleton IR instance,
     * validates it, and delegates to {@link #build(List)}.
     */
    public static Graph fromIr(Ir ir) {
        if (ir == null) {
            throw new IllegalArgumentException("IR instance can't be null");
        }

        Graph graph = new Graph();
        graph.build(ir.getCommands());
        return graph;
    }

    /**
     * Creates one {@link BasicBlock} per IR command, records label-to-block
     * mappings, and marks the entry/exit nodes once the wiring is completed.
     */
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
                // Record jump destinations so the next phase can resolve labels in O(1).
                labelToBlock.put(block.getLabel(), block);
            }
        }

        // Second pass wires edges now that every block (and label) is known.
        connectBlocks(createdBlocks);
        blocks.addAll(createdBlocks);
        entryBlock = blocks.get(0);
        exitBlock = blocks.get(blocks.size() - 1);
    }

    /**
     * Adds successor/predecessor edges according to the command semantics:
     * unconditional/conditional jumps, fall-through, and terminal instructions.
     */
    private void connectBlocks(List<BasicBlock> createdBlocks) {
        for (int i = 0; i < createdBlocks.size(); i++) {
            BasicBlock block = createdBlocks.get(i);
            IrCommand command = block.getCommand();

            if (command instanceof IrCommandJumpLabel) {
                // Unconditional jump: a single outgoing edge to the target label.
                BasicBlock target = labelToBlock.get(((IrCommandJumpLabel) command).labelName);
                if (target != null) {
                    block.addSuccessor(target);
                }

            } else if (command instanceof IrCommandJumpIfEqToZero) {
                // Conditional branch: add both taken (label) and fall-through successors.
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
                // Terminal node: no outgoing edges.
            } else {
                // Default case is straight-line code, so we fall through to the next block.
                BasicBlock fallThrough = nextBlock(createdBlocks, i);
                if (fallThrough != null) {
                    block.addSuccessor(fallThrough);
                }
            }
        }
    }

    /**
     * Helper that returns the sequential successor when fall-through is legal.
     */
    private BasicBlock nextBlock(List<BasicBlock> blocks, int index) {
        int next = index + 1;
        // Returning null signals "no fall-through" (e.g., last command).
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
