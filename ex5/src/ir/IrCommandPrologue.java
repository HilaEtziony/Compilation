package ir;

public class IrCommandPrologue extends IrCommand {
    public String funcName;
    public int frameSize; // size of the stack frame for local variables

    public IrCommandPrologue(String funcName, int frameSize) {
        this.funcName = funcName;
        this.frameSize = frameSize;
    }

    @Override
    public String toString() {
        return String.format("prologue %s (frame=%d)", funcName, frameSize);
    }
}