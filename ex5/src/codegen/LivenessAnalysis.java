package codegen;

import java.util.*;
import ir.*;
import temp.*;

/**
 * Backward dataflow liveness analysis over the CFG.
 *
 * Each BasicBlock wraps exactly one IrCommand.
 * For every block we compute:
 *   live_in[B]  = use[B] ∪ (live_out[B] \ def[B])
 *   live_out[B] = ∪ { live_in[S] | S ∈ successors(B) }
 *
 * Iterates until a fixed point is reached.
 */
public class LivenessAnalysis
{
    private final Map<BasicBlock, Set<Temp>> liveIn  = new HashMap<>();
    private final Map<BasicBlock, Set<Temp>> liveOut = new HashMap<>();

    public LivenessAnalysis(Graph cfg)
    {
        List<BasicBlock> blocks = cfg.getBlocks();
        if (blocks == null || blocks.isEmpty()) return;

        for (BasicBlock b : blocks)
        {
            liveIn.put(b,  new HashSet<>());
            liveOut.put(b, new HashSet<>());
        }

        boolean changed = true;
        while (changed)
        {
            changed = false;
            for (int i = blocks.size() - 1; i >= 0; i--)
            {
                BasicBlock b = blocks.get(i);
                IrCommand cmd = b.getCommand();

                Set<Temp> newOut = new HashSet<>();
                for (BasicBlock succ : b.getSuccessors())
                {
                    newOut.addAll(liveIn.get(succ));
                }

                Set<Temp> newIn = new HashSet<>(newOut);
                newIn.removeAll(cmd.def());
                newIn.addAll(cmd.use());

                if (!newIn.equals(liveIn.get(b)) || !newOut.equals(liveOut.get(b)))
                {
                    liveIn.put(b, newIn);
                    liveOut.put(b, newOut);
                    changed = true;
                }
            }
        }
    }

    public Set<Temp> getLiveIn(BasicBlock b)
    {
        return Collections.unmodifiableSet(liveIn.getOrDefault(b, Collections.emptySet()));
    }

    public Set<Temp> getLiveOut(BasicBlock b)
    {
        return Collections.unmodifiableSet(liveOut.getOrDefault(b, Collections.emptySet()));
    }
}
