package ast;

import semanticError.SemanticErrorException;
import symboltable.*;
import types.*;
import temp.*;
import ir.*;

/*
USAGE:
	| type:t ID:i ASSIGN exp:e SEMICOLON 								{: RESULT = new AstVarDec(t,new AstVarSimple(i),e); 	:}
	| type:t ID:i SEMICOLON											{: RESULT = new AstVarDec(t,new AstVarSimple(i),null); 	:}
	| type:t ID:i ASSIGN newExp:nE SEMICOLON						{: RESULT = new AstVarDec(t,new AstVarSimple(i),nE); 	:}
*/

// TODO rename to AstDecVar, consistency with class's origin
public class AstVarDec extends AstDec
{
    public AstVarType type;
    public AstVarSimple id;
    public AstExp expr;
	public boolean isClassField = false; // to distinguish between class field and local/global variable
	public int offset;
    public boolean isGlobal;

    public AstVarDec(AstVarType type, AstVarSimple id, AstExp expr, int lineNumber)
    {
        serialNumber = AstNodeSerialNumber.getFresh();
		this.lineNumber = lineNumber;
        this.type = type;
        this.id = id;
        this.expr = expr;
    }

    /************************************************************/
	/* The printing message for a variable declaration AST node */
	/************************************************************/
	public void printMe()
	{
		/****************************************/
		/* AST NODE TYPE = AST VAR DECLARATION */
		/***************************************/
		if (expr != null) System.out.format("VAR-DEC(%s):%s := expr\n",id.name,type.type);
		if (expr == null) System.out.format("VAR-DEC(%s):%s                \n",id.name,type.type);

		/**************************************/
		/* RECURSIVELY PRINT expr ... */
		/**************************************/
		if (expr != null) expr.printMe();

		/**********************************/
		/* PRINT to AST GRAPHVIZ DOT file */
		/**********************************/
		AstGraphviz.getInstance().logNode(
                serialNumber,
			String.format("VAR\nDEC(%s)\n:%s",id.name,type.type));
			// String.format("VAR\nDEC(%s)\n:%s",name,type));  // TODO change AstVarType

		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (expr != null) AstGraphviz.getInstance().logEdge(serialNumber,expr.serialNumber);

	}

	// Target: Ensure no duplicate names in the current scope and that the declared type exists.
	public Type semantMe()
	{
		Type t;

		/****************************/
		/* [1] Check If Type exists */
		/****************************/
		t = getSymbolTable().find(type.type);
		// t = getSymbolTable().find(type); // TODO change AstVarType
		if (t == null)
		{
			System.out.format(">> ERROR: non existing type %s\n",type.type);
			// System.out.format(">> ERROR [%d:%d] non existing type %s\n",2,2,type);
			throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
		}

		/****************************/
		/* [2] Check t is void */
		/****************************/
		if (t == TypeVoid.getInstance()) {
			System.out.format(">> ERROR: variable %s cannot be of type void\n", id.name);
			throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
		}

		/**************************************/
		/* [3] Check That Name does NOT exist */
		/**************************************/
		if (getSymbolTable().findInCurrentScope(id.name) != null)
		{
			System.out.format(">> ERROR: variable %s already exists in scope\n",id.name);
			throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
		}

		/************************************************/
		/* [4] Calculate offset and enter to Table */
		/************************************************/
		this.isGlobal = getSymbolTable().isGlobalScope();
		this.offset = getSymbolTable().calculateNewOffset(this.isGlobal);

		/************************************************/
		/* [5] Enter the Identifier to the Symbol Table */
		/************************************************/
		getSymbolTable().enter(id.name, t, this.offset, this.isGlobal);

		// check the assignment expression, if exists
		if(expr != null) {
			Type t_expr = expr.semantMe();
			// check assignment compatibility
			if(!t.isCompatible(t_expr)) {
				System.out.format(">> ERROR: cannot assign %s to %s\n", t_expr.name, t.name);
				throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
			}
		}

		return null;
	}

	public int semantMe(TypeClass theirClassType, int offset) {
		this.isClassField = true;
		// same as above, but enter to theirClassType's data members instead of symbol table
		Type t; // placeholder
		/****************************/
		/* [1] Check If Type exists */
		/****************************/
		t = getSymbolTable().find(type.type);
		if (t == null)
		{
			System.out.format(">> ERROR: non existing type %s\n",type.type);
			// System.out.format(">> ERROR [%d:%d] non existing type %s\n",2,2,type);
			throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
		}

		/****************************/
		/* [2] Check t is void      */
		/****************************/
		if (t == TypeVoid.getInstance()) {
			System.out.format(">> ERROR: variable %s cannot be of type void\n", id.name);
			throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
		}

		/**************************************/
		/* [3] Check Shadowing */
		/**************************************/
		if (theirClassType.getDataMemberInClass(id.name) != null)
		{
			System.out.format(">> ERROR: variable %s already exists in class %s\n",id.name, theirClassType.name);
			throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
		}

		if(this.type.type.equals("int")) {
			t = TypeInt.getInstance();
		}
		else if(this.type.type.equals("string")) {
			t = TypeString.getInstance();
		}
		else if(this.type.type.equals("void")) {
			// should have been caught earlier
			System.out.format(">> ERROR: variable %s cannot be of type void\n", id.name);
			throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
		}
		else {
			// must be a class type or array type
			Type classArrayType = getSymbolTable().find(this.type.type);
			//check if array
			if(classArrayType != null && classArrayType instanceof TypeArray){
				t = classArrayType;
			}
			// else must be class or error
			else if (classArrayType == null || !(classArrayType instanceof TypeClass)) {
				System.out.format(">> ERROR: non existing type %s\n",this.type.type);
				throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
			}
			t = classArrayType;
		}

		/************************************************/
		/* [4] Enter the Identifier to the Class Data Members */
		/************************************************/
		TypeClassVarDec fieldDescriptor = new TypeClassVarDec(t, id.name, offset); // set the name of the type to the variable's name
		theirClassType.dataMembers = new TypeList((Type)fieldDescriptor, theirClassType.dataMembers);
		getSymbolTable().enter(id.name,t, offset, false);

		// check the assignment expression, if exists
		if(expr != null) {
			Type t_expr = expr.semantMe();
			// check assignment compatibility
			if(!t.isCompatible(t_expr)) {
				System.out.format(">> ERROR: cannot assign %s to %s\n", t_expr.name, t.name);
				throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
			}
		}

		return offset + 4; // assuming each variable takes 4 bytes
	}

	public Temp irMe()
	{
		if (this.isClassField) {
			if (ir.Ir.getInstance().currentObjectPtr == null) {
				return null;
			}
			
			if (expr != null) {
				Temp val = expr.irMe();
				addIrCommand(new IrCommandFieldStore(ir.Ir.getInstance().currentObjectPtr, this.offset, val));
			}
			return null;
		}
		else{
			// Not generate allocate command for global variable 
			if (!this.isGlobal) {
				addIrCommand(new IrCommandAllocate(id.name));
			}

			if (expr != null)
			{
				Temp valueTemp = expr.irMe();
				addIrCommand(new IrCommandStore(id.name, valueTemp, this.offset, this.isGlobal));
			}
		}

		return null;
	}
}
