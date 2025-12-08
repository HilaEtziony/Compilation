package ast;

import types.*;
import symboltable.*;
import semanticError.SemanticErrorException;

/*
USAGE:
	| var:v LBRACK exp:e RBRACK										{: RESULT = new AstVarSubscript(v,e);     			:}
*/

public class AstVarSubscript extends AstVar
{
	public AstVar var;
	public AstExp subscript;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AstVarSubscript(AstVar var, AstExp subscript, int lineNumber)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		serialNumber = AstNodeSerialNumber.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.print("====================== var -> var [ exp ]\n");

		/*******************************/
		/* COPY INPUT DATA MEMBERS ... */
		/*******************************/
		this.lineNumber = lineNumber;
		this.var = var;
		this.subscript = subscript;
	}

	/*****************************************************/
	/* The printing message for a subscript var AST node */
	/*****************************************************/
	public void printMe()
	{
		/*************************************/
		/* AST NODE TYPE = AST SUBSCRIPT VAR */
		/*************************************/
		System.out.print("AST NODE SUBSCRIPT VAR\n");

		/****************************************/
		/* RECURSIVELY PRINT VAR + SUBSCRIPT ... */
		/****************************************/
		if (var != null) var.printMe();
		if (subscript != null) subscript.printMe();
		
		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AstGraphviz.getInstance().logNode(
				serialNumber,
			"SUBSCRIPT\nVAR\n...[...]");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (var       != null) AstGraphviz.getInstance().logEdge(serialNumber,var.serialNumber);
		if (subscript != null) AstGraphviz.getInstance().logEdge(serialNumber,subscript.serialNumber);
	}

	public Type semantMe()
	{
		/**********************************/
		/* [1] Evaluate var type (v)      */
		/**********************************/
		Type varType = var.semantMe();

		if (varType == null || !varType.isArray())
		{
			System.out.format(">> ERROR: variable is not an array type\n");
			throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
		}

		/**********************************/
		/* [2] Evaluate index expression  */
		/**********************************/
		Type subType = subscript.semantMe();

		if (subType != TypeInt.getInstance())
		{
			System.out.format(">> ERROR: array index must be int\n");
			throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
		}

		/************************************************************/
		/* [3] constant index check (if literal) must be >= 0       */
		/************************************************************/
		if (subscript.isConstant())
		{
			int val = ((AstExpInt)subscript).value;
			if (val < 0)
			{
				System.out.format(">> ERROR: array index must be >= 0\n");
				throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
			}
		}

		/************************************************************/
		/* [4] Return element type of array (explicit check)       */
		/************************************************************/
		TypeArray arrayType = (TypeArray) varType; 
		Type elementType = arrayType.type_of_array;

		if (elementType == null)
		{
			System.out.format(">> ERROR: internal error: array %s has no element type\n",varType.name);
			throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
		}

		return elementType;
	}
}
