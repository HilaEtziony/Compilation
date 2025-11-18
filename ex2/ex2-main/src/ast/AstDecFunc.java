package ast;

public abstract class AstDecFunc extends AstDec
{
    public AstDec func;

    public AstDecFunc(AstDec dec)
    {
        // TODO get line num
        serialNumber = AstNodeSerialNumber.getFresh();
        
        this.func = dec;
    }
}
