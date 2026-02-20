/***********/
/* PACKAGE */
/***********/
package ir;

/*******************/
/* PROJECT IMPORTS */
/*******************/
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
    public String toString()
    {
        return String.format("%s.field[%d] := %s", base, offset, src);
    }

    @Override
    public void mipsMe()
    {
        // TODO
    }
}