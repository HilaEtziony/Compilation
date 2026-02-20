package ir;

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
    public String toString()
    {
        return String.format("%s := %s[%s]", dst, base, index);
    }

    @Override
    public void mipsMe()
    {
        // TODO 
    }

}