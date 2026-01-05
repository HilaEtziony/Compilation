package ast;

public class AstStmtIf extends AstStmt
{
	public AstExp cond;
	public AstStmtList body;
	public AstStmtList elseBody;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AstStmtIf(AstExp cond, AstStmtList body)
	{
		this(cond, body, null);
	}

	public AstStmtIf(AstExp cond, AstStmtList body, AstStmtList elseBody)
	{
		this.cond = cond;
		this.body = body;
		this.elseBody = elseBody;
	}

	public void printMe()
	{
		/**************************************/
		/* AST NODE TYPE = AST STATEMENT IF */
		/**************************************/
		System.out.print("AST NODE STMT IF\n");

		/*************************************/
		/* RECURSIVELY PRINT COND ... */
		/*************************************/
		if (cond != null) cond.printMe();

		/*************************************/
		/* RECURSIVELY PRINT BODY ... */
		/*************************************/
		if (body != null) body.printMe();

		/*************************************/
		/* RECURSIVELY PRINT ELSE BODY ... */
		/*************************************/
		if (elseBody != null) elseBody.printMe();

		/**********************************/
		/* PRINT to AST GRAPHVIZ DOT file */
		/**********************************/
		AstGraphviz.getInstance().logNode(
				serialNumber,
			"STMT\nIF");

		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (cond != null) AstGraphviz.getInstance().logEdge(serialNumber,cond.serialNumber);
		if (body != null) AstGraphviz.getInstance().logEdge(serialNumber,body.serialNumber);
		if (elseBody != null) AstGraphviz.getInstance().logEdge(serialNumber,elseBody.serialNumber);
	}
}