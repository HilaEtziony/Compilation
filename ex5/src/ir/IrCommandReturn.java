package ir;

import java.util.*;
import temp.*;

public class IrCommandReturn extends IrCommand {
    public Temp res;

    public IrCommandReturn(Temp res) {
        this.res = res;
    }

    @Override
    public Set<Temp> use() { return res != null ? Collections.singleton(res) : Collections.emptySet(); }

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
