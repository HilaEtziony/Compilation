package ast;

public abstract class AstNewExp extends AstDec
{
    public AstVarType type;
    public AstExp exp;

    public AstNewExp(AstVarType type, AstExp exp)
    {
        // TODO get line num
        serialNumber = AstNodeSerialNumber.getFresh();

        this.type = type;
        this.exp = exp;
    }
}

/*
USAGES:

newExp 		::= 	NEW type:t 														{: RESULT = new AstNewExp(t, null); 				:}
					| NEW type:t LBRACK exp:e RBRACK								{: RESULT = new AstNewExp(t, e); 					:}
*/