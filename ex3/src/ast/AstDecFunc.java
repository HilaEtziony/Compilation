package ast;

import types.*;
import symboltable.*;

public class AstDecFunc extends AstDec
{
    public AstVarType return_type;
    public String identifier;
    public AstTypeIdList func_input; // might be null - don't forget to check whenever using
    public AstStmtList stmnts_of_funs;

    public AstDecFunc(AstVarType return_type, String identifier, AstTypeIdList func_input, AstStmtList stmnts_of_funs){
        // TODO get line num
        serialNumber = AstNodeSerialNumber.getFresh();

        this.return_type = return_type;
        this.identifier = identifier;
        this.func_input = func_input;
        this.stmnts_of_funs = stmnts_of_funs;
    }

    /************************************************************/
	/* The printing message for a function declaration AST node */
	/************************************************************/
	public void printMe()
	{
		/*************************************************/
		/* AST NODE TYPE = AST NODE FUNCTION DECLARATION */
		/*************************************************/
		System.out.format("FUNC(%s):%s\n",identifier,return_type);

		/***************************************/
		/* RECURSIVELY PRINT func_input + stmnts_of_funs ... */
		/***************************************/
		if (func_input != null) func_input.printMe();
		if (stmnts_of_funs   != null) stmnts_of_funs.printMe();
		
		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AstGraphviz.getInstance().logNode(
                serialNumber,
			String.format("FUNC(%s)\n:%s\n",identifier,return_type));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (func_input != null) AstGraphviz.getInstance().logEdge(serialNumber,func_input.serialNumber);
		if (stmnts_of_funs   != null) AstGraphviz.getInstance().logEdge(serialNumber,stmnts_of_funs.serialNumber);
	}

	public Type semantMe()
	{
		Type t;
		Type returnType = null;
		TypeList type_list = null;

		/*******************/
		/* [0] return type */
		/*******************/
		returnType = SymbolTable.getInstance().find(return_type.type);
		// returnType = SymbolTable.getInstance().find(return_type); // TODO change AstVarType
		if (returnType == null)
		{
			System.out.format(">> ERROR [%d:%d] non existing return type %s\n",6,6,returnType);				
		}
	
		/****************************/
		/* [1] Begin Function Scope */
		/****************************/
		SymbolTable.getInstance().beginScope();

		/***************************/
		/* [2] Semant Input func_input */
		/***************************/
		for (AstTypeIdList it = func_input; it  != null; it = it.tail)
		{
			t = SymbolTable.getInstance().find(it.head.type);
			if (t == null)
			{
				System.out.format(">> ERROR [%d:%d] non existing type %s\n",2,2,it.head.type);				
			}
			else
			{
				type_list = new TypeList(t,type_list);
				SymbolTable.getInstance().enter(it.identifier,t);
			}
		}

		/*******************/
		/* [3] Semant Body */
		/*******************/
		stmnts_of_funs.semantMe();

		/*****************/
		/* [4] End Scope */
		/*****************/
		SymbolTable.getInstance().endScope();

		/***************************************************/
		/* [5] Enter the Function Type to the Symbol Table */
		/***************************************************/
		SymbolTable.getInstance().enter(identifier,new TypeFunction(returnType,identifier,type_list));

		/************************************************************/
		/* [6] Return value is irrelevant for function declarations */
		/************************************************************/
		return null;		
	}
}

/*
accepts:

RESULT = new AstDecFunc(t,i,l,s);
RESULT = new AstDecFunc(t,i,null,s);
*/