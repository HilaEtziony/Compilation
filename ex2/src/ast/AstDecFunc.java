package ast;

public class AstDecFunc extends AstDec
{
    public AstVarType return_type;
    public String identifier;
    public AstTypeIdList func_input; // might be null - don't forget to check whenever using
    public AstStmtList stmnts_of_funs;

    public AstDecFunc(AstVarType return_type, String identifier, AstTypeIdList func_input, AstStmtList stmnts_of_funs){
        // TODO get line num
        serialNumber = AstNodeSerialNumber.getFresh();

        this.return_type = return_type;
        this.identifier = identifier;
        this.func_input = func_input;
        this.stmnts_of_funs = stmnts_of_funs;
    }

    public void printMe()
    {
        /**************************************/
        /* AST NODE TYPE = AST DECLARATION FUNC */
        /**************************************/
        System.out.print("AST NODE DEC FUNC\n");

        if (return_type != null) return_type.printMe();
        if (func_input != null) func_input.printMe();
        if (stmnts_of_funs != null) stmnts_of_funs.printMe();

        /**********************************/
        /* PRINT to AST GRAPHVIZ DOT file */
        /**********************************/
        AstGraphviz.getInstance().logNode(
                serialNumber,
            "DEC\nFUNC\n" + identifier);
        
        /****************************************/
        /* PRINT Edges to AST GRAPHVIZ DOT file */
        /****************************************/
        if (return_type != null) AstGraphviz.getInstance().logEdge(serialNumber,return_type.serialNumber);
        if (func_input != null) AstGraphviz.getInstance().logEdge(serialNumber,func_input.serialNumber);
        if (stmnts_of_funs != null) AstGraphviz.getInstance().logEdge(serialNumber,stmnts_of_funs.serialNumber);
    }
}

/*
accepts:

RESULT = new AstDecFunc(t,i,l,s);
RESULT = new AstDecFunc(t,i,null,s);
*/