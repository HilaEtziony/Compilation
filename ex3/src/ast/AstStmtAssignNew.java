package ast;

import types.*;
import symboltable.*;
import semanticError.SemanticErrorException;

/*
USAGE:
	| var:v ASSIGN newExp:nExp SEMICOLON							{: RESULT = new AstStmtAssignNew(v,nExp); 			:}
*/

public class AstStmtAssignNew extends AstStmt
{
	/***************/
	/*  var := exp */
	/***************/
	public AstVar var;
	public AstNewExp exp;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AstStmtAssignNew(AstVar var, AstNewExp exp, int lineNumber)
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
		this.lineNumber = lineNumber;
		this.var = var;
		this.exp = exp;
	}

	/*********************************************************/
	/* The printing message for an assign statement AST node */
	/*********************************************************/
	public void printMe()
	{
		/********************************************/
		/* AST NODE TYPE = AST ASSIGNMENT STATEMENT */
		/********************************************/
		System.out.print("AST NODE ASSIGN STMT NEW\n");

		/***********************************/
		/* RECURSIVELY PRINT VAR + EXP ... */
		/***********************************/
		if (var != null) var.printMe();
		if (exp != null) exp.printMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AstGraphviz.getInstance().logNode(
				serialNumber,
			"ASSIGN\nleft := right\n");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		AstGraphviz.getInstance().logEdge(serialNumber,var.serialNumber);
		AstGraphviz.getInstance().logEdge(serialNumber,exp.serialNumber);
	}

	public Type semantMe()
	{
		Type t_var = var.semantMe();
		Type t_new = exp.semantMe(); // Can be array or class type

		/******************************/
		/* [1] Check assignment for new (means class or array) */
		/******************************/
		if (!t_var.isCompatible(t_new)) {
			System.out.format(">> ERROR: cannot assign %s to %s\n", t_new.name, t_var.name);
			throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
		}
		return null;
	}
}
