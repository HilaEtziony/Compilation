package ast;

public abstract class AstExpNIL extends AstNode
{
    public AstExpNIL()
    {
        // TODO get line num
        serialNumber = AstNodeSerialNumber.getFresh();
    }
}
