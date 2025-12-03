package ast;

import types.*;
import symboltable.*;

public class AstStmtWhile extends AstStmt
{
	public AstExp cond;
	public AstStmtList body;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AstStmtWhile(AstExp cond, AstStmtList body)
	{
		serialNumber = AstNodeSerialNumber.getFresh();
		this.cond = cond;
		this.body = body;
	}

	/*****************************************************/
	/* The printing message for a while statement AST node */
	/*****************************************************/
	public void printMe()
	{
		/*****************************/
		/* AST NODE TYPE = AST WHILE */
		/*****************************/
		System.out.print("AST NODE STMT WHILE\n");

		/**************************************/
		/* RECURSIVELY PRINT cond + body ... */
		/**************************************/
		if (cond != null) cond.printMe();
		if (body != null) body.printMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AstGraphviz.getInstance().logNode(
                serialNumber,
			"WHILE\n(cond)\nDO");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (cond != null) AstGraphviz.getInstance().logEdge(serialNumber,cond.serialNumber);
		if (body != null) AstGraphviz.getInstance().logEdge(serialNumber,body.serialNumber);
	}
}