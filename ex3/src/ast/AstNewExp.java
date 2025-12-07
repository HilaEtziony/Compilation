package ast;

import types.*;
import symboltable.*;

/*
USAGE:
	| NEW type:t 														{: RESULT = new AstNewExp(t, null); 				:}
	| NEW type:t LBRACK exp:e RBRACK								{: RESULT = new AstNewExp(t, e); 					:}
*/

public class AstNewExp extends AstExp
{
    public AstVarType type;
    public AstExp exp;

    public AstNewExp(AstVarType type, AstExp exp)
    {
        // TODO get line num
        serialNumber = AstNodeSerialNumber.getFresh();

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
			System.out.format(">> ERROR [%d:%d] non existing type %s\n",2,2,type.type);
			System.exit(0);
		}

		/********************************************/
		/* [2] Require array type for new T[e]      */
		/********************************************/
		if (!t.isArray())
		{
			System.out.format(">> ERROR [%d:%d] new can only be used on array types (%s is not array)\n",2,2,type.type);
			System.exit(0);
		}

		/******************************/
		/* [3] Check exp type is int  */
		/******************************/
		Type sizeType = exp.semantMe();
		if (sizeType != TypeInt.getInstance())
		{
			System.out.format(">> ERROR [%d:%d] array size must be int\n",2,2);
			System.exit(0);
		}

		/************************************************************/
		/* [4] If size is a constant int literal → must be > 0      */
		/************************************************************/
		if (exp instanceof AstExpInt && ((AstExpInt)exp).isConstant())
		{
			int val = ((AstExpInt)exp).value;
			if (val <= 0)
			{
				System.out.format(">> ERROR [%d:%d] array size must be > 0\n",2,2);
				System.exit(0);
			}
		}

		/*********************************************/
		/* [5] Return type = array of that base type */
		/*********************************************/
		return t; // which is already an array type
	}
}

