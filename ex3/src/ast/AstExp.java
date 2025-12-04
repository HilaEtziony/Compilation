package ast;

import types.*;
import symboltable.*;

public abstract class AstExp extends AstNode
{
    public boolean isConstant()
    {
        // Default implementation, TODO should be overridden in subclasses representing constant expressions
        return false;
    }

    public int getConstantValue()
    {
        // Default implementation, TODO should be overridden in subclasses representing constant expressions
        System.out.format(">> ERROR: getConstantValue() called on non-constant expression\n");
		System.exit(0);
        return 0;
    }

}