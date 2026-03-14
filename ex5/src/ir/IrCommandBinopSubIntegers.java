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

public class IrCommandBinopSubIntegers extends IrCommandBinop
{

	public IrCommandBinopSubIntegers(Temp dst, Temp t1, Temp t2)
	{
		super(dst, t1, t2);
	}

	@Override
	public String toString()
	{
		return String.format("%s := %s - %s", dst, t1, t2);
	}

	@Override
	public void mipsMe()
	{
		MipsGenerator.getInstance().sub(dst,t1,t2);
	}
}
