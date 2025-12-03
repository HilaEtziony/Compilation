package ast;

import types.*;
import symboltable.*;

public class AstExpNIL extends AstExp
{
    public AstExpNIL()
    {
        // TODO get line num
        serialNumber = AstNodeSerialNumber.getFresh();
    }

	/****************************************************/
	/* The printing message for a NIL exp AST node */
	/****************************************************/
	public void printMe()
	{
		/*****************************/
		/* AST NODE TYPE = NIL EXP */
		/*****************************/
		System.out.print("AST NODE NIL\n");

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AstGraphviz.getInstance().logNode(
                serialNumber,
			"NIL");
	}
}
