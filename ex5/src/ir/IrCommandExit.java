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

public class IrCommandExit extends IrCommand
{
	
	public IrCommandExit()
	{
	}

	@Override
	public Set<Temp> use() { return Collections.emptySet(); }

	@Override
	public String toString()
	{
		return String.format("Exit()");
	}

	/***************/
	/* MIPS me !!! */
	/***************/
	public void mipsMe()
	{
		MipsGenerator.getInstance().exit();
	}
}
