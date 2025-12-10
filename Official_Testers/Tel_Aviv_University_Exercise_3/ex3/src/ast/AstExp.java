package ast;

import types.*;
import symboltable.*;

public abstract class AstExp extends AstNode
{
    public boolean isConstant()
    {
        // Default implementation. Id overridden in subclasses representing constant expressions (AstExpInt)
        // Perhaps will need to override in AstExpBinop, AstExpString in the future
        return false;
    }

    public int getConstantValue()
    {
        // Default implementation. Is overridden in subclasses representing constant expressions (AstExpInt)
        // Perhaps will need to override in AstExpBinop, AstExpString in the future
        return 0;
    }

}