package ast;

import types.*;
import symboltable.*;
import semanticError.SemanticErrorException;

/*
USAGE:
	| TYPE_INT          												{: RESULT = new AstVarType("int"); 					:}
	| TYPE_STRING     												{: RESULT = new AstVarType("string"); 				:}
	| TYPE_VOID       												{: RESULT = new AstVarType("void"); 				:}
	| ID:name         												{: RESULT = new AstVarType(name); 					:}
*/

public class AstVarType extends AstDec
{
    public String type;

    public AstVarType(String type, int lineNumber)
    {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.lineNumber = lineNumber;
        this.type = type;
    }

    /****************************************************/
	/* The printing message for a var type AST node */
	/****************************************************/
	public void printMe()
	{
		/*****************************/
		/* AST NODE TYPE = VAR TYPE */
		/*****************************/
		System.out.format("AST NODE VAR TYPE( %s )\n", type);

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AstGraphviz.getInstance().logNode(
                serialNumber,
			String.format("VAR\nTYPE(%s)", type));
	}

	public Type semantMe()
	{
		// TODO
		return null;
	}
}