package ast;

import types.*;
import temp.*;

/*
USAGE:
	| callExp:c SEMICOLON											{: RESULT = new AstStmtCall(c); 					:}
*/

public class AstStmtCall extends AstStmt
{
	/***************/
	/*  var := exp */
	/***************/
	public AstExpCall expCall;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AstStmtCall(AstExpCall expCall, int lineNumber)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		serialNumber = AstNodeSerialNumber.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.print("====================== stmt -> var ASSIGN exp SEMICOLON\n");

		/*******************************/
		/* COPY INPUT DATA MEMBERS ... */
		/*******************************/
		// this.var = var;
		this.lineNumber = lineNumber;
		this.expCall = expCall;
	}

	public void printMe()
		{
			expCall.printMe();

			/***************************************/
			/* PRINT Node to AST GRAPHVIZ DOT file */
			/***************************************/
			AstGraphviz.getInstance().logNode(
					serialNumber,
				String.format("STMT\nCALL"));
			
			/****************************************/
			/* PRINT Edges to AST GRAPHVIZ DOT file */
			/****************************************/
			AstGraphviz.getInstance().logEdge(serialNumber,expCall.serialNumber);
	}

	public Type semantMe()
	{
		return expCall.semantMe();
	}

	public Temp irMe()
	{
		if (expCall != null) expCall.irMe();

		return null;
	}
}
