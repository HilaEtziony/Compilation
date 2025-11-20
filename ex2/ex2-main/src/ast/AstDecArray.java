package ast;

public abstract class AstDecArray extends AstDec
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
}

/*
USAGES:

arrayTypedef ::= 	ARRAY ID:i EQ type:t LBRACK RBRACK SEMICOLON					{: RESULT = new AstArrayTypeDef(i,t); 				:}
*/