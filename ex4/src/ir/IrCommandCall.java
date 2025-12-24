package ir;

import temp.*;

public class IrCommandCall extends IrCommand {
    public Temp res;
    public Temp varTemp;
    public String id;
    public TempList args;

    public IrCommandCall(Temp res, Temp varTemp, String id, TempList args) {
        this.res = res;
        this.varTemp = varTemp;
        this.id = id;
        this.args = args;
    }
}