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

	private TypeClass cachedClassType; 
    private boolean isVirtualCall = false;
	private boolean isVoid = false;

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
		if (expList != null) 
		{
			AstGraphviz.getInstance().logEdge(serialNumber,expList.serialNumber);
		}
	}

	public Type semantMe()
	{
		// if in class, first check if method exists in class
		TypeClass currentClass = getSymbolTable().currentClass;
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

			// Class fields can be wrapped in TypeClassVarDec; unwrap to the actual
			// class type before resolving the method.
			// if varType is class variable, extract the type
			if(varType instanceof TypeClassVarDec) varType = ((TypeClassVarDec)varType).t;

			this.cachedClassType = (TypeClass) varType;
			this.isVirtualCall = true;

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
			funcType = getSymbolTable().find(id);
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
		Type returnType = funcType.getReturnType();
		
		if (returnType == null || returnType.isVoid()) 
		{
			this.isVoid = true;
		}
		
		return returnType;
	}

	public Temp irMe()
	{
		Temp varTemp = null;
        TempList tempArgsList = null;

		if (var != null) {
			varTemp = var.irMe();
		} 
		else {
			TypeClass currentClass = SymbolTable.getInstance().currentClass;
			if (currentClass != null && currentClass.getMethod(id) != null) {
				this.cachedClassType = currentClass;
				this.isVirtualCall = true;
				varTemp = TempFactory.getInstance().getFreshTemp();
				int thisOffset = SymbolTable.getInstance().getOffset("this");
				addIrCommand(new IrCommandLoad(varTemp, "this", thisOffset, false)); 
			}
		}

        /*******************************************************************/
        /* [1] Evaluate function arguments LEFT to RIGHT (per PDF 2.3)     */
        /*******************************************************************/
        if (expList != null) {
            int numArgs = expList.size();
            // Evaluate left-to-right
            Temp[] argTemps = new Temp[numArgs];
            for (int i = 0; i < numArgs; i++) {
                argTemps[i] = expList.get(i).irMe();
            }
            // Build TempList in correct order
            for (int i = numArgs - 1; i >= 0; i--) {
                tempArgsList = new TempList(argTemps[i], tempArgsList);
            }
        }

		if (varTemp != null) {
			tempArgsList = new TempList(varTemp, tempArgsList);
		}

		/********************************************************************************/
        /* [1.5] Special cases: Library functions (PrintInt, PrintString, Malloc, Exit) */
        /********************************************************************************/
        if (var == null) {
            if (id.equals("PrintInt")) {
                Temp arg = (tempArgsList != null) ? tempArgsList.head : null;
                addIrCommand(new IrCommandPrintInt(arg));
                return null; // PrintInt returns void
            }
            if (id.equals("PrintString")) {
                Temp arg = (tempArgsList != null) ? tempArgsList.head : null;
                addIrCommand(new IrCommandPrintString(arg));
                return null; // PrintString returns void
            }
            if (id.equals("Exit")) {
                addIrCommand(new IrCommandExit());
                return null; // Exit terminates program
            }
            if (id.equals("Malloc")) {
                Temp sizeArg = (tempArgsList != null) ? tempArgsList.head : null;
                Temp res = TempFactory.getInstance().getFreshTemp();
                addIrCommand(new IrCommandMalloc(res, sizeArg));
                return res;
            }
        }

        /*******************************************************************/
        /* [2] Allocate a result Temp                                      */
        /*******************************************************************/
        Temp resultTemp = null;
		if (!this.isVoid) {
			resultTemp = TempFactory.getInstance().getFreshTemp();
		}

		/*******************************************************************/
        /* [3] Create the IR Call command and add it to the IR list.       */
        /*******************************************************************/
		// VTable Call 
		if (varTemp != null) {
			Type varType = this.cachedClassType;
			
			TypeClass tc = (TypeClass) varType;
			int methodOffset = tc.getMethodOffset(id); 

			addIrCommand(new IrCommandVirtualCall(resultTemp, varTemp, methodOffset, tempArgsList));
		} 
		// Global Call 
		else {
			addIrCommand(new IrCommandCall(resultTemp, null, id, tempArgsList));
		}
		/******************************************************/
        /* [4] Return the Temp that will hold the return value */
        /******************************************************/
        return resultTemp;
	}
}

