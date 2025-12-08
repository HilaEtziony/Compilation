package ast;

import types.*;
import symboltable.*;
import semanticError.SemanticErrorException;

/*
USAGE:
	| RETURN exp:e SEMICOLON										{: RESULT = new AstStmtReturn(e); 					:}
	| RETURN SEMICOLON												{: RESULT = new AstStmtReturn(null); 				:}
*/

public class AstStmtReturn extends AstStmt
{
	public AstExp exp;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AstStmtReturn(AstExp exp, int lineNumber)
	{
		this.lineNumber = lineNumber;
		this.exp = exp;
	}

	/********************************************************/
	/* The printing message for a return statement AST node */
	/********************************************************/
	public void printMe()
	{
		/***********************************/
		/* AST NODE TYPE = AST RETURN STMT */
		/***********************************/
		System.out.print("AST NODE STMT RETURN\n");

		/*****************************/
		/* RECURSIVELY PRINT exp ... */
		/*****************************/
		if (exp != null) exp.printMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AstGraphviz.getInstance().logNode(
                serialNumber,
			"RETURN");

		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (exp != null) AstGraphviz.getInstance().logEdge(serialNumber,exp.serialNumber);
	}

	public Type semantMe()
	{
		Type funcRetType = SymbolTable.getInstance().getCurrentFunctionReturnType();
		Type expType = (exp == null) ? TypeVoid.getInstance() : exp.semantMe();

		// return statements can only be found inside functions.
		if (!SymbolTable.getInstance().isInFunction())
		{
			System.out.format("ERROR: return statement is not inside a function\n");
			throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
		}

		// If a function has return type void, its return statements must be empty (return;).
		if (exp != null && funcRetType == TypeVoid.getInstance())
		{
			System.out.format("ERROR: return with a value in a void function\n");
			throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
		}

		// If a function has a non-void return type T, then every return statement must return an expression
		if (exp == null && funcRetType != TypeVoid.getInstance())
		{
			System.out.format("ERROR: missing return value in a non-void function\n");
			throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
		}

		// If a function has a non-void return type T, then every return statement must return an expression
		// whose type is compatible with type T.
		if (!funcRetType.isCompatible(expType)){
			System.out.format("ERROR: incompatible return type\n");
			throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
		}

		/* TODO Since functions can not be nested, it follows that a return statement belongs to exactly 1 function.
		-> SymbolTableEntry of function should have a field of return!
		A function/method may have control flow paths without a return statement, even if the return type of
		the function is not void. */

		return expType;
	}
}