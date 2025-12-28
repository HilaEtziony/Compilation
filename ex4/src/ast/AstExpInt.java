package ast;

import types.*;
import symboltable.*;
import semanticError.SemanticErrorException;
import temp.*;
import ir.*;

/*
USAGE:
	| MINUS INT:i													{: RESULT = new AstExpInt(i, true); 				:}
	| INT:i 														{: RESULT = new AstExpInt(i, false); 				:}
*/

public class AstExpInt extends AstExp
{
	public int value;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AstExpInt(int value, boolean isNegative, int lineNumber)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		serialNumber = AstNodeSerialNumber.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.format("====================== exp -> INT( %d )\n", value);

		/*******************************/
		/* COPY INPUT DATA MEMBERS ... */
		/*******************************/
		this.lineNumber = lineNumber;
		if (isNegative) 
		{
			this.value = -1 * value;
		} 
		else 
		{
			this.value = value;
		}
	}


	/************************************************/
	/* The printing message for an INT EXP AST node */
	/************************************************/
	public void printMe()
	{
		/*******************************/
		/* AST NODE TYPE = AST INT EXP */
		/*******************************/
		System.out.format("AST NODE INT( %d )\n",value);

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AstGraphviz.getInstance().logNode(
                serialNumber,
			String.format("INT(%d)",value));
	}

	public Type semantMe()
	{
		return TypeInt.getInstance();
	}

	public boolean isConstant()
	{
		return true;
	}

	public int getConstantValue()
	{
		return this.value;
	}

	public Temp irMe()
	{
		Temp t = TempFactory.getInstance().getFreshTemp();
		addIrCommand(new IrCommandConstInt(t,value));
		return t;
	}

}
