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
}
