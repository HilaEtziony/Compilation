package ast;

public class AstCFieldList extends AstNode
{
    public AstCField head;
    public AstCFieldList tail;

    public AstCFieldList(AstCField head, AstCFieldList tail)
    {
        // TODO get line num
        serialNumber = AstNodeSerialNumber.getFresh();

        if (tail != null) System.out.print("====================== cFieldList -> cField cFieldList\n");
        if (tail == null) System.out.print("====================== cFieldList -> cField            \n");

        this.head = head;
        this.tail = tail;
    }

    public void printMe()
    {
        /**************************************/
        /* AST NODE TYPE = AST CLASS FIELD LIST */
        /**************************************/
        System.out.print("AST NODE CFIELD LIST\n");

        /*************************************/
        /* RECURSIVELY PRINT HEAD + TAIL ... */
        /*************************************/
        if (head != null) head.printMe();
        if (tail != null) tail.printMe();

        /**********************************/
        /* PRINT to AST GRAPHVIZ DOT file */
        /**********************************/
        AstGraphviz.getInstance().logNode(
                serialNumber,
            "CFIELD\nLIST\n");
        
        /****************************************/
        /* PRINT Edges to AST GRAPHVIZ DOT file */
        /****************************************/
        if (head != null) AstGraphviz.getInstance().logEdge(serialNumber,head.serialNumber);
        if (tail != null) AstGraphviz.getInstance().logEdge(serialNumber,tail.serialNumber);
    }
}