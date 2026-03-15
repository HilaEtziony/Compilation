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

public class IrCommandBinopGtIntegers extends IrCommandBinop
{
	public IrCommandBinopGtIntegers(Temp dst, Temp t1, Temp t2)
	{
		super(dst, t1, t2);
	}

	@Override
	public String toString()
	{
		return String.format("%s := (%s > %s)", dst, t1, t2);
	}

	@Override
    public void mipsMe()
    {
        /*******************************/
        /* [1] Allocate 2 fresh labels */
        /*******************************/
        String labelEnd        = getFreshLabel("end");
        String labelAssignOne  = getFreshLabel("AssignOne");
        String labelAssignZero = getFreshLabel("AssignZero");
        
        /***************************************************/
        /* [2] if (t1 >  t2) goto labelAssignOne;          */
        /* if (t1 <= t2) goto labelAssignZero;         	   */
        /***************************************************/
        
		// Using 'blt' with swapped args (if t2 < t1 then t1 > t2)
        mips.MipsGenerator.getInstance().blt(t2, t1, labelAssignOne);
        mips.MipsGenerator.getInstance().label(labelAssignZero);
        mips.MipsGenerator.getInstance().li(dst, 0);
        mips.MipsGenerator.getInstance().jump(labelEnd);

        /************************/
        /* [3] labelAssignOne:  */
        /************************/
        mips.MipsGenerator.getInstance().label(labelAssignOne);
        mips.MipsGenerator.getInstance().li(dst, 1);

        /******************/
        /* [4] labelEnd:  */
        /******************/
        mips.MipsGenerator.getInstance().label(labelEnd);
    }
}
