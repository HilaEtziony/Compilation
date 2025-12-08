package ast;

public class AstNewExp extends AstExp
{
    public AstVarType type;
    public AstExp exp;

    public AstNewExp(AstVarType type, AstExp exp)
    {
        // TODO get line num
        serialNumber = AstNodeSerialNumber.getFresh();

        this.type = type;
        this.exp = exp;
    }

    public void printMe()
    {
        /**************************************/
        /* AST NODE TYPE = AST NEW EXPRESSION */
        /**************************************/
        System.out.print("AST NODE NEW EXP\n");

        /*************************************/
        /* RECURSIVELY PRINT TYPE ... */
        /*************************************/
        if (type != null) type.printMe();

        /*************************************/
        /* RECURSIVELY PRINT EXP ... */
        /*************************************/
        if (exp != null) exp.printMe();

        /**********************************/
        /* PRINT to AST GRAPHVIZ DOT file */
        /**********************************/
        AstGraphviz.getInstance().logNode(
                serialNumber,
            "NEW\nEXP");

        /****************************************/
        /* PRINT Edges to AST GRAPHVIZ DOT file */
        /****************************************/
        if (type != null) AstGraphviz.getInstance().logEdge(serialNumber,type.serialNumber);
        if (exp != null) AstGraphviz.getInstance().logEdge(serialNumber,exp.serialNumber);
    }
}

/*
USAGES:

newExp 		::= 	NEW type:t 														{: RESULT = new AstNewExp(t, null); 				:}
					| NEW type:t LBRACK exp:e RBRACK								{: RESULT = new AstNewExp(t, e); 					:}
*/