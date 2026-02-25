/***********/
/* PACKAGE */
/***********/
package ir;

import mips.MipsGenerator;

/*******************/
/* GENERAL IMPORTS */
/*******************/

/*******************/
/* PROJECT IMPORTS */
/*******************/
import temp.*;

public class IrCommandPrintInt extends IrCommand
{
	public Temp t;
	
	public IrCommandPrintInt(Temp t)
	{
		this.t = t;
	}

	@Override
	public String toString()
	{
		return String.format("PrintInt(%s)", t);
	}

	/***************/
	/* MIPS me !!! */
	/***************/
	public void mipsMe()
	{
		MipsGenerator.getInstance().printInt(t);
	}
}
