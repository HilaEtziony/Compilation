package ast;

public abstract class AstClassDec extends AstDec
{
    public ID class;

    public AstClassDec(ID class)
    {
        // TODO get line num
        serialNumber = AstNodeSerialNumber.getFresh();

        this.class = class;
    }
}
