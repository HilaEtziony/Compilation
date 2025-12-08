package ast;

import types.*;
import symboltable.*;

/*
USAGE:

funcDec 	::= 	type:t ID:i LPAREN typeIdList:l RPAREN LBRACE stmtList:s RBRACE {: RESULT = new AstDecFunc(t,i,l,s);    			:}
					| type:t ID:i LPAREN RPAREN LBRACE stmtList:s RBRACE			{: RESULT = new AstDecFunc(t,i,null,s);    			:}

classDec 	::= 	CLASS ID:name EXTENDS ID:parentName LBRACE cFieldList:l RBRACE	{: RESULT = new AstDecClass(name,parentName,l); 	:}
					| CLASS ID:name LBRACE cFieldList:l RBRACE						{: RESULT = new AstDecClass(name,null,l); 			:}

cFieldList	::=		cField:c	cFieldList:l										{: RESULT = new AstDecList(c,l);    				:}
					| cField:c														{: RESULT = new AstDecList(c,null); 				:}

arrayTypedef ::= 	ARRAY ID:i EQ type:t LBRACK RBRACK SEMICOLON					{: RESULT = new AstDecArray(i,t); 					:}
*/

public abstract class AstDec extends AstStmt
{
    public AstDec()
    {
        serialNumber = AstNodeSerialNumber.getFresh();
    }

    public abstract Type semantMe();

    // public Type semantMe(TypeClass theirClassType) // // Dec of a class = cField. Yamit: Not sure if needed
}

/*
This class is only being derived-from. Need to think about fields that should be shared among all declarations.
*/
