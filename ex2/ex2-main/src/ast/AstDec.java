package ast;

public abstract class AstDec extends AstStmt
{
    public AstDec()
    {
        // TODO get line num
        serialNumber = AstNodeSerialNumber.getFresh();
    }
}
