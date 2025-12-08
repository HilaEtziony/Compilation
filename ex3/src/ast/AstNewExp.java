package ast;

import types.*;
import symboltable.*;
import semanticError.SemanticErrorException;

/*
USAGE:
	| NEW type:t 														{: RESULT = new AstNewExp(t, null); 				:}
	| NEW type:t LBRACK exp:e RBRACK								{: RESULT = new AstNewExp(t, e); 					:}
*/

public class AstNewExp extends AstExp
{
    public AstVarType type;
    public AstExp exp;

    public AstNewExp(AstVarType type, AstExp exp, int lineNumber)
    {
        serialNumber = AstNodeSerialNumber.getFresh();
		this.lineNumber = lineNumber;
        this.type = type;
        this.exp = exp;
    }

	/****************************************************/
	/* The printing message for a new exp AST node */
	/****************************************************/
	public void printMe()
	{
		/*****************************/
		/* AST NODE TYPE = NEW EXP */
		/*****************************/
		System.out.format("AST NODE NEW EXP( %s )\n", type.type);

		/**************************************/
		/* RECURSIVELY PRINT exp ... */
		/**************************************/
		if (exp != null) exp.printMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AstGraphviz.getInstance().logNode(
                serialNumber,
			String.format("NEW(%s)", type.type));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (exp != null) AstGraphviz.getInstance().logEdge(serialNumber, exp.serialNumber);
	}

	public Type semantMe()
	{
		/****************************/
		/* [1] Check If Type exists */
		/****************************/
		Type t = SymbolTable.getInstance().find(type.type);
		if (t == null)
		{
			System.out.format(">> ERROR: non existing type %s\n",type.type);
			throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
		}

		/********************************************/
		/* [2] Require array or class type  */
		/********************************************/
		if (!(t.isArray() || t.isClass()))
		{
			System.out.format(">> ERROR: new can only be used on array or class types (%s is not array or class)\n",type.type);
			throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");

		}

		if(t.isClass() && exp != null) {
			System.out.format(">> ERROR: new of class type cannot have size expression\n");
			throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
		}

		/******************************/
		/* [3] Check exp type is int  */
		/******************************/
		Type sizeType = exp.semantMe();
		if (t.isArray() && sizeType != TypeInt.getInstance())
		{
			System.out.format(">> ERROR: array size must be int\n");
			throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
		}

		/************************************************************/
		/* [4] If size is a constant int literal must be > 0      */
		/************************************************************/
		if (t.isArray() && exp.isConstant())
		{
			int val = ((AstExpInt)exp).value;
			if (val <= 0)
			{
				System.out.format(">> ERROR: array size must be > 0\n");
				throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
			}
		}

		/*********************************************/
		/* [5] Return type = array of that base type */
		/*********************************************/
		return t; // which is already an array type
	}
}

