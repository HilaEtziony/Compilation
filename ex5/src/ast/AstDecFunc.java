package ast;

import semanticError.SemanticErrorException;
import symboltable.*;
import types.*;
import temp.*;
import ir.*;

/*
USAGE:
	| type:t ID:i LPAREN typeIdList:l RPAREN LBRACE stmtList:s RBRACE {: RESULT = new AstDecFunc(t,i,l,s);    			:}
	| type:t ID:i LPAREN RPAREN LBRACE stmtList:s RBRACE			{: RESULT = new AstDecFunc(t,i,null,s);    			:}
*/

public class AstDecFunc extends AstDec
{
    public AstVarType return_type;
    public String identifier;
    public AstTypeIdList func_input; // might be null - don't forget to check whenever using
    public AstStmtList stmnts_of_funs;
	public int numLocals = 0;

    public AstDecFunc(AstVarType return_type, String identifier, AstTypeIdList func_input, AstStmtList stmnts_of_funs, int lineNumber){
        serialNumber = AstNodeSerialNumber.getFresh();
		this.lineNumber = lineNumber;
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
		Type returnType = this.return_type.semantMe();
		TypeList type_list =null;

		/*******************/
		/* [0] return type */
		/*******************/
		// returnType = getSymbolTable().find(return_type.type);
		// returnType = getSymbolTable().find(return_type); // TODO change AstVarType
		if (returnType == null)
		{
			System.out.format(">> ERROR: non existing return type %s\n",returnType);
			throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");				
		}

		// check if function name already exists in current scope
		if (getSymbolTable().findInCurrentScope(identifier) != null)
		{
			System.out.format(">> ERROR: function name %s already exists in current scope\n",identifier);
			throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
		}

		/****************************/
		/* [1] Begin Function Scope */
		/****************************/
		getSymbolTable().beginScope();
		getSymbolTable().enter(identifier, new TypeFunction(returnType, identifier, null)); // sentinal for return type


		int paramOffset = 8;
		/***************************/
		/* [2] Semant Input func_input */
		/***************************/
		for (AstTypeIdList it = func_input; it  != null; it = it.tail)
		{
			t = getSymbolTable().find(it.head.type);
			if (t == null || t == TypeVoid.getInstance())
			{
				System.out.format(">> ERROR: non existing type %s\n",it.head.type);	
				throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");			
			}
			else if (getSymbolTable().findInCurrentScope(it.identifier) != null)
			{
				System.out.format(">> ERROR: duplicate variable name %s\n",it.identifier);	
				throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");			
			}
			else
			{
				type_list = new TypeList(t,type_list);
				getSymbolTable().enter(it.identifier, t, paramOffset, false);
        
        		paramOffset += 4; // assuming each parameter takes 4 bytes
			}
		}
		// reverse type_list to maintain order
		TypeList reversed = null;
		while (type_list != null) {
			reversed = new TypeList(type_list.head, reversed);
			type_list = type_list.tail;
		}
		type_list = reversed;
		//moved sentinal enter after processing input params
		getSymbolTable().enter(identifier, new TypeFunction(returnType, identifier, type_list));
		// getSymbolTable().enter(identifier, t);

		/*******************/
		/* [3] Semant Body */
		/*******************/
		getSymbolTable().resetLocalOffset();
		stmnts_of_funs.semantMe();
		this.numLocals = getSymbolTable().getLocalCount();

		/*****************/
		/* [4] End Scope */
		/*****************/
		getSymbolTable().endScope();

		/***************************************************/
		/* [5] Enter the Function Type to the Symbol Table */
		/***************************************************/
		TypeFunction funcType = new TypeFunction(returnType,identifier,type_list);
		getSymbolTable().enter(identifier, funcType);

		/************************************************************/
		/* [6] Return value is irrelevant for function declarations */
		/************************************************************/
		return null;		
	}

