package ast;

public class AstTypeIdList extends AstDec
{
    public AstVarType head;
    public AstTypeIdList tail;

    public AstTypeIdList(AstVarType type, String identifier, AstTypeIdList rest_of_list){
        // TODO get line num
        serialNumber = AstNodeSerialNumber.getFresh();

        // TODO
        this.head = type;
        this.tail = rest_of_list;
    }

    public void printMe()
    {
        /**************************************/
        /* AST NODE TYPE = AST TYPE ID LIST */
        /**************************************/
        System.out.print("AST NODE TYPE ID LIST\n");

        /*************************************/
        /* RECURSIVELY PRINT HEAD ... */
        /*************************************/
        if (head != null) head.printMe();

        /*************************************/
        /* RECURSIVELY PRINT TAIL ... */
        /*************************************/
        if (tail != null) tail.printMe();

        /**********************************/
        /* PRINT to AST GRAPHVIZ DOT file */
        /**********************************/
        AstGraphviz.getInstance().logNode(
                serialNumber,
            "TYPE ID\nLIST");

        /****************************************/
        /* PRINT Edges to AST GRAPHVIZ DOT file */
        /****************************************/
        if (head != null) AstGraphviz.getInstance().logEdge(serialNumber,head.serialNumber);
        if (tail != null) AstGraphviz.getInstance().logEdge(serialNumber,tail.serialNumber);
    }
}

/*
accepts:

RESULT = new AstTypeIdList(t,i,l);
RESULT = new AstTypeIdList(t,i,null);
*/