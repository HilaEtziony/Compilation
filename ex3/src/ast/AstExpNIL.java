package ast;

import types.*;
import symboltable.*;

public class AstExpNIL extends AstExp
{
    public AstExpNIL()
    {
        // TODO get line num
        serialNumber = AstNodeSerialNumber.getFresh();
    }
}
