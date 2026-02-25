/***********/
/* PACKAGE */
/***********/
package ir;

/*******************/
/* PROJECT IMPORTS */
/*******************/
import java.util.*;
import temp.*;

public class IrCommandFieldLoad extends IrCommand
{
    public Temp dst;
    public Temp base;
    public int offset;

    public IrCommandFieldLoad(Temp dst, Temp base, int offset)
    {
        this.dst    = dst;
        this.base   = base;
        this.offset = offset;
    }

    @Override
    public Set<Temp> def() { return Collections.singleton(dst); }

    @Override
    public Set<Temp> use() { return Collections.singleton(base); }

    @Override
    public String toString()
    {
        return String.format("%s := %s.field[%d]", dst, base, offset);
    }
}