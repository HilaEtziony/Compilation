package ast;

import types.*;
import symboltable.*;

/*
USAGE:
	| exp:e1 PLUS exp:e2											{: RESULT = new AstExpBinop(e1, e2, 0); 			:}
	| exp:e1 MINUS exp:e2											{: RESULT = new AstExpBinop(e1, e2, 1); 			:}
	| exp:e1 TIMES exp:e2											{: RESULT = new AstExpBinop(e1, e2, 2); 			:}
	| exp:e1 DIVIDE exp:e2											{: RESULT = new AstExpBinop(e1, e2, 3); 			:}
	| exp:e1 LT exp:e2												{: RESULT = new AstExpBinop(e1, e2, 4); 			:}
	| exp:e1 GT exp:e2												{: RESULT = new AstExpBinop(e1, e2, 5); 			:}
	| exp:e1 EQ exp:e2												{: RESULT = new AstExpBinop(e1, e2, 6); 			:}
*/

public class AstExpBinop extends AstExp
{
	int op;
	public AstExp left;
	public AstExp right;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AstExpBinop(AstExp left, AstExp right, int op)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		serialNumber = AstNodeSerialNumber.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.print("====================== exp -> exp BINOP exp\n");

		/*******************************/
		/* COPY INPUT DATA MEMBERS ... */
		/*******************************/
		this.left = left;
		this.right = right;
		this.op = op;
	}
	
	/*************************************************/
	/* The printing message for a binop exp AST node */
	/*************************************************/
	public void printMe()
	{
		String sop="";
		
		/*********************************/
		/* CONVERT OP to a printable sop */
		/*********************************/
		if (op == 0) {sop = "+";}
		if (op == 1) {sop = "-";}
		if (op == 3) {sop = "*";}
		if (op == 4) {sop = "/";}
		if (op == 4) {sop = "<";}
		if (op == 5) {sop = ">";}
		if (op == 6) {sop = "==";}

		/**********************************/
		/* AST NODE TYPE = AST BINOP EXP */
		/*********************************/
		System.out.print("AST NODE BINOP EXP\n");
		System.out.format("BINOP EXP(%s)\n",sop);

		/**************************************/
		/* RECURSIVELY PRINT left + right ... */
		/**************************************/
		if (left != null) left.printMe();
		if (right != null) right.printMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AstGraphviz.getInstance().logNode(
                serialNumber,
			String.format("BINOP(%s)",sop));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (left  != null) AstGraphviz.getInstance().logEdge(serialNumber,left.serialNumber);
		if (right != null) AstGraphviz.getInstance().logEdge(serialNumber,right.serialNumber);
	}

	public Type semantMe()
	{
		Type t1 = null;
		Type t2 = null;

		if (left  != null) t1 = left.semantMe();
		if (right != null) t2 = right.semantMe();

		// + is allowed in TypeInt, TypeString
		// *, -, /, <, >, == are allowed in TypeInt only
		if ((t1 == TypeInt.getInstance()) && (t2 == TypeInt.getInstance()))
		{
			if (op == 0 /* + */ || op == 1 /* - */ || op == 2 /* * */)
			{
				return TypeInt.getInstance();
			}

			if (op == 3 /* / */)
			{
				// Division by zero check could be added here if right is a constant expression
				if (right.isConstant()) // TODO add isConstant() to AstExp
				{
					int rightValue = right.getConstantValue(); // TODO add getConstantValue() to AstExp
					if (rightValue == 0)
					{
						System.out.format(">> ERROR: Division by zero\n");
						System.exit(0);
					}
				}

				return TypeInt.getInstance();
			}

			if (op == 4 /* < */ || op == 5 /* > */ || op == 6 /* == */)
			{
				return TypeInt.getInstance(); // L has 2 primitive types, but still we're splitting for login just in case that'll change
			}
		}

		if (t1 == TypeString.getInstance() && t2 == TypeString.getInstance())
		{
			if (op == 0 /* + */){
				return TypeString.getInstance();
			}

			if (op == 4 /* < */ || op == 5 /* > */ || op == 6 /* == */){
				return TypeInt.getInstance();
			}
		}

		System.exit(0);
		return null;
	}
}