package ast;

import ir.Ir;
import ir.IrCommandFieldLoad;
import ir.IrCommandLoad;
import semanticError.SemanticErrorException;
import temp.Temp;
import temp.TempFactory;
import types.*;

/*
USAGE:
	| var:v DOT ID:fieldName										{: RESULT = new AstVarField(v,fieldName); 			:}
*/

public class AstVarField extends AstVar
{
	public AstVar var;
	public String fieldName;
	public int fieldOffset;

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
		if(varType instanceof TypeClassVarDec) varType = ((TypeClassVarDec)varType).t;

		// Check that the base variable is of a class type
		if (!(varType instanceof TypeClass))
		{
			System.err.println(this + "|" + this.fieldName + "|" + this.lineNumber + "|"+this.var + "|" + varType);
			System.out.format("ERROR: variable is not of a class type\n");
			throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
		}
		TypeClass classType = (TypeClass) varType;

		while(classType != null){
			// Look for the field in the class
			TypeList fieldList = classType.dataMembers;

			while (fieldList != null)
			{
				Type field = fieldList.head;
				if (field.name.equals(fieldName) && (field instanceof TypeClassVarDec))
				{
					this.fieldOffset = field.offset;
					return field;
				}
				fieldList = fieldList.tail;
			}

			// Field not found - check in parent classes
			classType = classType.father;
		}
		System.out.format("ERROR: field %s does not exist in class %s\n", fieldName, ((TypeClass)varType).name);
		throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
	}

	public Temp irMe()
	{
		Temp base = var.irMe();
		Temp dst = TempFactory.getInstance().getFreshTemp();
		addIrCommand(new IrCommandFieldLoad(dst, base, this.fieldOffset));
		return dst;
	}

	public String getPath() {
		return var.getPath() + "." + fieldName;
	}
}

