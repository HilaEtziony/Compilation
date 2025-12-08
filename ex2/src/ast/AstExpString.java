package ast;

public class AstExpString extends AstExp
{
    public String string;

    public AstExpString(String string)
    {
        // TODO get line num
        serialNumber = AstNodeSerialNumber.getFresh();
        
        this.string = string;
    }
    public void printMe()
    {
        /***************************************/
        /* AST NODE TYPE = AST STRING EXPRESSION */
        /***************************************/
        System.out.print("AST NODE STRING EXPRESSION\n");

        /**********************************/
        /* PRINT to AST GRAPHVIZ DOT file */
        /**********************************/
        AstGraphviz.getInstance().logNode(
                serialNumber,
            "STRING\n" + string);
    }
}