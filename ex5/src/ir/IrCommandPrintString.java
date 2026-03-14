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

public class IrCommandPrintString extends IrCommand
{
	Temp t;
	
	public IrCommandPrintString(Temp t)
	{
		this.t = t;
	}

	@Override
	public Set<Temp> use() { return Collections.singleton(t); }

	@Override
	public String toString()
	{
		return String.format("PrintString(%s)", t);
	}

	/***************/
	/* MIPS me !!! */
	/***************/
	public void mipsMe()
	{
		MipsGenerator.getInstance().printString(t);
	}
}
