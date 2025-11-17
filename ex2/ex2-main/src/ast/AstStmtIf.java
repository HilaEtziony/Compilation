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
}