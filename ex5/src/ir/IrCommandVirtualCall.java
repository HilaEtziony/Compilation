package ir;

import java.util.*;
import mips.MipsGenerator;
import temp.Temp;
import temp.TempList;

public class IrCommandVirtualCall extends IrCommand {
    public Temp dst;
    public Temp obj;
    public int offset;
    public TempList args;

    public IrCommandVirtualCall(Temp dst, Temp obj, int offset, TempList args) {
        this.dst = dst;
        this.obj = obj;
        this.offset = offset;
        this.args = args;
    }

    @Override
    public Set<Temp> def() { return dst != null ? Collections.singleton(dst) : Collections.emptySet(); }

    @Override
    public Set<Temp> use()
    {
        Set<Temp> s = new HashSet<>();
        if (obj != null) s.add(obj);
        for (TempList tl = args; tl != null; tl = tl.tail)
        {
            if (tl.head != null) s.add(tl.head);
        }
        return s;
    }

    @Override
    public String toString() {
        return String.format("%s := virtual_call %s(offset %d) with args: %s",
                dst != null ? dst.toString() : "void",
                obj.toString(),
                offset,
                args != null ? args.toString() : "none");
    }

    @Override
    public void mipsMe() {
        MipsGenerator.getInstance().virtualCall(dst, obj, offset, args);
    }
}
