package ast;

public abstract class AstDec extends AstStmt
{
    public AstDec()
    {
        // TODO get line num
        serialNumber = AstNodeSerialNumber.getFresh();
    }

    public void printMe()
    {
          /**********************************/
        /* PRINT to AST GRAPHVIZ DOT file */
        /**********************************/
        AstGraphviz.getInstance().logNode(
                serialNumber,
            "DEC\n");
    }
}

/*
This class is only being derived-from. Need to think about fields that should be shared among all declarations.
*/