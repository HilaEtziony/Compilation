package ast;

import types.*;
import symboltable.*;

/*
USAGE:
	| exp:e COMMA expList:l											{: RESULT = new AstExpList(e,l);    				:}
	| exp:e															{: RESULT = new AstExpList(e,null);    				:}
*/

public class AstExpList extends AstDec
{
    public AstExp head;
    public AstExpList tail;

    public AstExpList(AstExp head, AstExpList tail)
    {
        // TODO get line num
        serialNumber = AstNodeSerialNumber.getFresh();

        this.head = head;
        this.tail = tail;
    }

    /*******************************************************/
	/* The printing message for a expression list AST node */
	/*******************************************************/
	public void printMe()
	{
		/********************************/
		/* AST NODE TYPE = AST EXP LIST */
		/********************************/
		System.out.print("AST NODE EXP LIST\n");

		/*************************************/
		/* RECURSIVELY PRINT HEAD + TAIL ... */
		/*************************************/
		if (head != null) head.printMe();
		if (tail != null) tail.printMe();

		/**********************************/
		/* PRINT to AST GRAPHVIZ DOT file */
		/**********************************/
		AstGraphviz.getInstance().logNode(
				serialNumber,
			"EXP\nLIST\n");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (head != null) AstGraphviz.getInstance().logEdge(serialNumber,head.serialNumber);
		if (tail != null) AstGraphviz.getInstance().logEdge(serialNumber,tail.serialNumber);
	}
}

