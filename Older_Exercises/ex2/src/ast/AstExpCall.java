package ast;

public class AstExpCall extends AstExp
{
    public AstVar var;
    public String id;
    public AstExpList expList;

    public AstExpCall(AstVar var, String id, AstExpList expList)
    {
        // TODO get line num
        serialNumber = AstNodeSerialNumber.getFresh();

        this.var = var;
        this.id = id;
        this.expList = expList;
    }

    public void printMe()
    {
        /**************************************/
        /* AST NODE TYPE = AST CALL EXPRESSION */
        /**************************************/
        System.out.print("AST NODE CALL EXPRESSION\n");

        /*************************************/
        /* RECURSIVELY PRINT VAR + EXPLIST ... */
        /*************************************/
        if (var != null) var.printMe();
        if (expList != null) expList.printMe();

        /**********************************/
        /* PRINT to AST GRAPHVIZ DOT file */
        /**********************************/
        AstGraphviz.getInstance().logNode(
                serialNumber,
            "CALL\nEXPRESSION\n" + id);
        
        /****************************************/
        /* PRINT Edges to AST GRAPHVIZ DOT file */
        /****************************************/
        if (var != null) AstGraphviz.getInstance().logEdge(serialNumber,var.serialNumber);
        if (expList != null) AstGraphviz.getInstance().logEdge(serialNumber,expList.serialNumber);
    }
}

/*
USAGES:

callExp 	::= 	var:v DOT ID:i LPAREN expList:l RPAREN 							{: RESULT = new AstCallExp(v,i,l);    				:}
					| var:v DOT ID:i LPAREN RPAREN									{: RESULT = new AstCallExp(v,i,null);    			:}
					| ID:i LPAREN expList:l RPAREN 									{: RESULT = new AstCallExp(null,i,l);    			:}
					| ID:i LPAREN RPAREN											{: RESULT = new AstCallExp(null,i,null);    		:}
*/