package ast;

public class AstExpCall extends AstExp
{
    public AstVar var;
    public String id;
    public AstExpList expList;

    public AstExpCall(AstVar var, String id, AstExpList expList)
    {
        // TODO get line num
        serialNumber = AstNodeSerialNumber.getFresh();

        this.var = var;
        this.id = id;
        this.expList = expList;
    }
}

/*
USAGES:

callExp 	::= 	var:v DOT ID:i LPAREN expList:l RPAREN 							{: RESULT = new AstCallExp(v,i,l);    				:}
					| var:v DOT ID:i LPAREN RPAREN									{: RESULT = new AstCallExp(v,i,null);    			:}
					| ID:i LPAREN expList:l RPAREN 									{: RESULT = new AstCallExp(null,i,l);    			:}
					| ID:i LPAREN RPAREN											{: RESULT = new AstCallExp(null,i,null);    		:}
*/