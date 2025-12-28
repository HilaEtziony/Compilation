package ir;

import temp.*;

/*
USAGE:
	| var:v DOT ID:i LPAREN expList:l RPAREN 							{: RESULT = new AstExpCall(v,i,l);    				:}
	| var:v DOT ID:i LPAREN RPAREN									{: RESULT = new AstExpCall(v,i,null);    			:}
	| ID:i LPAREN expList:l RPAREN 									{: RESULT = new AstExpCall(null,i,l);    			:}
	| ID:i LPAREN RPAREN											{: RESULT = new AstExpCall(null,i,null);    		:}
*/

public class IrCommandCall extends IrCommand {
    public Temp res;
    public Temp varTemp;
    public String id;
    public TempList args;

    public IrCommandCall(Temp res, Temp varTemp, String id, TempList args) {
        this.res = res;
        this.varTemp = varTemp;
        this.id = id;
        this.args = args;
    }
}