package ast;

import types.*;
import symboltable.*;
import semanticError.SemanticErrorException;

/*
USAGE:
	| type:t ID:i ASSIGN exp:e SEMICOLON 								{: RESULT = new AstVarDec(t,new AstVarSimple(i),e); 	:}
	| type:t ID:i SEMICOLON											{: RESULT = new AstVarDec(t,new AstVarSimple(i),null); 	:}
	| type:t ID:i ASSIGN newExp:nE SEMICOLON						{: RESULT = new AstVarDec(t,new AstVarSimple(i),nE); 	:}
*/

// TODO rename to AstDecVar, consistency with class's origin
public class AstVarDec extends AstDec
{
    public AstVarType type;
    public AstVarSimple id;
    public AstExp expr;

    public AstVarDec(AstVarType type, AstVarSimple id, AstExp expr, int lineNumber)
    {
        serialNumber = AstNodeSerialNumber.getFresh();
		this.lineNumber = lineNumber;
        this.type = type;
        this.id = id;
        this.expr = expr;
    }

    /************************************************************/
	/* The printing message for a variable declaration AST node */
	/************************************************************/
	public void printMe()
	{
		/****************************************/
		/* AST NODE TYPE = AST VAR DECLARATION */
		/***************************************/
		if (expr != null) System.out.format("VAR-DEC(%s):%s := expr\n",id.name,type.type);
		if (expr == null) System.out.format("VAR-DEC(%s):%s                \n",id.name,type.type);

		/**************************************/
		/* RECURSIVELY PRINT expr ... */
		/**************************************/
		if (expr != null) expr.printMe();

		/**********************************/
		/* PRINT to AST GRAPHVIZ DOT file */
		/**********************************/
		AstGraphviz.getInstance().logNode(
                serialNumber,
			String.format("VAR\nDEC(%s)\n:%s",id.name,type.type));
			// String.format("VAR\nDEC(%s)\n:%s",name,type));  // TODO change AstVarType

		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (expr != null) AstGraphviz.getInstance().logEdge(serialNumber,expr.serialNumber);

	}

	// Target: Ensure no duplicate names in the current scope and that the declared type exists.
	public Type semantMe()
	{
		Type t;

		/****************************/
		/* [1] Check If Type exists */
		/****************************/
		t = SymbolTable.getInstance().find(type.type);
		// t = SymbolTable.getInstance().find(type); // TODO change AstVarType
		if (t == null)
		{
			System.out.format(">> ERROR: non existing type %s\n",type.type);
			// System.out.format(">> ERROR [%d:%d] non existing type %s\n",2,2,type);
			throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
		}

		/****************************/
		/* [2] Check t is void */
		/****************************/
		if (t == TypeVoid.getInstance()) {
			System.out.format(">> ERROR: variable %s cannot be of type void\n", id.name);
			throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
		}

		/**************************************/
		/* [3] Check That Name does NOT exist */
		/**************************************/
		if (SymbolTable.getInstance().find(id.name) != null)
		// if (SymbolTable.getInstance().find(id) != null)
		{
			System.out.format(">> ERROR: variable %s already exists in scope\n",id.name);
			throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
		}

		/************************************************/
		/* [4] Enter the Identifier to the Symbol Table */
		/************************************************/
		SymbolTable.getInstance().enter(id.name,t);

		/************************************************************/
		/* [5] Return value is irrelevant for variable declarations */
		/************************************************************/
		return null;
	}
}
