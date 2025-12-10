package ast;

import types.*;
import symboltable.*;
import semanticError.SemanticErrorException;

public class AstExpNIL extends AstExp
{
    public AstExpNIL(int lineNumber)
    {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.lineNumber = lineNumber;
    }

    public void printMe()
    {
        System.out.print("AST NODE NIL\n");
        AstGraphviz.getInstance().logNode(serialNumber, "NIL");
    }

    public Type semantMe() 
    {
        return TypeNil.getInstance();
    }
}
