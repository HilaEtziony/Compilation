package ast;

import types.*;
import symboltable.*;
import temp.Temp;
import temp.TempFactory;
import ir.*;
import semanticError.SemanticErrorException;

public class AstExpNIL extends AstExp
{
    public AstExpNIL()
    {
        serialNumber = AstNodeSerialNumber.getFresh();
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

    @Override
    public Temp irMe()
    {
        /****************************************************************/
        /* [1] Generate a fresh Temp to hold the NIL value              */
        /****************************************************************/
        Temp dst = TempFactory.getInstance().getFreshTemp();

        /****************************************************************/
        /* [2] NIL is represented as the constant integer 0              */
        /* We use IrCommandConstInt to load 0 into our destination Temp */
        /****************************************************************/
        Ir.getInstance().AddIrCommand(new IrCommandConstInt(dst, 0));

        /****************************************************************/
        /* [3] Return the Temp containing the value 0                   */
        /****************************************************************/
        return dst;
    }
}
