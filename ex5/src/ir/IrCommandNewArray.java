package ir;

import java.util.*;
import temp.Temp;

public class IrCommandNewArray extends IrCommand {
    public Temp dst;
    public Temp size;

    public IrCommandNewArray(Temp dst, Temp size) {
        this.dst = dst;
        this.size = size;
    }

    @Override
    public Set<Temp> def() { return Collections.singleton(dst); }

    @Override
    public Set<Temp> use() { return size != null ? Collections.singleton(size) : Collections.emptySet(); }

    @Override
    public String toString() {
        return String.format("%s := new_array(%s)", dst, size);
    }
}