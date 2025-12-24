package ast;

import semanticError.SemanticErrorException;
import symboltable.*;
import types.*;
import temp.*;
import ir.*;

/*
USAGE:
	| var:v DOT ID:i LPAREN expList:l RPAREN 							{: RESULT = new AstExpCall(v,i,l);    				:}
	| var:v DOT ID:i LPAREN RPAREN									{: RESULT = new AstExpCall(v,i,null);    			:}
	| ID:i LPAREN expList:l RPAREN 									{: RESULT = new AstExpCall(null,i,l);    			:}
	| ID:i LPAREN RPAREN											{: RESULT = new AstExpCall(null,i,null);    		:}
*/

public class AstExpCall extends AstExp
{
    public AstVar var;
    public String id;
    public AstExpList expList;

    public AstExpCall(AstVar var, String id, AstExpList expList, int lineNumber)
    {
        serialNumber = AstNodeSerialNumber.getFresh();
		this.lineNumber = lineNumber;
        this.var = var;
        this.id = id;
        this.expList = expList;
    }

    /************************************************/
	/* The printing message for a call exp AST node */
	/************************************************/
	public void printMe()
	{
		/********************************/
		/* AST NODE TYPE = AST CALL EXP */
		/********************************/
		System.out.format("CALL(%s)\nWITH:\n",id);

		/***************************************/
		/* RECURSIVELY PRINT expList + body ... */
		/***************************************/
		if (expList != null) expList.printMe();
		
		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AstGraphviz.getInstance().logNode(
                serialNumber,
			String.format("CALL(%s)\nWITH",id));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		AstGraphviz.getInstance().logEdge(serialNumber,expList.serialNumber);
	}

	public Type semantMe()
	{
		// if in class, first check if method exists in class
		TypeClass currentClass = SymbolTable.getInstance().currentClass;
		if (currentClass != null && var == null) {
			Type funcType = currentClass.getMethod(id);
			if (funcType != null) {
				return funcType.getReturnType();
			}
		}		
		/**************************************/
		/* [1] Find function in symbol table  */
		/**************************************/
		Type funcType;
		if (var != null) {
			// Method call on object
			Type varType = var.semantMe();
			if (!varType.isClass()) {
				System.out.format(">> ERROR: variable is not a class for method call %s\n", id);
				throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
			}
			funcType = ((TypeClass)varType).getMethod(id);
			if (funcType == null) {
				System.out.format(">> ERROR: method %s does not exist in class %s\n", id, varType.name);
				throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
			}
		} else {
			// Global function call
			funcType = SymbolTable.getInstance().find(id);
			if (funcType == null || !funcType.isFunction()) {
				System.out.format(">> ERROR: function %s does not exist\n", id);
				throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
			}
		}

		/***********************************************/
		/* [2] Check number of arguments if expList    */
		/***********************************************/
		TypeList paramTypes = funcType.getParamTypes(); 
		int nParams = (paramTypes == null) ? 0 : paramTypes.length();
		int nArgs = (expList != null) ? expList.size() : 0;

		if (nArgs != nParams) {
			System.out.format(">> ERROR: function %s expects %d arguments, got %d\n", id, nParams, nArgs);
			throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
		}

		/***********************************************/
		/* [3] Check each argument type               */
		/***********************************************/
		for (int i = 0; i < nParams; i++) {
			Type tArg = expList.get(i).semantMe();
			Type tParam = paramTypes.get(i);

			// nil can be assigned only to class or array
			if (tArg.isNil() && !(tParam.isClass() || tParam.isArray())) {
				System.out.format(">> ERROR: cannot assign nil to parameter %d of type %s in function %s\n", i+1, tParam.name, id);
				throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
			}

			// check compatibility
			if (!tParam.isCompatible(tArg)) {
				System.out.format(">> ERROR: argument %d type %s does not match parameter type %s in function %s\n", i+1, tArg.name, tParam.name, id);
				throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
			}
		}

		/***********************************************/
		/* [4] Return function return type            */
		/***********************************************/
		return funcType.getReturnType(); 
	}

	public Temp irMe()
	{
		Temp varTemp = null;
        TempList tempArgsList = null;

        if (var != null) {
            varTemp = var.irMe();
        }

        /*******************************************************************/
        /* [1] Evaluate function arguments from the expList.               */
        /*******************************************************************/
        if (expList != null) {
            int numArgs = expList.size();
            for (int i = numArgs - 1; i >= 0; i--) {
                Temp t = expList.get(i).irMe();
                tempArgsList = new TempList(t, tempArgsList);
            }
        }

        /*******************************************************************/
        /* [2] Allocate a result Temp and add the Call command to the IR.  */
        /*******************************************************************/
        Temp resultTemp = TempFactory.getInstance().getFreshTemp();
        
		/*******************************************************************/
        /* [3] Create the IR Call command and add it to the IR list.       */
        /*******************************************************************/
        Ir.getInstance().AddIrCommand(new IrCommandCall(resultTemp, varTemp, id, tempArgsList));

		/******************************************************/
        /* [4] Return the Temp that will hold the return value */
        /******************************************************/
        return resultTemp;
	}
}

