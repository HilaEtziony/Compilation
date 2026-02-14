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

public class IrCommandLoad extends IrCommand
{
	Temp dst;
	String varName;
	public int offset;
    public boolean isGlobal;

	public IrCommandLoad(Temp dst, String varName, int offset, boolean isGlobal)
	{
		this.dst      = dst;
		this.varName = varName;
		this.offset   = offset;
		this.isGlobal = isGlobal;
	}

	@Override
	public String toString()
	{
		String scope = isGlobal ? "global" : "frame";
		String name = (varName == null) ? "<anon>" : varName;
		return String.format("%s := load %s(offset=%d, %s)", dst, name, offset, scope);
	}

	public Temp getDst() {
		return dst;
	}

	public String getVarInfo() {
		return varName +"["+ offset +"]";
	}

	public String getVarName() {
		return varName;
	}

	/***************/
	/* MIPS me !!! */
	/***************/
	public void mipsMe()
	{
		MipsGenerator.getInstance().load(dst, varName);
	}
}
