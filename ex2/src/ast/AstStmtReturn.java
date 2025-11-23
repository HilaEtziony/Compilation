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
}