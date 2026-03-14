package ir;

import mips.MipsGenerator;
import temp.*;

public class IrCommandStringConcat extends IrCommandBinop {
    public IrCommandStringConcat(Temp dst, Temp t1, Temp t2) {
        super(dst, t1, t2);
    }

    @Override
    public String toString() {
        return String.format("%s := %s + (string) %s", dst, t1, t2);
    }

    @Override
    public void mipsMe() {
        MipsGenerator.getInstance().stringConcat(dst, t1, t2);
    }
}