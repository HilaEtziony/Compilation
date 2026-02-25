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

public class IrCommandConstInt extends IrCommand
{
	public Temp t;
	int value;
	
	public IrCommandConstInt(Temp t, int value)
	{
		this.t = t;
		this.value = value;
	}

	@Override
	public String toString()
	{
		if (t == null)
		{
			return String.format("const %d", value);
		}
		return String.format("%s := %d", t, value);
	}

	public Temp getTemp()
	{
		return t;
	}

	/***************/
	/* MIPS me !!! */
	/***************/
	public void mipsMe()
	{
		MipsGenerator.getInstance().li(t,value);
	}
}
