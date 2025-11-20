package ast;

public abstract class AstExpList extends AstDec
{
    public AstExp head;
    public AstExpList tail;

    public AstExpList(AstExp head, AstExpList tail)
    {
        // TODO get line num
        serialNumber = AstNodeSerialNumber.getFresh();

        this.head = head;
        this.tail = tail;
    }
}

/*
USAGES:

expList		::=		exp:e COMMA expList:l											{: RESULT = new AstExpList(e,l);    				:}
					| exp:e															{: RESULT = new AstExpList(e,null);    				:}
*/