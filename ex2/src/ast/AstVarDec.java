package ast;

public class AstVarDec extends AstDec
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
    public void printMe()
    {
        /**************************************/
        /* AST NODE TYPE = AST VARIABLE DECLARATION */
        /**************************************/
        System.out.print("AST NODE VAR DEC\n");

        /*************************************/
        /* RECURSIVELY PRINT TYPE ... */
        /*************************************/
        if (type != null) type.printMe();

        /*************************************/
        /* RECURSIVELY PRINT ID ... */
        /*************************************/
        if (id != null) id.printMe();

        /*************************************/
        /* RECURSIVELY PRINT EXPR ... */
        /*************************************/
        if (expr != null) expr.printMe();

        /**********************************/
        /* PRINT to AST GRAPHVIZ DOT file */
        /**********************************/
        AstGraphviz.getInstance().logNode(
                serialNumber,
            "VAR\nDEC");

        /****************************************/
        /* PRINT Edges to AST GRAPHVIZ DOT file */
        /****************************************/
        if (type != null) AstGraphviz.getInstance().logEdge(serialNumber,type.serialNumber);
        if (id != null) AstGraphviz.getInstance().logEdge(serialNumber,id.serialNumber);
        if (expr != null) AstGraphviz.getInstance().logEdge(serialNumber,expr.serialNumber);
    }
}
