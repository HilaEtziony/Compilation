/***********/
/* PACKAGE */
/***********/
package ir;

/*******************/
/* GENERAL IMPORTS */
/*******************/
import java.util.*;

/*******************/
/* PROJECT IMPORTS */
/*******************/
import temp.*;

public abstract class IrCommand
{
	/*****************/
	/* Label Factory */
	/*****************/
	protected static int labelCounter = 0;
	public    static String getFreshLabel(String msg)
	{
		return String.format("Label_%d_%s", labelCounter++,msg);
	}

	/****************************/
	/* Liveness: def/use sets   */
	/****************************/
	public Set<Temp> def() { return Collections.emptySet(); }
	public Set<Temp> use() { return Collections.emptySet(); }

	/*******************************************************************/
	/* MIPS me - default empty body so subclasses compile before       */
	/* their mipsMe() implementations are added (done in MIPS branch) */
	/*******************************************************************/
	public void mipsMe() {}
}
