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

public class IrCommandAllocate extends IrCommand
{
	String varName;

	public IrCommandAllocate(String varName)
	{
		this.varName = varName;
	}

	@Override
	public String toString()
	{
		return String.format("alloc %s", varName);
	}

	/***************/
	/* MIPS me !!! */
	/***************/
	public void mipsMe()
	{
		MipsGenerator.getInstance().allocate(varName);
	}
}
