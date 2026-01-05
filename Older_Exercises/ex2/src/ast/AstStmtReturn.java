package ast;

public class AstStmtReturn extends AstStmt
{
	public AstExp exp;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AstStmtReturn(AstExp exp)
	{
		this.exp = exp;
	}

	public void printMe()
	{
		/**************************************/
		/* AST NODE TYPE = AST STATEMENT RETURN */
		/**************************************/
		System.out.print("AST NODE STMT RETURN\n");

		/*************************************/
		/* RECURSIVELY PRINT EXP ... */
		/*************************************/
		if (exp != null) exp.printMe();

		/**********************************/
		/* PRINT to AST GRAPHVIZ DOT file */
		/**********************************/
		AstGraphviz.getInstance().logNode(
				serialNumber,
			"STMT\nRETURN");

		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (exp != null) AstGraphviz.getInstance().logEdge(serialNumber,exp.serialNumber);
	}
}