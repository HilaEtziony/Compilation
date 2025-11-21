package ast;

public abstract class AstVarDec extends AstDec
{
    public AstVarType type;
    public AstVarSimple id;
    public AstExp expr;

    public AstVarDec(AstVarType type, AstVarSimple id, AstExp expr)
    {
        // TODO get line num
        serialNumber = AstNodeSerialNumber.getFresh();
        
        this.type = type;
        this.id = id;
        this.expr = expr;
    }
}
