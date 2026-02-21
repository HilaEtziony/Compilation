package ast;

import semanticError.SemanticErrorException;
import types.*;
import temp.*;
import ir.*;

/*
USAGE:
	| var:v ASSIGN exp:e SEMICOLON									{: RESULT = new AstStmtAssign(v,e); 				:}
*/

public class AstStmtAssign extends AstStmt
{
	/***************/
	/*  var := exp */
	/***************/
	public AstVar var;
	public AstExp exp;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AstStmtAssign(AstVar var, AstExp exp, int lineNumber)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		serialNumber = AstNodeSerialNumber.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.print("====================== stmt -> var ASSIGN exp SEMICOLON\n");

		/*******************************/
		/* COPY INPUT DATA MEMBERS ... */
		/*******************************/
		this.lineNumber = lineNumber;
		this.var = var;
		this.exp = exp;
	}

	/*********************************************************/
	/* The printing message for an assign statement AST node */
	/*********************************************************/
	public void printMe()
	{
		/********************************************/
		/* AST NODE TYPE = AST ASSIGNMENT STATEMENT */
		/********************************************/
		System.out.print("AST NODE ASSIGN STMT\n");

		/***********************************/
		/* RECURSIVELY PRINT VAR + EXP ... */
		/***********************************/
		if (var != null) var.printMe();
		if (exp != null) exp.printMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AstGraphviz.getInstance().logNode(
                serialNumber,
			"ASSIGN\nleft := right\n");

		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		AstGraphviz.getInstance().logEdge(serialNumber,var.serialNumber);
		AstGraphviz.getInstance().logEdge(serialNumber,exp.serialNumber);
	}

	public Type semantMe()
	{
		Type t_exp = exp.semantMe();
		Type t_var = var.semantMe();

		if(t_var != null && t_var instanceof TypeClassVarDec) t_var = ((TypeClassVarDec)t_var).t;
		if(t_exp != null && t_exp instanceof TypeClassVarDec) t_exp = ((TypeClassVarDec)t_exp).t;

		/**************************************/
		/* [1] Check assignment of nil        */
		/**************************************/
		if (t_exp.isNil() && !(t_var.isClass() || t_var.isArray())) {
			System.out.format(">> ERROR: cannot assign nil to %s\n", t_var.name);
			throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
		}

		/**************************************/
		/* [2] Check assignment compatibility */
		/**************************************/
		if (!t_var.isCompatible(t_exp)) {
			System.out.format(">> ERROR: cannot assign %s to %s\n", t_exp.name, t_var.name);
			throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
		}

		/************************************************************/
		/* [3] Return value is irrelevant for statements          */
		/************************************************************/
		return null;
	}

	public Temp irMe() {
	//L-Value
    Temp baseTemp = null;
    Temp indexTemp = null;
    int fieldOffset = -1;

    if (var instanceof AstVarField) {
        baseTemp = ((AstVarField) var).var.irMe();
        fieldOffset = ((AstVarField) var).fieldOffset;
    } 
    else if (var instanceof AstVarSubscript) {
        baseTemp = ((AstVarSubscript) var).var.irMe();
        indexTemp = ((AstVarSubscript) var).subscript.irMe();
    }

	// R-Value
    Temp srcTemp = exp.irMe(); 

    // Store
    if (var instanceof AstVarSimple) {
			AstVarSimple v = (AstVarSimple) var;
            if (v.isClassField()) { 
                Temp tThis = TempFactory.getInstance().getFreshTemp();
                addIrCommand(new IrCommandLoad(tThis, "this", 8, false));
                addIrCommand(new IrCommandFieldStore(tThis, v.getCachedOffset(), srcTemp));
            } else {
                addIrCommand(new IrCommandStore(v.name, srcTemp, v.getCachedOffset(), v.isGlobalVariable()));
            }
    }
    else if (var instanceof AstVarField) {
        addIrCommand(new IrCommandFieldStore(baseTemp, fieldOffset, srcTemp));
    }
    else if (var instanceof AstVarSubscript) {
        addIrCommand(new IrCommandArrayStore(baseTemp, indexTemp, srcTemp));
    }
    
    return null;
	}
}
