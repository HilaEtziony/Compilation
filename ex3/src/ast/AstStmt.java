package ast;

import types.*;
import symboltable.*;

public class AstStmt extends AstNode
{
	/****************************************************/
	/* The printing message for a stmt AST node */
	/****************************************************/
	public void printMe()
	{
		/*****************************/
		/* AST NODE TYPE = STMT */
		/*****************************/
		System.out.print("AST NODE STMT\n");

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AstGraphviz.getInstance().logNode(
                serialNumber,
			"STMT");
	}

	public Type semantMe()
	{
		// TODO
		return null;
	}
}
