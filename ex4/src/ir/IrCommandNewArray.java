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
        return String.format("%s <- malloc(%s * 4 + 4) // Array", dst, size);
    }
}