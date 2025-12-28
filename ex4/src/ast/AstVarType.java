package ast;

import semanticError.SemanticErrorException;
import symboltable.*;
import temp.Temp;
import types.*;

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
		// check that type exists in symbol table
		Type t = getSymbolTable().find(type);
		if (t == null) {
			System.out.format("ERROR: type %s not found in symbol table\n",type);
			throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
		}

		return t;
	}

	public Temp irMe()
	{
		return null;
	}
}