package ir;

/*******************/
/* GENERAL IMPORTS */
/*******************/
import java.util.*;

/*******************/
/* PROJECT IMPORTS */
/*******************/
import temp.*;

public class IrCommandBinop extends IrCommand
{
	public Temp t1;
	public Temp t2;
	public Temp dst;

	public IrCommandBinop(Temp dst, Temp t1, Temp t2)
	{
		this.dst = dst;
		this.t1 = t1;
		this.t2 = t2;
	}

	@Override
	public Set<Temp> def() { return Collections.singleton(dst); }

	@Override
	public Set<Temp> use()
	{
		Set<Temp> s = new HashSet<>();
		if (t1 != null) s.add(t1);
		if (t2 != null) s.add(t2);
		return s;
	}

	@Override
	public String toString()
	{
		return String.format("%s := %s # %s", dst, t1, t2);
	}

}