	public int semantMe(TypeClass theirClassType, int offset){
		// similar to above, but enter to theirClassType's data members instead of symbol table
		Type t;
		Type returnType = this.return_type.semantMe();
		TypeList type_list =null;

		System.out.format(">> Semanting method %s of class %s\n", identifier, theirClassType.name);

		/*******************/
		/* [0] return type */
		/*******************/
		if (returnType == null)
		{
			System.out.format(">> ERROR: non existing return type %s\n",returnType);
			throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");				
		}

		// check if function name already exists in current scope
		if (getSymbolTable().findInCurrentScope(identifier) != null)
		{
			System.out.format(">> ERROR: function name %s already exists in current scope\n",identifier);
			throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
		}

		/***************************/
		/* [1] Semant Input func_input */
		/***************************/
		getSymbolTable().beginScope();

		int paramOffset = 8;

		for (AstTypeIdList it = func_input; it  != null; it = it.tail)
		{
			t = getSymbolTable().find(it.head.type);
			if (t == null || t == TypeVoid.getInstance())
			{
				System.out.format(">> ERROR: non existing type %s\n",it.head.type);	
				throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");			
			}
			else if (getSymbolTable().findInCurrentScope(it.identifier) != null)
			{
				System.out.format(">> ERROR: duplicate variable name %s in function %s\n",it.identifier,this.identifier);	
				throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");			
			}
			else
			{
				type_list = new TypeList(t,type_list);
				getSymbolTable().enter(it.identifier, t, paramOffset, false);
				
				paramOffset += 4; // assuming each parameter takes 4 bytes
			}
		}
		// reverse type_list to maintain order
		TypeList reversed = null;
		while (type_list != null) {
			reversed = new TypeList(type_list.head, reversed);
			type_list = type_list.tail;
		}
		type_list = reversed;

		/***************************************************/
		/* [2] Enter the Function Type to theirClassType's data members */
		/***************************************************/
		TypeFunction funcType = new TypeFunction(returnType,this.identifier,type_list);
		/* Check for overriding */
		Type existingMember = theirClassType.getDataMemberInClass(identifier);
		if (existingMember != null){
			if (!existingMember.isFunction() || theirClassType.hasDataMemberInClass(identifier))
			{
				System.out.format(
					">> ERROR: Overshadowing\\Overloading is not allowed in L\n",
					this.identifier
				);
				throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
			}
			// else, check override correctness
			TypeFunction parentFunc = (TypeFunction) existingMember;

			/* return type must match exactly */
			if (parentFunc.returnType != funcType.returnType)
			{
				System.out.format(
					">> ERROR: return type mismatch in override of %s\n",
					this.identifier
				);
				throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
			}

			/* parameters must match exactly */
			if (!TypeList.sameTypes(parentFunc.paramTypes, funcType.paramTypes))
			{
				System.out.format(
					">> ERROR: parameter list mismatch in override of %s\n",
					this.identifier
				);
				throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
			}
		}
		getSymbolTable().enter(identifier, funcType);
		
		getSymbolTable().resetLocalOffset();

		theirClassType.dataMembers = new TypeList(funcType, theirClassType.dataMembers);

		stmnts_of_funs.semantMe();

		this.numLocals = getSymbolTable().getLocalCount();

		getSymbolTable().endScope();
		
		getSymbolTable().enter(identifier, funcType);

		return offset;
	}

	public Temp irMe()
	{
		addIrCommand(new IrCommandLabel(identifier));

		int returnValSpace = this.return_type.type.equals("void") ? 0 : 1;
		int returnValOffset = -((this.numLocals + returnValSpace) * 4);

		if (returnValSpace > 0) {
			getSymbolTable().setCurrentFunctionReturnOffset(returnValOffset);
		}

		// Calculate frame size inculuding return address if return_type not "void"
		int frameSize = (this.numLocals + returnValSpace) * 4;
		addIrCommand(new IrCommandPrologue(identifier, frameSize));

		// Create and set exit label
		String exitLabel = identifier + "_exit";
		getSymbolTable().setCurrentFunctionExitLabel(exitLabel);

		if (stmnts_of_funs != null) {
			stmnts_of_funs.irMe();
		}

		addIrCommand(new IrCommandLabel(exitLabel));
		
		Temp resTemp = null;
		if (returnValSpace > 0) {
			resTemp = TempFactory.getInstance().getFreshTemp();
			addIrCommand(new IrCommandLoad(resTemp, "return_val", returnValOffset, false));
		}

		addIrCommand(new IrCommandEpilogue(identifier));
		addIrCommand(new IrCommandReturn(resTemp));

		return null;
	}

}