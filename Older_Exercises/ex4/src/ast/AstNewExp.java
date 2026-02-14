package ast;

import ir.*;
import semanticError.SemanticErrorException;
import symboltable.*;
import temp.Temp;
import temp.TempFactory;
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
		Type t = getSymbolTable().find(type.type);
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
		// else must be array of the type, and exp of type int
		else{
			Type expType = exp.semantMe();
			if (expType != TypeInt.getInstance()){
				System.out.format(">> ERROR: new with size expression must be of type int (not %s)\n",expType.name);
				throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
			}
			if (exp.isConstant())
			{
				int expValue = exp.getConstantValue();
				if (expValue <= 0)
				{
					System.out.format(">> ERROR: Array size must be positive (not %d)\n", expValue);
					throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
				}
			}
			return new TypeArray("new", t);
		}
	}

	public Temp irMe() {
		Temp dst = TempFactory.getInstance().getFreshTemp();

		if (exp == null) { // like "new int"
			Type t = getSymbolTable().find(type.type);
			TypeClass tc = (TypeClass) t;
			int numberOfFields = tc.calcTotalFields();
			int sizeInBytes = (numberOfFields + 1) * 4;

			addIrCommand(new IrCommandNewClass(dst, sizeInBytes));
		}
		else { // like "new int[x]"
			Temp sizeTemp = exp.irMe();
			addIrCommand(new IrCommandNewArray(dst, sizeTemp));
			// TODO Hila why not handle like in "if" block, but just multiply by 4?
		}

		return dst;
	}

}

