package ast;

import ir.Ir;
import ir.IrCommandLoad;
import semanticError.SemanticErrorException;
import temp.Temp;
import temp.TempFactory;
import types.*;

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

		if(varType != null && varType instanceof TypeClassVarDec) varType = ((TypeClassVarDec)varType).t;

		if (varType == null || !varType.isArray())
		{
			System.out.format(">> ERROR: variable is not an array type\n");
			throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
		}

		/**********************************/
		/* [2] Evaluate index expression  */
		/**********************************/
		Type subType = subscript.semantMe();
		if(subType != null && subType instanceof TypeClassVarDec) subType = ((TypeClassVarDec)subType).t;

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

	public Temp irMe()
	{
		/*******************************************************************/
		/* [1] IMPORTANT: Trigger the IR generation for the index expression. */
		/* This adds the calculation commands to the IR list BEFORE the Load. */
		/*******************************************************************/
		this.subscript.irMe(); 

		/*******************************************************************/
		/* [2] Create a fresh Temp for the value we are about to load      */
		/*******************************************************************/
		Temp dst = TempFactory.getInstance().getFreshTemp();

		/*******************************************************************/
		/* [3] Build the path. Since this is a subscript, we start with [] */
		/*******************************************************************/
		String fullPath = "[]";
		AstVar tempVar = this.var;

		while (tempVar != null) 
		{
			if (tempVar instanceof AstVarSimple) {
				fullPath = ((AstVarSimple) tempVar).name + "." + fullPath;
				break;
			} 
			else if (tempVar instanceof AstVarField) {
				fullPath = ((AstVarField) tempVar).fieldName + "." + fullPath;
				tempVar = ((AstVarField) tempVar).var;
			} 
			else if (tempVar instanceof AstVarSubscript) {
				// Recursive index calculation for nested arrays like a[i][j]
				((AstVarSubscript) tempVar).subscript.irMe();
				fullPath = "[]" + "." + fullPath;
				tempVar = ((AstVarSubscript) tempVar).var;
			} 
			else { break; }
		}

		/*******************************************************************/
		/* [4] Generate the Load command using the symbolic path           */
		/*******************************************************************/
		Ir.getInstance().AddIrCommand(new IrCommandLoad(dst, fullPath));

		return dst;
	}

	public String getPath() {
		this.subscript.irMe(); 
		return var.getPath() + ".[]";
	}
}
