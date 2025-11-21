package ast;

public abstract class AstExpNIL extends AstExp
{
    public AstExpNIL()
    {
        // TODO get line num
        serialNumber = AstNodeSerialNumber.getFresh();
    }
}
