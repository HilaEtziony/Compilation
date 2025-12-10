package ast;

import semanticError.SemanticErrorException;
import types.*;

/*
USAGE:
	| type:t ID:i COMMA typeIdList:l									{: RESULT = new AstTypeIdList(t,i,l);    			:}
	| type:t ID:i													{: RESULT = new AstTypeIdList(t,i,null);    		:}
*/

public class AstTypeIdList extends AstDec
{
    public AstVarType head;
    public String identifier;
    public AstTypeIdList tail;

    public AstTypeIdList(AstVarType type, String identifier, AstTypeIdList rest_of_list, int lineNumber){
        serialNumber = AstNodeSerialNumber.getFresh();
		this.lineNumber = lineNumber;
        this.head = type;
        this.identifier = identifier;
        this.tail = rest_of_list;
    }

	/************************************************************/
	/* The printing message for a type id list AST node */
	/************************************************************/
	public void printMe()
	{
		/****************************************/
		/* AST NODE TYPE = AST TYPE ID LIST */
		/****************************************/
		System.out.format("TYPE-ID(%s):%s\n", identifier, head.type);

		/**************************************/
		/* RECURSIVELY PRINT head + tail ... */
		/**************************************/
		if (head != null) head.printMe();
		if (tail != null) tail.printMe();

		/**********************************/
		/* PRINT to AST GRAPHVIZ DOT file */
		/**********************************/
		AstGraphviz.getInstance().logNode(
                serialNumber,
			String.format("TYPE-ID(%s)\n:%s", identifier, head.type));

		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (head != null) AstGraphviz.getInstance().logEdge(serialNumber, head.serialNumber);
		if (tail != null) AstGraphviz.getInstance().logEdge(serialNumber, tail.serialNumber);
	}

	public Type semantMe()
	{
		return null;
	}

	public TypeList getTypeList() throws SemanticErrorException
	{
		TypeList type_list_tail = null;
		if (this.tail != null){
			type_list_tail = this.tail.getTypeList();
		}

		Type head_type = this.head.semantMe();
		return new TypeList(head_type, type_list_tail);
	}
}