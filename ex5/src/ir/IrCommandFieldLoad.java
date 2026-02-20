/***********/
/* PACKAGE */
/***********/
package ir;

/*******************/
/* PROJECT IMPORTS */
/*******************/
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
    public String toString()
    {
        return String.format("%s := %s.field[%d]", dst, base, offset);
    }

    @Override
    public void mipsMe()
    {
        // TODO
    }
}