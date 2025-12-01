package ast;

import types.*;
import symboltable.*;

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

    /************************************************/
	/* The printing message for a call exp AST node */
	/************************************************/
	public void printMe()
	{
		/********************************/
		/* AST NODE TYPE = AST CALL EXP */
		/********************************/
		System.out.format("CALL(%s)\nWITH:\n",id);

		/***************************************/
		/* RECURSIVELY PRINT expList + body ... */
		/***************************************/
		if (expList != null) expList.printMe();
		
		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AstGraphviz.getInstance().logNode(
                serialNumber,
			String.format("CALL(%s)\nWITH",id));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		AstGraphviz.getInstance().logEdge(serialNumber,expList.serialNumber);
	}
}

/*
USAGES:

callExp 	::= 	var:v DOT ID:i LPAREN expList:l RPAREN 							{: RESULT = new AstCallExp(v,i,l);    				:}
					| var:v DOT ID:i LPAREN RPAREN									{: RESULT = new AstCallExp(v,i,null);    			:}
					| ID:i LPAREN expList:l RPAREN 									{: RESULT = new AstCallExp(null,i,l);    			:}
					| ID:i LPAREN RPAREN											{: RESULT = new AstCallExp(null,i,null);    		:}
*/