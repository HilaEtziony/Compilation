package ast;

public class AstExpString extends AstExp
{
    public String string;

    public AstExpString(String string)
    {
        // TODO get line num
        serialNumber = AstNodeSerialNumber.getFresh();
        
        this.string = string;
    }
}