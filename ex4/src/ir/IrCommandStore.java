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


public class IrCommandStore extends IrCommand
{
	String varName;
	Temp src;
	public int offset;
    public boolean isGlobal;

	public IrCommandStore(String varName, Temp src, int offset, boolean isGlobal)
	{
		this.src      = src;
		this.varName = varName;
		this.offset = offset;
		this.isGlobal = isGlobal;
	}

	@Override
	public String toString()
	{
		String scope = isGlobal ? "global" : "frame";
		String name = (varName == null) ? "<anon>" : varName;
		return String.format("store %s := %s(offset=%d, %s)", name, src, offset, scope);
	}

	public Temp getSrc()
	{
		return this.src;
	}

	public String getVarInfo() {
		return varName +"["+ offset +"]";
	}

	public String getVarName()
	{
		return this.varName;
	}
}
