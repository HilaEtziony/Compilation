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

    @Override
    public String toString() {
        String target = (varTemp != null) ? String.format("%s.%s", varTemp, id) : id;
        String argsStr = formatArgs();
        if (res == null) {
            return String.format("call %s(%s)", target, argsStr);
        }
        return String.format("%s := call %s(%s)", res, target, argsStr);
    }

    private String formatArgs() {
        if (args == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        TempList current = args;
        while (current != null) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(current.head);
            current = current.tail;
        }
        return sb.toString();
    }
}