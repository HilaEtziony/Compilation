package ir;

import temp.Temp;

public class IrCommandNewClass extends IrCommand {
    public Temp dst;
    public int size; 

    public IrCommandNewClass(Temp dst, int size) {
        this.dst = dst;
        this.size = size;
    }
}