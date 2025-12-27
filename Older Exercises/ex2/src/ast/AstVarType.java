package ast;

public class AstVarType extends AstDec
{
    public String type;

    public AstVarType(String type)
    {
        // TODO get line num
        serialNumber = AstNodeSerialNumber.getFresh();
        
        this.type = type;
    }

    public void printMe()
    {
        /**************************************/
        /* AST NODE TYPE = AST VARIABLE TYPE */
        /**************************************/
        System.out.print("AST NODE VAR TYPE\n");

        /**********************************/
        /* PRINT to AST GRAPHVIZ DOT file */
        /**********************************/
        AstGraphviz.getInstance().logNode(
                serialNumber,
            "TYPE\n" + type);
    }
}