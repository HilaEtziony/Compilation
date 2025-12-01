package ast;

import types.*;
import symboltable.*;

public class AstTypeIdList extends AstDec
{
    public AstVarType head;
    public String identifier;
    public AstTypeIdList tail;

    public AstTypeIdList(AstVarType type, String identifier, AstTypeIdList rest_of_list){
        // TODO get line num
        serialNumber = AstNodeSerialNumber.getFresh();

        this.head = type;
        this.identifier = identifier;
        this.tail = rest_of_list;
    }
}

/*
accepts:

RESULT = new AstTypeIdList(t,i,l);
RESULT = new AstTypeIdList(t,i,null);
*/