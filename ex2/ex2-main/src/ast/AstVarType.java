package ast;

public abstract class AstVarType extends AstDec
{
    public String type;

    public AstVarType(String type)
    {
        // TODO get line num
        serialNumber = AstNodeSerialNumber.getFresh();
        
        this.type = type;
    }
}