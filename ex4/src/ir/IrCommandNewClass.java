package ir;

import temp.Temp;

public class IrCommandNewClass extends IrCommand {
    public Temp dst;
    public int size; 

    public IrCommandNewClass(Temp dst, int size) {
        this.dst = dst;
        this.size = size;
    }

    @Override
    public String toString() {
        return String.format("%s := new_class(size=%d)", dst, size);
    }
}