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

public class IrCommandJumpLabel extends IrCommand
{
	String labelName;

	public IrCommandJumpLabel(String labelName)
	{
		this.labelName = labelName;
	}

	@Override
	public String toString()
	{
		return String.format("goto %s", labelName);
	}

	/***************/
	/* MIPS me !!! */
	/***************/
	public void mipsMe()
	{
		MipsGenerator.getInstance().jump(labelName);
	}
}
