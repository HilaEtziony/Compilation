package ir;

public class IrCommandEpilogue extends IrCommand {
    public String funcName;

    public IrCommandEpilogue(String funcName) {
        this.funcName = funcName;
    }
}