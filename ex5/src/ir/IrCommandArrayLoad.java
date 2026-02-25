package ir;

import java.util.*;
import temp.Temp;

public class IrCommandArrayLoad extends IrCommand
{
    public Temp dst;
    public Temp base;
    public Temp index;

    public IrCommandArrayLoad(Temp dst, Temp base, Temp index)
    {
        this.dst   = dst;
        this.base  = base;
        this.index = index;
    }

    @Override
    public Set<Temp> def() { return Collections.singleton(dst); }

    @Override
    public Set<Temp> use()
    {
        Set<Temp> s = new HashSet<>();
        if (base != null) s.add(base);
        if (index != null) s.add(index);
        return s;
    }

    @Override
    public String toString()
    {
        return String.format("%s := %s[%s]", dst, base, index);
    }

}