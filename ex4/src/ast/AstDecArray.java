package ast;

import semanticError.SemanticErrorException;
import symboltable.*;
import temp.Temp;
import types.*;

/*
USAGE:
	| ARRAY ID:i EQ type:t LBRACK RBRACK SEMICOLON					{: RESULT = new AstDecArray(i,t); 					:}
*/

public class AstDecArray extends AstDec
{
    public String identifier;
    public AstVarType type;

    public AstDecArray(String identifier, AstVarType type, int lineNumber)
    {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.lineNumber = lineNumber;
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

	public Type semantMe()
	{
		Type t;

		/****************************/
		/* [1] Check If Type exists */
		/****************************/
		t = SymbolTable.getInstance().find(type.type); 
		if (t == null)
		{
			System.out.format(">> ERROR: type %s does not exist\n",type.type);
			throw new Error("ERROR("+ this.lineNumber +")");
		}

		/****************************/
		/* [2] Check Type is not void */
		/****************************/
		if (t == TypeVoid.getInstance())
		{
			System.out.format(">> ERROR: cannot define array over void\n");
			throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
		}

		/**************************************/
		/* [3] Check That Name does NOT exist */
		/**************************************/
		if (SymbolTable.getInstance().find(identifier) != null)
		{
			System.out.format(">> ERROR: array %s already exists in scope\n",identifier);
			throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
		}

		/************************************************/
		/* [4] Create new array type and enter to table */
		/************************************************/
		Type arrayType = new TypeArray(identifier, t);
		SymbolTable.getInstance().enter(identifier, arrayType);

		/************************************************************/
		/* [5] Return value is irrelevant for type declarations     */
		/************************************************************/
		return null;
	}

	public Temp irMe()
	{
		return null;
	}
}