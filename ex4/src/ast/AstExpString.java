package ast;

import types.*;
import symboltable.*;
import temp.Temp;
import temp.TempFactory;
import ir.Ir;
import ir.IrCommandConstString;
import semanticError.SemanticErrorException;

/*
USAGE:
	| STRING:s														{: RESULT = new AstExpString(s); 					:}
*/

public class AstExpString extends AstExp
{
    public String string;

    public AstExpString(String string, int lineNumber)
    {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.lineNumber = lineNumber;
        this.string = string;
    }

    /******************************************************/
	/* The printing message for a STRING EXP AST node */
	/******************************************************/
	public void printMe()
	{
		/*******************************/
		/* AST NODE TYPE = AST STRING EXP */
		/*******************************/
		System.out.format("AST NODE STRING( %s )\n",string);

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AstGraphviz.getInstance().logNode(
                serialNumber,
			String.format("STRING\n%s",string.replace('"','\'')));
	}

	public Type semantMe()
	{
		return TypeString.getInstance();
	}

	public Temp irMe()
	{
		Temp dst = TempFactory.getInstance().getFreshTemp();
		addIrCommand(new IrCommandConstString(dst, this.string));
		return dst;
	}
}