import ir.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.Collections;

public class UseBeforeDefCheck {
    private final Graph cfg;

    public UseBeforeDefCheck(Graph cfg) {
        if (cfg == null) {
            throw new IllegalArgumentException("CFG instance can't be null");
        }
        this.cfg = cfg;
    }

    private Set<String> monotoneLatticeFunc(Set<String> in_set, IrCommand command){
        Set<String> out_set = new HashSet<>(in_set);
        
        if(command instanceof IrCommandConstInt){
            IrCommandConstInt cmd = (IrCommandConstInt) command;
            out_set.add(cmd.getTemp().toString());
        }
        else if (command instanceof IrCommandStore){
            IrCommandStore cmd = (IrCommandStore) command;
            String varName = cmd.getVarName();
            String srcTemp = cmd.getSrc().toString();
            // Check if the source temp is defined
            if(out_set.contains(srcTemp.toString())){
                out_set.add(varName);
            }
        }
        else if (command instanceof IrCommandLoad){
            IrCommandLoad cmd = (IrCommandLoad) command;

            if(out_set.contains(cmd.getVarName())){
                out_set.add(cmd.getDst().toString());
            }
        }
        
        

        return out_set;
    }

    private Set<String> setIntersection(Set<String> set1, Set<String> set2){
        // if one of the sets is null, return the other set
        if(set1 == null) return set2;
        if(set2 == null) return set1;

        // compute intersection
        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        return intersection;
    }

    private Set<String>[] createLattice(){
        // initialize lattice and worklist
        Set<String>[] lattice = new Set[cfg.getBlocks().size()];
        Queue<BasicBlock> worklist = new LinkedList<>();
        BasicBlock temp;

        //debug prints
        System.err.println("CFG has " + cfg.getBlocks().size() + " blocks.");
        System.err.println("lattice has size " + lattice.length);
        System.out.println("Starting use-before-def analysis...");

        // add entry block to worklist, and process until worklist is empty
        worklist.add(cfg.getEntryBlock());
        while(!worklist.isEmpty()){
            // get next block from worklist
            temp = worklist.poll();
            System.out.println("Visiting block " + temp.getIndex() + ": " + temp.getCommand().toString());
            
            // get or create in_set for the block
            Set<String> in_set = lattice[temp.getIndex()];
            if(in_set == null){
                in_set = new HashSet<String>();
                lattice[temp.getIndex()] = in_set;
            }
            // Process the command to update in_set and out_set
            IrCommand command = temp.getCommand();   
            Set<String> out_set = monotoneLatticeFunc(in_set, command);
            System.out.println("in_set: " + in_set.toString());
            System.out.println("out_set: " + out_set.toString());

            for(BasicBlock succ : temp.getSuccessors()){
                Set<String> in_block = lattice[succ.getIndex()];
                lattice[succ.getIndex()] = setIntersection(in_block, out_set);
                
                if(in_block == null){ // if never visited before, add to worklist
                    worklist.add(succ);
                }
                else if(!lattice[succ.getIndex()].equals(in_block)){ // if changed, add to worklist
                    worklist.add(succ);
                }
            }
        }
        return lattice;
    }

    public ArrayList<String> useBeforeDef(){
        System.out.println("Running use-before-def analysis...");
        Set<String>[] lattice = createLattice();
        ArrayList<String> errors;
        Set<String> errorSet = new HashSet<>();

        // After analysis, check for use-before-def
        for(BasicBlock block : cfg.getBlocks()){
            Set<String> in_set = lattice[block.getIndex()];
            IrCommand command = block.getCommand();
            if(command instanceof IrCommandLoad){
                IrCommandLoad cmd = (IrCommandLoad) command;
                String varName = cmd.getVarName();
                if(!in_set.contains(varName)){
                    errorSet.add(varName);
                }
            }
        }
        errors = new ArrayList<>(errorSet);
        Collections.sort(errors);

        return errors;
    }
    

}

