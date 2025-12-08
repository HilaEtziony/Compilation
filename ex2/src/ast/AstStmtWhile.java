package ast;

public class AstStmtWhile extends AstStmt
{
	public AstExp cond;
	public AstStmtList body;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AstStmtWhile(AstExp cond, AstStmtList body)
	{
		this.cond = cond;
		this.body = body;
	}

	public void printMe()
	{
		/**************************************/
		/* AST NODE TYPE = AST STATEMENT WHILE */
		/**************************************/
		System.out.print("AST NODE STMT WHILE\n");

		/*************************************/
		/* RECURSIVELY PRINT COND ... */
		/*************************************/
		if (cond != null) cond.printMe();

		/*************************************/
		/* RECURSIVELY PRINT BODY ... */
		/*************************************/
		if (body != null) body.printMe();

		/**********************************/
		/* PRINT to AST GRAPHVIZ DOT file */
		/**********************************/
		AstGraphviz.getInstance().logNode(
				serialNumber,
			"STMT\nWHILE");

		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (cond != null) AstGraphviz.getInstance().logEdge(serialNumber,cond.serialNumber);
		if (body != null) AstGraphviz.getInstance().logEdge(serialNumber,body.serialNumber);
	}
}