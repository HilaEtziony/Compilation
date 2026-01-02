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

public class IrCommandBinopDivIntegers extends IrCommand
{
	public Temp t1;
	public Temp t2;
	public Temp dst;

	public IrCommandBinopDivIntegers(Temp dst, Temp t1, Temp t2)
	{
		this.dst = dst;
		this.t1 = t1;
		this.t2 = t2;
	}

	@Override
	public String toString()
	{
		return String.format("%s := %s / %s", dst, t1, t2);
	}
}
