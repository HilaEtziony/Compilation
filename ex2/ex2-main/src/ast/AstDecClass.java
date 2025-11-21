package ast;

public abstract class AstDecClass extends AstDec
{
    public String name;
    public String parentName;
    public AstCFieldList cFieldList;

    public AstDecClass(String name, String parentName, AstCFieldList cFieldList)
    {
        // TODO get line num
        serialNumber = AstNodeSerialNumber.getFresh();

        this.name = name;
        this.parentName = parentName;
        this.cFieldList = cFieldList;
    }
}

/*
USAGES:

classDec 	::= 	CLASS ID:name EXTENDS ID:parentName LBRACE cFieldList:l RBRACE	{: RESULT = new AstDecClass(name,parentNmae,l); 	:}
					| CLASS ID:name LBRACE cFieldList:l RBRACE						{: RESULT = new AstDecClass(name,null,l); 			:}
*/