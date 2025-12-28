package ast;

import semanticError.SemanticErrorException;
import symboltable.*;
import types.*;
import ir.*;
import temp.*;

/*
USAGE:
	| ID:name															{: RESULT = new AstVarSimple(name);       			:}
*/

public class AstVarSimple extends AstVar
{
	/************************/
	/* simple variable name */
	/************************/
	public String name;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AstVarSimple(String name, int lineNumber)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		serialNumber = AstNodeSerialNumber.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.format("====================== var -> ID( %s )\n",name);

		/*******************************/
		/* COPY INPUT DATA MEMBERS ... */
		/*******************************/
		this.lineNumber = lineNumber;
		this.name = name;
	}

	/**************************************************/
	/* The printing message for a simple var AST node */
	/**************************************************/
	public void printMe()
	{
		/**********************************/
		/* AST NODE TYPE = AST SIMPLE VAR */
		/**********************************/
		System.out.format("AST NODE SIMPLE VAR( %s )\n",name);

		/*********************************/
		/* Print to AST GRAPHVIZ DOT file */
		/*********************************/
		AstGraphviz.getInstance().logNode(
				serialNumber,
			String.format("SIMPLE\nVAR\n(%s)",name));
	}

	public Type semantMe()
	{
		/******************************/
		/* [1] Try finding var in ST */
		/******************************/
		Type t = getSymbolTable().find(name);

		if (t == null)
		{
			System.out.format(">> ERROR: variable %s does not exist\n",name);
			throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
		}

		/**********************************************************/
		/* [2] return type of variable, since simple var has type */
		/**********************************************************/
		return t;
	}

	public Temp irMe()
	{
		SymbolTableEntry entry = getSymbolTable().findEntry(name);
		Temp t = TempFactory.getInstance().getFreshTemp();
		addIrCommand(new IrCommandLoad(t,name, entry.offset, entry.isGlobal));
		return t;
	}

	public String getPath() {
		return this.name;
	}
}
