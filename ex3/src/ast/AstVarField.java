package ast;

import semanticError.SemanticErrorException;
import types.*;

/*
USAGE:
	| var:v DOT ID:fieldName										{: RESULT = new AstVarField(v,fieldName); 			:}
*/

public class AstVarField extends AstVar
{
	public AstVar var;
	public String fieldName;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AstVarField(AstVar var, String fieldName, int lineNumber)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		serialNumber = AstNodeSerialNumber.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.format("====================== var -> var DOT ID( %s )\n",fieldName);

		/*******************************/
		/* COPY INPUT DATA MEMBERS ... */
		/*******************************/
		this.lineNumber = lineNumber;
		this.var = var;
		this.fieldName = fieldName;
	}

	/*************************************************/
	/* The printing message for a field var AST node */
	/*************************************************/
	public void printMe()
	{
		/*********************************/
		/* AST NODE TYPE = AST FIELD VAR */
		/*********************************/
		System.out.print("AST NODE FIELD VAR\n");

		/**********************************************/
		/* RECURSIVELY PRINT VAR, then FIELD NAME ... */
		/**********************************************/
		if (var != null) var.printMe();
		System.out.format("FIELD NAME( %s )\n",fieldName);

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AstGraphviz.getInstance().logNode(
				serialNumber,
			String.format("FIELD\nVAR\n...->%s",fieldName));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (var != null) AstGraphviz.getInstance().logEdge(serialNumber,var.serialNumber);
	}

	public Type semantMe()
	{
		// Get the type of the base variable
		Type varType = var.semantMe();
		System.out.println("Base variable type: " + varType);
		// Check that the base variable is of a class type
		if (!(varType instanceof TypeClass))
		{
			System.out.format("ERROR: variable is not of a class type\n");
			throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
		}
		TypeClass classType = (TypeClass) varType;
		System.out.println("Class type: " + classType.name);
		// Look for the field in the class
		TypeList fieldList = classType.dataMembers;
		System.out.println("Looking for field: " + fieldName + " " + fieldList);

		while (fieldList != null)
		{
			Type field = fieldList.head;
			System.out.println("Checking field: " + field.name);
			if (field.name.equals(fieldName))
			{
				System.out.println("Field found: " + field.name + " of type " + field);
				return field;
			}
			fieldList = fieldList.tail;
		}	

		// Field not found
		System.out.format("ERROR: field %s does not exist in class %s\n", fieldName, classType.name);
		throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
	}
}
