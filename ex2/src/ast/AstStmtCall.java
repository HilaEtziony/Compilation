package ast;

public class AstStmtCall extends AstStmt
{
	/***************/
	/*  var := exp */
	/***************/
	public AstExpCall expCall;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AstStmtCall(AstExpCall expCall)
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
		// this.var = var;
		this.expCall = expCall;
	}

	/*********************************************************/
	/* The printing message for an assign statement AST node */
	/*********************************************************/
	public void printMe()
	{
		/********************************************/
		/* AST NODE TYPE = AST CALL STATEMENT */
		/********************************************/
		System.out.print("AST NODE CALL STMT\n");

		/***********************************/
		/* RECURSIVELY PRINT VAR + EXP ... */
		/***********************************/
		if (expCall != null) expCall.printMe();
		// if (exp != null) exp.printMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AstGraphviz.getInstance().logNode(
				serialNumber,
			"ASSIGN\nleft := right\n");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		AstGraphviz.getInstance().logEdge(serialNumber,expCall.serialNumber);
		// AstGraphviz.getInstance().logEdge(serialNumber,exp.serialNumber);
	}
}
