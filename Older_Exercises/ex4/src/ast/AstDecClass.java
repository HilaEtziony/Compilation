package ast;

import semanticError.SemanticErrorException;
import symboltable.*;
import temp.Temp;
import types.*;

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
        /********************************************************/
        /* [0a] Check if the class name is already in use       */
        /********************************************************/
        if (SymbolTable.getInstance().find(name) != null) {
            System.out.format("ERROR: class %s already exists in symbol table\n", name);
            throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
        }

        /********************************************************/
        /* [0b] Check if the parent class exists (if extended)  */
        /********************************************************/
        Type parentType = null;
        if (parentName != null) {
            parentType = SymbolTable.getInstance().find(parentName);
            if (parentType == null || !(parentType instanceof TypeClass)) {
                System.out.format("ERROR: parent class %s of class %s doesn't exist\n", parentName, name);
                throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
            }
        }

        /********************************************************/
        /* [0c] Check for circular inheritance                  */
        /********************************************************/
        if (parentName != null) {
            TypeClass curr = (TypeClass) parentType;
            while (curr != null) {
                if (curr.name.equals(this.name)) {
                    System.out.format("ERROR: circular inheritance detected for class %s\n", name);
                    throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
                }
                curr = curr.father;
            }
        }

        /********************************************************/
        /* [1] Create the Class Type object                     */
        /********************************************************/
        TypeClass t = new TypeClass((TypeClass)parentType, name, null);

        /********************************************************/
        /* [2] Register the class in the Global Symbol Table    */
        /* Doing this before beginScope allows fields to    */
        /* reference the class itself (Recursion).          */
        /********************************************************/
        SymbolTable.getInstance().enter(name, t);

        /********************************************************/
        /* [3] Begin Class Scope                                */
        /********************************************************/
        SymbolTable.getInstance().beginScope();
        SymbolTable.getInstance().currentClass = t;

		// Calculate the starting offset: 4 (for the VTable) if there is no parent, or the size of the parent if it exists
        int startOffset = 4;
        if (parentType != null) {
            startOffset = ((TypeClass)parentType).size;
        }


		/********************************************************/
        /* [4] Import Parent Members into current scope         */
        /********************************************************/
        if (parentType != null) {
            TypeClass curr = (TypeClass) parentType;
            while (curr != null) {
                for (TypeList it = curr.dataMembers; it != null; it = it.tail) {
					String memberName = it.head.name;
                    // enter into symbol table just if the name doesn't exist in the current scope and
					// the name is not redefined in the current class
                    if (SymbolTable.getInstance().findInCurrentScope(memberName) == null &&
                        !isRedefinedInCurrentClass(memberName)) 
                    {
                        SymbolTable.getInstance().enter(memberName, it.head);
                    }
                }
                curr = curr.father;
            }
        }

		/********************************************************/
        /* [5] Semant Class Members with Offsets                */
        /********************************************************/
        if (this.cFieldList != null) {
            t.size = this.cFieldList.semantMe(t, startOffset);
        } else {
            t.size = startOffset;
        }

        /********************************************************/
        /* [6] End Class Scope                                  */
        /********************************************************/
        SymbolTable.getInstance().endScope();
        SymbolTable.getInstance().currentClass = null;

		/********************************************************/
        /* [7] Register the final TypeClass globally            */
        /********************************************************/
        getSymbolTable().enter(name, t);

        return null;
    }

	public Temp irMe()
	{
        for (AstDecList it = cFieldList; it != null; it = it.tail)
        {
            if (it.head instanceof AstDecFunc)
            {
                it.head.irMe();
            }
        }
		return null;
	}

	private boolean isRedefinedInCurrentClass(String nameToCheck) {
		if (cFieldList == null) return false;
		for (AstDecList it = cFieldList; it != null; it = it.tail) {
			if (it.head instanceof AstVarDec) {
				if (((AstVarDec)it.head).id.name.equals(nameToCheck)) return true;
			}
			if (it.head instanceof AstDecFunc) {
				if (((AstDecFunc)it.head).identifier.equals(nameToCheck)) return true;
			}
		}
		return false;
	}

}

