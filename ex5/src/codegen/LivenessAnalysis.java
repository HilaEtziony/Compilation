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
    private final Graph cfg;
    private final Map<BasicBlock, Set<Temp>> liveIn;
    private final Map<BasicBlock, Set<Temp>> liveOut;

    public LivenessAnalysis(Graph cfg)
    {
        this.cfg = cfg;
        this.liveIn  = new HashMap<>();
        this.liveOut = new HashMap<>();
        run();
    }

    /* ------------------------------------------------------------------ */
    /*  Public API                                                        */
    /* ------------------------------------------------------------------ */

    public Set<Temp> getLiveIn(BasicBlock b)
    {
        return Collections.unmodifiableSet(liveIn.getOrDefault(b, Collections.emptySet()));
    }

    public Set<Temp> getLiveOut(BasicBlock b)
    {
        return Collections.unmodifiableSet(liveOut.getOrDefault(b, Collections.emptySet()));
    }

    /* ------------------------------------------------------------------ */
    /*  Fixed-point iteration                                             */
    /* ------------------------------------------------------------------ */

    private void run()
    {
        List<BasicBlock> blocks = cfg.getBlocks();
        if (blocks == null || blocks.isEmpty()) return;

        /* initialise empty sets */
        for (BasicBlock b : blocks)
        {
            liveIn.put(b,  new HashSet<>());
            liveOut.put(b, new HashSet<>());
        }

        boolean changed = true;
        while (changed)
        {
            changed = false;
            /* iterate in reverse order for faster convergence on backward analysis */
            for (int i = blocks.size() - 1; i >= 0; i--)
            {
                BasicBlock b = blocks.get(i);

                /* live_out[B] = ∪ live_in[S] for all successors S */
                Set<Temp> newOut = new HashSet<>();
                for (BasicBlock succ : b.getSuccessors())
                {
                    newOut.addAll(liveIn.get(succ));
                }

                /* live_in[B] = use[B] ∪ (live_out[B] \ def[B]) */
                Set<Temp> newIn = new HashSet<>(newOut);
                newIn.removeAll(def(b.getCommand()));
                newIn.addAll(use(b.getCommand()));

                if (!newIn.equals(liveIn.get(b)) || !newOut.equals(liveOut.get(b)))
                {
                    liveIn.put(b, newIn);
                    liveOut.put(b, newOut);
                    changed = true;
                }
            }
        }
    }

    /* ------------------------------------------------------------------ */
    /*  use / def extraction via instanceof checks                        */
    /* ------------------------------------------------------------------ */

    /** Returns the set of Temps DEFINED (written) by this command. */
    public static Set<Temp> def(IrCommand cmd)
    {
        Set<Temp> s = new HashSet<>();

        if (cmd instanceof IrCommandConstInt)
        {
            addNonNull(s, ((IrCommandConstInt) cmd).t);
        }
        else if (cmd instanceof IrCommandConstString)
        {
            addNonNull(s, ((IrCommandConstString) cmd).dst);
        }
        else if (cmd instanceof IrCommandBinop)
        {
            /* covers Add, Sub, Mul, Div, Eq, Lt, Gt */
            addNonNull(s, ((IrCommandBinop) cmd).dst);
        }
        else if (cmd instanceof IrCommandLoad)
        {
            addNonNull(s, ((IrCommandLoad) cmd).getDst());
        }
        else if (cmd instanceof IrCommandLoadFromAddress)
        {
            addNonNull(s, ((IrCommandLoadFromAddress) cmd).dst);
        }
        else if (cmd instanceof IrCommandFieldLoad)
        {
            addNonNull(s, ((IrCommandFieldLoad) cmd).dst);
        }
        else if (cmd instanceof IrCommandArrayLoad)
        {
            addNonNull(s, ((IrCommandArrayLoad) cmd).dst);
        }
        else if (cmd instanceof IrCommandNewClass)
        {
            addNonNull(s, ((IrCommandNewClass) cmd).dst);
        }
        else if (cmd instanceof IrCommandNewArray)
        {
            addNonNull(s, ((IrCommandNewArray) cmd).dst);
        }
        else if (cmd instanceof IrCommandCall)
        {
            addNonNull(s, ((IrCommandCall) cmd).res);
        }
        /* All other commands (Label, Jump, Allocate, Store, Prologue,
           Epilogue, Return, PrintInt, etc.) define nothing. */

        return s;
    }

    /** Returns the set of Temps USED (read) by this command. */
    public static Set<Temp> use(IrCommand cmd)
    {
        Set<Temp> s = new HashSet<>();

        if (cmd instanceof IrCommandBinop)
        {
            /* covers Add, Sub, Mul, Div, Eq, Lt, Gt */
            addNonNull(s, ((IrCommandBinop) cmd).t1);
            addNonNull(s, ((IrCommandBinop) cmd).t2);
        }
        else if (cmd instanceof IrCommandStore)
        {
            addNonNull(s, ((IrCommandStore) cmd).getSrc());
        }
        else if (cmd instanceof IrCommandFieldStore)
        {
            addNonNull(s, ((IrCommandFieldStore) cmd).base);
            addNonNull(s, ((IrCommandFieldStore) cmd).src);
        }
        else if (cmd instanceof IrCommandArrayStore)
        {
            addNonNull(s, ((IrCommandArrayStore) cmd).base);
            addNonNull(s, ((IrCommandArrayStore) cmd).index);
            addNonNull(s, ((IrCommandArrayStore) cmd).src);
        }
        else if (cmd instanceof IrCommandFieldLoad)
        {
            addNonNull(s, ((IrCommandFieldLoad) cmd).base);
        }
        else if (cmd instanceof IrCommandArrayLoad)
        {
            addNonNull(s, ((IrCommandArrayLoad) cmd).base);
            addNonNull(s, ((IrCommandArrayLoad) cmd).index);
        }
        else if (cmd instanceof IrCommandLoadFromAddress)
        {
            addNonNull(s, ((IrCommandLoadFromAddress) cmd).address);
        }
        else if (cmd instanceof IrCommandNewArray)
        {
            addNonNull(s, ((IrCommandNewArray) cmd).size);
        }
        else if (cmd instanceof IrCommandJumpIfEqToZero)
        {
            addNonNull(s, ((IrCommandJumpIfEqToZero) cmd).t);
        }
        else if (cmd instanceof IrCommandPrintInt)
        {
            addNonNull(s, ((IrCommandPrintInt) cmd).t);
        }
        else if (cmd instanceof IrCommandReturn)
        {
            addNonNull(s, ((IrCommandReturn) cmd).res);
        }
        else if (cmd instanceof IrCommandCall)
        {
            IrCommandCall call = (IrCommandCall) cmd;
            addNonNull(s, call.varTemp);
            for (TempList tl = call.args; tl != null; tl = tl.tail)
            {
                addNonNull(s, tl.head);
            }
        }
        /* IrCommandConstInt, IrCommandConstString, IrCommandLoad,
           IrCommandNewClass, IrCommandLabel, IrCommandJumpLabel,
           IrCommandAllocate, IrCommandPrologue, IrCommandEpilogue
           use no temps. */

        return s;
    }

    private static void addNonNull(Set<Temp> set, Temp t)
    {
        if (t != null) set.add(t);
    }
}