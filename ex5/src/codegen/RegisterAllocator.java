package codegen;

import java.util.*;
import ir.*;
import temp.*;

public class RegisterAllocator
{
    private static final int K = 10;
    private static final String[] REGISTERS = {
        "$t0", "$t1", "$t2", "$t3", "$t4",
        "$t5", "$t6", "$t7", "$t8", "$t9"
    };

    private static Map<Temp, String> regTable = new HashMap<>();
    private static Map<Temp, Set<Temp>> interferenceGraph = new HashMap<>();

    public static String getRegister(Temp t)
    {
        if (regTable.containsKey(t))
        {
            return regTable.get(t);
        }
        else
        {
            throw new RuntimeException("Error! Temp " + t + " not allocated to any register.");
        }
    }

    /**
     * Entry point: build interference graph from liveness results, then color.
     */
    public static void allocateRegisters(LivenessAnalysis liveness, Graph cfg)
    {
        interferenceGraph.clear();
        regTable.clear();

        buildInterferenceGraph(liveness, cfg);
        colorGraph();
    }

    /**
     * Build the interference graph:
     * Each two Temps that are both in the same liveOut set get a bidirectional edge.
     */
    private static void buildInterferenceGraph(LivenessAnalysis liveness, Graph cfg)
    {
        List<BasicBlock> blocks = cfg.getBlocks();

        /* Ensure every referenced Temp is a node (even if isolated) */
        for (BasicBlock b : blocks)
        {
            IrCommand cmd = b.getCommand();
            for (Temp t : cmd.def())
            {
                interferenceGraph.putIfAbsent(t, new HashSet<>());
            }
            for (Temp t : cmd.use())
            {
                interferenceGraph.putIfAbsent(t, new HashSet<>());
            }
        }

        /* Add interference edges from liveOut sets */
        for (BasicBlock b : blocks)
        {
            List<Temp> liveList = new ArrayList<>(liveness.getLiveOut(b));

            for (int i = 0; i < liveList.size(); i++)
            {
                for (int j = i + 1; j < liveList.size(); j++)
                {
                    Temp t1 = liveList.get(i);
                    Temp t2 = liveList.get(j);

                    interferenceGraph.get(t1).add(t2);
                    interferenceGraph.get(t2).add(t1);
                }
            }
        }
    }

    /**
     * Graph coloring with optimistic spilling.
     * Simplify phase:
     * If stuck (all remaining nodes have degree >= K), pick the
     * highest-degree node as a potential spill and push it too.
     * Select phase:
     * Pop nodes off the stack and assign the lowest available colour
     * ($t0-$t9) that no already-coloured neighbour uses.
     * If no colour is available → actual spill → throw RuntimeException.
     */
    private static void colorGraph()
    {
        if (interferenceGraph.isEmpty()) return; // no need

        // keep a mapping of current degree for each node
        Map<Temp, Integer> degree = new HashMap<>();
        for (Map.Entry<Temp, Set<Temp>> e : interferenceGraph.entrySet())
        {
            degree.put(e.getKey(), e.getValue().size());
        }

        Set<Temp> nodes_left = new HashSet<>(interferenceGraph.keySet());
        Deque<Temp> stack  = new ArrayDeque<>();

        int totalNodes = interferenceGraph.size();

        /**
         * Simplify phase: repeatedly remove nodes and push onto stack
         */ 

        while (stack.size() < totalNodes)
        {
            // Try to find a non-removed node with degree < K
            // Find max degree in case we need to spill - saves us 
            // from iterating again later
            Temp candidate = null;
            int maxDeg = -1;
            Temp maxDegNode = null;
            for (Temp t : nodes_left)
            {
                if (nodes_left.contains(t) && degree.get(t) < K)
                {
                    candidate = t;
                    break;
                }
                else if (nodes_left.contains(t) && degree.get(t) > maxDeg)
                {
                    maxDeg = degree.get(t);
                    maxDegNode = t;
                }
            }

            // All remaining nodes have degree >= K.
            // Optimistic spill: pick the highest-degree node
            if (candidate == null)
            {
                candidate = maxDegNode;
            }

            // "Remove" candidate: push onto stack and decrement neighbours
            nodes_left.remove(candidate);
            stack.push(candidate);

            for (Temp neighbour : interferenceGraph.get(candidate))
            {
                if (nodes_left.contains(neighbour))
                {
                    degree.put(neighbour, degree.get(neighbour) - 1);
                }
            }
        }

        /**
         * Coloring phase: pop nodes and assign registers
         */

        while (!stack.isEmpty())
        {
            Temp t = stack.pop();

            // Collect colours already used by neighbours (in the ORIGINAL graph)
            Set<String> usedColours = new HashSet<>();
            for (Temp neighbour : interferenceGraph.get(t))
            {
                if (regTable.containsKey(neighbour))
                {
                    usedColours.add(regTable.get(neighbour));
                }
            }

            // Assign the lowest-numbered available register
            String assigned = null;
            for (String reg : REGISTERS)
            {
                if (!usedColours.contains(reg))
                {
                    assigned = reg;
                    break;
                }
            }

            if (assigned == null)
            {
                throw new RuntimeException("Register Allocation Failed");
            }

            regTable.put(t, assigned);
        }
    }

    public static void printInterferenceGraph()
    {
        System.out.println("=== Interference Graph ===");
        for (Map.Entry<Temp, Set<Temp>> entry : interferenceGraph.entrySet())
        {
            System.out.println(entry.getKey() + " interferes with: " + entry.getValue());
        }
        System.out.println("==========================");
    }
}
