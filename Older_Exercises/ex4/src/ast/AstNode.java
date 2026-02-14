package ast;

import types.*;
import symboltable.*;
import temp.*;
import ir.*;

public abstract class AstNode
{
	/*******************************************/
	/* The serial number is for debug purposes */
	/* In particular, it can help in creating  */
	/* a graphviz dot format of the AST ...    */
	/*******************************************/
	public int serialNumber;
	public int lineNumber;
	
	/***********************************************/
	/* The default message for an unknown AST node */
	/***********************************************/
	public void printMe()
	{
		System.out.print("AST NODE UNKNOWN\n");
	}

	/***********************************************/
	/* The default semantic action for an AST node */
	/***********************************************/
	public Type semantMe()
	{
		return null;
	}

	/*****************************************/
	/* The default IR action for an AST node */
	/*****************************************/
	public Temp irMe()
	{
		return null;
	}

	protected SymbolTable getSymbolTable()
	{
		return symboltable.SymbolTable.getInstance();
	}

	protected void addIrCommand(IrCommand cmd)
	{
		ir.Ir.getInstance().AddIrCommand(cmd);
	}
}
