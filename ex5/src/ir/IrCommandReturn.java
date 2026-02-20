package ir;

import temp.*;

public class IrCommandReturn extends IrCommand {
    public Temp res;

    public IrCommandReturn(Temp res) {
        this.res = res;
    }

    @Override
    public String toString() {
        if (res == null) {
            return "return";
        }
        return String.format("return %s", res);
    }

    @Override
    public void mipsMe()
    {
        // TODO
    }
}