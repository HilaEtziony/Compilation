package ast;

import types.*;
import symboltable.*;

/*
USAGE:
	| ARRAY ID:i EQ type:t LBRACK RBRACK SEMICOLON					{: RESULT = new AstDecArray(i,t); 					:}
*/

public class AstDecArray extends AstDec
{
    public String identifier;
    public AstVarType type;

    public AstDecArray(String identifier, AstVarType type)
    {
        // TODO get line num
        serialNumber = AstNodeSerialNumber.getFresh();
        
        this.identifier = identifier;
        this.type = type;
    }

	/************************************************************/
	/* The printing message for an array declaration AST node */
	/************************************************************/
	public void printMe()
	{
		/****************************************/
		/* AST NODE TYPE = AST ARRAY DECLARATION */
		/****************************************/
		System.out.format("ARRAY-DEC(%s):%s\n", identifier, type.type);

		/**************************************/
		/* RECURSIVELY PRINT type ... */
		/**************************************/
		if (type != null) type.printMe();

		/**********************************/
		/* PRINT to AST GRAPHVIZ DOT file */
		/**********************************/
		AstGraphviz.getInstance().logNode(
                serialNumber,
			String.format("ARRAY\nDEC(%s)\n:%s", identifier, type.type));

		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (type != null) AstGraphviz.getInstance().logEdge(serialNumber, type.serialNumber);
	}
}