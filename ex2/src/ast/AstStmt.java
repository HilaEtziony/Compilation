package ast;

public class AstStmt extends AstNode
{
    public AstStmt()
    {
        // TODO get line num
        serialNumber = AstNodeSerialNumber.getFresh();
    }

    public void printMe()
    {
        /**************************************/
        /* AST NODE TYPE = AST STATEMENT */
        /**************************************/
        System.out.print("AST NODE STMT\n");

        /**********************************/
        /* PRINT to AST GRAPHVIZ DOT file */
        /**********************************/
        AstGraphviz.getInstance().logNode(
                serialNumber,
            "STMT");
    }
}
