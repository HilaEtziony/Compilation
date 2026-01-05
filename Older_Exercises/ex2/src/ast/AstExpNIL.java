package ast;

public class AstExpNIL extends AstExp
{
    public AstExpNIL()
    {
        // TODO get line num
        serialNumber = AstNodeSerialNumber.getFresh();
    }

    public void printMe()
    {
        /**************************************/
        /* AST NODE TYPE = AST NIL EXPRESSION */
        /**************************************/
        System.out.print("AST NODE NIL EXPRESSION\n");

        /**********************************/
        /* PRINT to AST GRAPHVIZ DOT file */
        /**********************************/
        AstGraphviz.getInstance().logNode(
                serialNumber,
            "NIL\n");
    }
}
