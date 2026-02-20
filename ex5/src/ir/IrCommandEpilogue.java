package ir;

public class IrCommandEpilogue extends IrCommand {
    public String funcName;

    public IrCommandEpilogue(String funcName) {
        this.funcName = funcName;
    }

    @Override
    public String toString() {
        return String.format("epilogue %s", funcName);
    }

    @Override
    public void mipsMe() 
    {
        // TODO
    }
}