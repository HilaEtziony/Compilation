/***********/
/* PACKAGE */
/***********/
package ir;

/*******************/
/* PROJECT IMPORTS */
/*******************/
import java.util.*;
import temp.*;


public class IrCommandFieldStore extends IrCommand
{
    public Temp base;
    public int offset;
    public Temp src;

    public IrCommandFieldStore(Temp base, int offset, Temp src)
    {
        this.base   = base;
        this.offset = offset;
        this.src    = src;
    }

    @Override
    public Set<Temp> use()
    {
        Set<Temp> s = new HashSet<>();
        if (base != null) s.add(base);
        if (src != null) s.add(src);
        return s;
    }

    @Override
    public String toString()
    {
        return String.format("%s.field[%d] := %s", base, offset, src);
    }
}