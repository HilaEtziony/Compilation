package ir;

import java.util.*;
import temp.Temp;

public class IrCommandArrayStore extends IrCommand
{
    public Temp base;
    public Temp index;
    public Temp src;

    public IrCommandArrayStore(Temp base, Temp index, Temp src)
    {
        this.base  = base;
        this.index = index;
        this.src   = src;
    }

    @Override
    public Set<Temp> use()
    {
        Set<Temp> s = new HashSet<>();
        if (base != null) s.add(base);
        if (index != null) s.add(index);
        if (src != null) s.add(src);
        return s;
    }

    @Override
    public String toString()
    {
        return String.format("%s[%s] := %s", base, index, src);
    }
}