package ir;

import temp.Temp;

public class IrCommandNewArray extends IrCommand {
    public Temp dst;
    public Temp size;

    public IrCommandNewArray(Temp dst, Temp size) {
        this.dst = dst;
        this.size = size;
    }

    @Override
    public String toString() {
        return String.format("%s := new_array(%s)", dst, size);
    }
}