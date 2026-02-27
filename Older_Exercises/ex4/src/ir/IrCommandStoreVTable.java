package ir;

import temp.Temp;

public class IrCommandStoreVTable extends IrCommand {
    public Temp dstObj;
    public String vtableName;

    public IrCommandStoreVTable(Temp dstObj, String vtableName) {
        this.dstObj = dstObj;
        this.vtableName = vtableName;
    }

    @Override
    public String toString() {
        return String.format("store_vtable(obj=%s, table=%s)", dstObj, vtableName);
    }
}