/***********/
/* PACKAGE */
/***********/
package ir;

/*******************/
/* GENERAL IMPORTS */
/*******************/

/*******************/
/* PROJECT IMPORTS */
/*******************/
import temp.*;

/*
USAGE:
	| WHILE LPAREN exp:cond RPAREN LBRACE stmtList:body RBRACE 		{: RESULT = new AstStmtWhile(cond,body); 			:}
*/

public class IrCommandJumpIfEqToZero extends IrCommand
{
	Temp t;
	String labelName;
	
	public IrCommandJumpIfEqToZero(Temp t, String labelName)
	{
		this.t          = t;
		this.labelName = labelName;
	}

	@Override
	public String toString()
	{
		return String.format("if %s == 0 goto %s", t, labelName);
	}
}
