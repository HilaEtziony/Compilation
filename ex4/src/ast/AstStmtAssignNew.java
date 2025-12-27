package ast;

import ir.Ir;
import ir.IrCommandStore;
import semanticError.SemanticErrorException;
import symboltable.SymbolTableEntry;
import temp.Temp;
import types.*;

/*
USAGE:
	| var:v ASSIGN newExp:nExp SEMICOLON							{: RESULT = new AstStmtAssignNew(v,nExp); 			:}
*/

public class AstStmtAssignNew extends AstStmt
{
	/***************/
	/*  var := exp */
	/***************/
	public AstVar var;
	public AstNewExp exp;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AstStmtAssignNew(AstVar var, AstNewExp exp, int lineNumber)
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
		System.out.print("AST NODE ASSIGN STMT NEW\n");

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
		Type t_var = var.semantMe();
		Type t_new = exp.semantMe(); // Can be array or class type

		if(t_var != null && t_var instanceof TypeClassVarDec) t_var = ((TypeClassVarDec)t_var).t;

		/******************************/
		/* [1] Check assignment for new (means class or array) */
		/******************************/
		if (!t_var.isCompatible(t_new)) {
			System.out.format(">> ERROR: cannot assign %s to %s\n", t_new.name, t_var.name);
			throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
		}
		return null;
	}

	public Temp irMe() {
		Temp src = exp.irMe();

		if (var instanceof AstVarSimple) {
			AstVarSimple v = (AstVarSimple) var;
			symboltable.SymbolTableEntry entry = symboltable.SymbolTable.getInstance().findEntry(v.name);
			
			ir.Ir.getInstance().AddIrCommand(new ir.IrCommandStore(
				v.name, 
				src, 
				entry.offset, 
				entry.isGlobal
			));
		} 
		else if (var instanceof AstVarField) {
			AstVarField v = (AstVarField) var;
			Temp base = v.var.irMe(); 
			
			ir.Ir.getInstance().AddIrCommand(new ir.IrCommandFieldStore(base, v.fieldOffset, src));
		} 
		else if (var instanceof AstVarSubscript) {
			AstVarSubscript v = (AstVarSubscript) var;
			Temp base = v.var.irMe();      
			Temp index = v.subscript.irMe();
			
			ir.Ir.getInstance().AddIrCommand(new ir.IrCommandArrayStore(base, index, src));
		}

		return null;
	}
}
