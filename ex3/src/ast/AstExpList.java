package ast;

import types.*;
import symboltable.*;
import semanticError.SemanticErrorException;

/*
USAGE:
	| exp:e COMMA expList:l											{: RESULT = new AstExpList(e,l);    				:}
	| exp:e															{: RESULT = new AstExpList(e,null);    				:}
*/

public class AstExpList extends AstDec
{
    public AstExp head;
    public AstExpList tail;

    public AstExpList(AstExp head, AstExpList tail, int lineNumber)
    {
        serialNumber = AstNodeSerialNumber.getFresh();
		this.lineNumber = lineNumber;
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

    /***********************************************/
    /* Return the i-th element in the list         */
    /***********************************************/
    public AstExp get(int i)
    {
        if (i == 0) return head;
        return tail.get(i - 1);
    }

    /***********************************************/
    /* Return the number of elements in the list  */
    /***********************************************/
    public int size()
    {
        if (tail == null) return 1;
        return 1 + tail.size();
    }

	public Type semantMe()
	{
		/***********************************************/
		/* [1] Evaluate the head expression           */
		/***********************************************/
		if (head != null)
			head.semantMe();

		/***********************************************/
		/* [2] Recursively evaluate the tail list     */
		/***********************************************/
		if (tail != null)
			tail.semantMe();

		/***********************************************/
		/* [3] Return null because the list itself    */
		/* does not have a single type               */
		/***********************************************/
		return null;
	}
}

