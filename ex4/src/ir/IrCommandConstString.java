package ir;

import temp.Temp;

public class IrCommandConstString extends IrCommand
{
    public Temp dst;
    public String strValue;

    public IrCommandConstString(Temp dst, String strValue)
    {
        this.dst = dst;
        this.strValue = strValue;
    }
}