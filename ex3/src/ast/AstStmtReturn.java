package ast;

import types.*;
import symboltable.*;

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
	public AstStmtReturn(AstExp exp)
	{
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
		// return statements can only be found inside functions.
		if (!SymbolTable.getInstance().isInFunction())
		{
			System.out.format("ERROR: return statement is not inside a function\n");
			System.exit(0);
		}

		// If a function has return type void, its return statements must be empty (return;).
		if (exp != null && SymbolTable.getInstance().getCurrentFunctionReturnType() == TypeVoid.getInstance())
		{
			System.out.format("ERROR: return with a value in a void function\n");
			System.exit(0);
		}

		// If a function has a non-void return type T, then every return statement must return an expression
		if (exp == null && SymbolTable.getInstance().getCurrentFunctionReturnType() != TypeVoid.getInstance())
		{
			System.out.format("ERROR: missing return value in a non-void function\n");
			System.exit(0);
		}

		// If a function has a non-void return type T, then every return statement must return an expression
		// whose type is compatible with type T.
		if (!SymbolTable.getInstance().getCurrentFunctionReturnType().isCompatible(exp.semantMe())){
			System.out.format("ERROR: incompatible return type\n");
			System.exit(0);
		}

		Type returnType = null;
		if (exp != null)
		{
			returnType = exp.semantMe();
		}

		else
		{
			returnType = TypeVoid.getInstance();
		}

		/* TODO Since functions can not be nested, it follows that a return statement belongs to exactly 1 function.
		-> SymbolTableEntry of function should have a field of return!
		A function/method may have control flow paths without a return statement, even if the return type of
		the function is not void. */

		return returnType;
	}
}