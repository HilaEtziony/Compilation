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
}
