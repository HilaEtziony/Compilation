package ast;

import types.*;
import symboltable.*;
import semanticError.SemanticErrorException;

/*
USAGE:
	| CLASS ID:name EXTENDS ID:parentName LBRACE cFieldList:l RBRACE	{: RESULT = new AstDecClass(name,parentName,l); 	:}
	| CLASS ID:name LBRACE cFieldList:l RBRACE							{: RESULT = new AstDecClass(name,null,l); 			:}
*/

public class AstDecClass extends AstDec
{
    public String name;
    public String parentName;
    public AstDecList cFieldList;

    public AstDecClass(String name, String parentName, AstDecList cFieldList, int lineNumber)
    {
        serialNumber = AstNodeSerialNumber.getFresh();
		this.lineNumber = lineNumber;
        this.name = name;
        this.parentName = parentName;
        this.cFieldList = cFieldList;
    }

    /*********************************************************/
	/* The printing message for a class declaration AST node */
	/*********************************************************/
	public void printMe()
	{
		/*************************************/
		/* RECURSIVELY PRINT HEAD + TAIL ... */
		/*************************************/
		System.out.format("CLASS DEC = %s\n",name);
		if (cFieldList != null) cFieldList.printMe();
		
		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AstGraphviz.getInstance().logNode(
                serialNumber,
			String.format("CLASS\n%s",name));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		AstGraphviz.getInstance().logEdge(serialNumber, cFieldList.serialNumber);
	}

	public Type semantMe()
	{
		/* [0a] Make sure class doesn't already exist */
		if (SymbolTable.getInstance().find(name) != null) {
			System.out.format("ERROR: class %s already exists in symbol table\n",name);
			throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
		}

		Type parentType = SymbolTable.getInstance().find(parentName);
		/* [0b] Make sure father exist */
		if (parentName != null) {
			if (parentType == null || !(parentType instanceof TypeClass)) {
				System.out.format("ERROR: parent class %s of class %s doesn't exist\n",parentName,name);
				throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
			}
		}

		/* [0c] Prevent circular dependencies */
		if (parentName != null) {
			// if got here, then parentType exists and is TypeClass

			TypeClass curr = (TypeClass) parentType;
			while (curr != null){
				if (curr.name.equals(this.name))
				{
					System.out.format("ERROR: class %s cannot extend itself, directly nor indirectly\n",name);
					throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
				}

				curr = curr.father;
			}
		}

		/*************************/
		/* [1] Begin Class Scope */
		/*************************/
		SymbolTable.getInstance().beginScope();

		/*******************************/
		/* [1a] Semant Class ...  */
		/*******************************/
		TypeClass t = new TypeClass(null,name, null);

		/***************************/
		/* [2] Semant Data Members */
		/***************************/
		if (cFieldList != null) {
			// cFieldList.semantMe(t); // TODO should a class field be entered any differently than regular declarations?
			// cFieldList.semantMe();

			// TODO
		}

		/*****************/
		/* [3] End Scope */
		/*****************/
		SymbolTable.getInstance().endScope();

		/************************************************/
		/* [4] Enter the Class Type to the Symbol Table */
		/************************************************/
		SymbolTable.getInstance().enter(name,t);

		/*********************************************************/
		/* [5] Return value is irrelevant for class declarations */
		/*********************************************************/
		return null;
	}
}

