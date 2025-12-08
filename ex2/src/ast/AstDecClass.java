package ast;

public class AstDecClass extends AstDec
{
    public String name;
    public String parentName;
    public AstDecList cFieldList;

    public AstDecClass(String name, String parentName, AstDecList cFieldList)
    {
        // TODO get line num
        serialNumber = AstNodeSerialNumber.getFresh();

        this.name = name;
        this.parentName = parentName;
        this.cFieldList = cFieldList;
    }

    public void printMe()
    {
        /**************************************/
        /* AST NODE TYPE = AST DECLARATION CLASS */
        /**************************************/
        System.out.print("AST NODE DEC CLASS\n");

        /*************************************/
        /* RECURSIVELY PRINT cFieldList ... */
        /*************************************/
        if (cFieldList != null) cFieldList.printMe();

        /**********************************/
        /* PRINT to AST GRAPHVIZ DOT file */
        /**********************************/
        AstGraphviz.getInstance().logNode(
                serialNumber,
            "DEC\nCLASS\n" + name + 
            (parentName != null ? ("\nEXTENDS\n" + parentName) : ""));
        
        /****************************************/
        /* PRINT Edges to AST GRAPHVIZ DOT file */
        /****************************************/
        if (cFieldList != null) AstGraphviz.getInstance().logEdge(serialNumber,cFieldList.serialNumber);
    }
}

/*
USAGES:

classDec 	::= 	CLASS ID:name EXTENDS ID:parentName LBRACE cFieldList:l RBRACE	{: RESULT = new AstDecClass(name,parentNmae,l); 	:}
					| CLASS ID:name LBRACE cFieldList:l RBRACE						{: RESULT = new AstDecClass(name,null,l); 			:}
*/