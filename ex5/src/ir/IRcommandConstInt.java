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

public class IrCommandConstInt extends IrCommand
{
	Temp t;
	int value;
	
	public IrCommandConstInt(Temp t, int value)
	{
		this.t = t;
		this.value = value;
	}

	@Override
	public Set<Temp> def() { return t != null ? Collections.singleton(t) : Collections.emptySet(); }

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
