package ast;

public abstract class AstVarType extends AstDec
{
    public int type;

    public AstVarType(int type)
    {
        // TODO get line num
        serialNumber = AstNodeSerialNumber.getFresh();
        
        this.type = type;
    }
}