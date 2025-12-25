package ir;

import temp.*;

public class IrCommandReturn extends IrCommand {
    public Temp res;

    public IrCommandReturn(Temp res) {
        this.res = res;
    }
}