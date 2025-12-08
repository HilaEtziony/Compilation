package ast;

public class AstDecArray extends AstDec
{
    public String identifier;
    public AstVarType type;

    public AstDecArray(String identifier, AstVarType type)
    {
        // TODO get line num
        serialNumber = AstNodeSerialNumber.getFresh();
        
        this.identifier = identifier;
        this.type = type;
    }

    public void printMe()
    {
        /**************************************/
        /* AST NODE TYPE = AST DECLARATION ARRAY */
        /**************************************/
        System.out.print("AST NODE DEC ARRAY\n");

        /*************************************/
        /* RECURSIVELY PRINT TYPE ... */
        /*************************************/
        if (type != null) type.printMe();

        /**********************************/
        /* PRINT to AST GRAPHVIZ DOT file */
        /**********************************/
        AstGraphviz.getInstance().logNode(
                serialNumber,
            "DEC\nARRAY\n" + identifier);
        
        /****************************************/
        /* PRINT Edges to AST GRAPHVIZ DOT file */
        /****************************************/
        if (type != null) AstGraphviz.getInstance().logEdge(serialNumber,type.serialNumber);
    }
}

/*
USAGES:

arrayTypedef ::= 	ARRAY ID:i EQ type:t LBRACK RBRACK SEMICOLON					{: RESULT = new AstArrayTypeDef(i,t); 				:}
*/