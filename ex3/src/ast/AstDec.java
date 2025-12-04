package ast;

import types.*;
import symboltable.*;

public abstract class AstDec extends AstStmt
{
    public AstDec()
    {
        // TODO get line num
        serialNumber = AstNodeSerialNumber.getFresh();
    }

	public Type semantMe()
	{
		// TODO
		return null;
	}
}

/*
This class is only being derived-from. Need to think about fields that should be shared among all declarations.
*/