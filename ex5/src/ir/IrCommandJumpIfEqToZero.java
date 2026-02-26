/***********/
/* PACKAGE */
/***********/
package ir;

import mips.MipsGenerator;

/*******************/
/* GENERAL IMPORTS */
/*******************/
import java.util.*;

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
	public Set<Temp> use() { return Collections.singleton(t); }

	@Override
	public String toString()
	{
		return String.format("if %s == 0 goto %s", t, labelName);
	}

	/***************/
	/* MIPS me !!! */
	/***************/
	public void mipsMe()
	{
		MipsGenerator.getInstance().beqz(t, labelName);
	}
}
