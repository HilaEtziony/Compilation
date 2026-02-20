package ir;

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
    public String toString()
    {
        return String.format("%s[%s] := %s", base, index, src);
    }

    @Override
    public void mipsMe()
    {
        // TODO
    }
}