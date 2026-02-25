package ir;

import java.util.*;
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

    @Override
    public Set<Temp> def() { return Collections.singleton(dst); }

    @Override
    public String toString()
    {
        if (strValue == null) {
            return String.format("%s := null", dst);
        }
        String escaped = strValue.replace("\"", "\\\"");
        return String.format("%s := \"%s\"", dst, escaped);
    }
}