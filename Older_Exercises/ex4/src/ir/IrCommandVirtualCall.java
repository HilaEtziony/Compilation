package ir;

import temp.Temp;
import temp.TempList;

public class IrCommandVirtualCall extends IrCommand {
    public Temp dst;           
    public Temp obj;          
    public int offset;       
    public TempList args;     

    public IrCommandVirtualCall(Temp dst, Temp obj, int offset, TempList args) {
        this.dst = dst;
        this.obj = obj;
        this.offset = offset;
        this.args = args;
    }

    @Override
    public String toString() {
        return String.format("%s := virtual_call %s(offset %d) with args: %s", 
                              dst != null ? dst.toString() : "void", 
                              obj.toString(), 
                              offset, 
                              args != null ? args.toString() : "none");
    }
}