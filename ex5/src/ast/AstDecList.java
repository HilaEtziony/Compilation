package ast;

import temp.*;
import types.*;

/*
USAGE:
	| dec:d	decList:l												{: RESULT = new AstDecList(d,l);    				:}
	| dec:d															{: RESULT = new AstDecList(d,null); 				:}
	| cField:c	cFieldList:l										{: RESULT = new AstDecList(c,l);    				:}
	| cField:c														{: RESULT = new AstDecList(c,null); 				:}
*/

public class AstDecList extends AstStmt
{
    /****************/
    /* DATA MEMBERS */
    /****************/
    public AstDec head;
    public AstDecList tail;

    /******************/
    /* CONSTRUCTOR(S) */
    /******************/
    public AstDecList(AstDec head, AstDecList tail)
    {
        serialNumber = AstNodeSerialNumber.getFresh();
        if (tail != null) System.out.print("====================== decList -> dec decList\n");
        if (tail == null) System.out.print("====================== decList -> dec         \n");

        this.head = head;
        this.tail = tail;
    }

	/********************************************************/
	/* The printing message for a declaration list AST node */
	/********************************************************/
	public void printMe()
	{
		/********************************/
		/* AST NODE TYPE = AST DEC LIST */
		/********************************/
		System.out.print("AST NODE DEC LIST\n");

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
			"DEC\nLIST\n");

		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (head != null) AstGraphviz.getInstance().logEdge(serialNumber,head.serialNumber);
		if (tail != null) AstGraphviz.getInstance().logEdge(serialNumber,tail.serialNumber);
	}

	public Type semantMe()
	{
		// First pass: definitions (classes, arrays, global variables) - in order of appearance
		for (AstDecList it = this; it != null; it = it.tail)
		{
			if (it.head != null && !(it.head instanceof AstDecFunc))
			{
				it.head.semantMe();
			}
		}

		// Second pass: function bodies (which can now use all previously defined)
		for (AstDecList it = this; it != null; it = it.tail)
		{
			if (it.head != null && (it.head instanceof AstDecFunc))
			{
				it.head.semantMe();
			}
		}

		return null;
	}

	public int semantMe(TypeClass container, int offset) {
		int currentOffset = offset;

		if (head != null) {
			if (head instanceof AstDecFunc) {
				// Functions use the 1-arg semantMe(TypeClass) — they don't affect field offset
				((AstDecFunc) head).semantMe(container);
			} else {
				currentOffset = head.semantMe(container, currentOffset);
			}
		}
		
		if (tail != null) {
			return tail.semantMe(container, currentOffset);
		}
		
		return currentOffset;
	}

	public Temp irMe()
	{
			// First pass: definitions (classes, arrays, global variables) - in order of appearance
		for (AstDecList it = this; it != null; it = it.tail)
		{
			if (it.head != null && !(it.head instanceof AstDecFunc))
			{
				it.head.irMe();
			}
		}

		// Second pass: function bodies (which can now use all previously defined)
		for (AstDecList it = this; it != null; it = it.tail)
		{
			if (it.head != null && (it.head instanceof AstDecFunc))
			{
				it.head.irMe();
			}
		}

		return null;	
	}

}