package ast;

import semanticError.SemanticErrorException;
import symboltable.*;
import types.*;

/*
USAGE:
	| NEW type:t 														{: RESULT = new AstNewExp(t, null); 				:}
	| NEW type:t LBRACK exp:e RBRACK								{: RESULT = new AstNewExp(t, e); 					:}
*/

public class AstNewExp extends AstExp
{
    public AstVarType type;
    public AstExp exp;

    public AstNewExp(AstVarType type, AstExp exp, int lineNumber)
    {
        serialNumber = AstNodeSerialNumber.getFresh();
		this.lineNumber = lineNumber;
        this.type = type;
        this.exp = exp;
    }

	/****************************************************/
	/* The printing message for a new exp AST node */
	/****************************************************/
	public void printMe()
	{
		/*****************************/
		/* AST NODE TYPE = NEW EXP */
		/*****************************/
		System.out.format("AST NODE NEW EXP( %s )\n", type.type);

		/**************************************/
		/* RECURSIVELY PRINT exp ... */
		/**************************************/
		if (exp != null) exp.printMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AstGraphviz.getInstance().logNode(
                serialNumber,
			String.format("NEW(%s)", type.type));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (exp != null) AstGraphviz.getInstance().logEdge(serialNumber, exp.serialNumber);
	}

	public Type semantMe()
	{
		/****************************/
		/* [1] Check If Type exists */
		/****************************/
		Type t = SymbolTable.getInstance().find(type.type);
		if (t == null)
		{
			System.out.format(">> ERROR: non existing type %s\n",type.type);
			throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
		}

		if (t == TypeVoid.getInstance()){
			System.out.format(">> ERROR: cannot allocate void type %s\n",type.type);
			throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
		}

		if (t.isFunction()){
			System.out.format(">> ERROR: cannot allocate function type %s\n",type.type);
			throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
		}

		if (t.isNil()){
			System.out.format(">> ERROR: cannot allocate nil type %s\n",type.type);
			throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
		}

		// if no [exp] then must be class
		if (exp == null){
			// allocating class type
			if (!t.isClass()){
				System.out.format(">> ERROR: new without size expression must be of class type (%s is not class)\n",type.type);
				throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
			}
			return t;
		}
		// else must be array of the type
		else{
			return new TypeArray("new array", t);
		}
	}
}

