package ast;

import types.*;
import symboltable.*;
import temp.Temp;
import ir.Ir;
import ir.IrCommand;
import ir.IrCommandJumpIfEqToZero;
import ir.IrCommandJumpLabel;
import ir.IrCommandLabel;
import semanticError.SemanticErrorException;

/*
USAGE:
	| IF LPAREN exp:cond RPAREN LBRACE stmtList:body RBRACE
	  ELSE LBRACE stmtList:elseBody RBRACE 							{: RESULT = new AstStmtIf(cond,body,elseBody); 		:}
	| IF LPAREN exp:cond RPAREN LBRACE stmtList:body RBRACE 		{: RESULT = new AstStmtIf(cond,body); 				:}
*/

public class AstStmtIf extends AstStmt
{
	public AstExp cond;
	public AstStmtList body;
	public AstStmtList elseBody;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AstStmtIf(AstExp cond, AstStmtList body, int lineNumber)
	{
		this(cond, body, null, lineNumber);
	}

	public AstStmtIf(AstExp cond, AstStmtList body, AstStmtList elseBody, int lineNumber)
	{
		serialNumber = AstNodeSerialNumber.getFresh();
		this.lineNumber = lineNumber;
		this.cond = cond;
		this.body = body;
		this.elseBody = elseBody;
	}

	/****************************************************/
	/* The printing message for an if statment AST node */
	/****************************************************/
	public void printMe()
	{
		/*************************************/
		/* AST NODE TYPE = AST SUBSCRIPT VAR */
		/*************************************/
		System.out.print("AST NODE STMT IF\n");

		/**************************************/
		/* RECURSIVELY PRINT left + right ... */
		/**************************************/
		if (cond != null) cond.printMe();
		if (body != null) body.printMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AstGraphviz.getInstance().logNode(
                serialNumber,
			"IF (left)\nTHEN right");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (cond != null) AstGraphviz.getInstance().logEdge(serialNumber,cond.serialNumber);
		if (body != null) AstGraphviz.getInstance().logEdge(serialNumber,body.serialNumber);
	}

	public Type semantMe()
	{
		/****************************/
		/* [0] Semant the Condition */
		/****************************/
		if (cond.semantMe() != TypeInt.getInstance()) // cond should be considered an int
		{
			System.out.format(">> ERROR: condition inside IF is not integral\n");
			throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
		}
		
		/*************************/
		/* [1] Begin If Scope */
		/*************************/
		getSymbolTable().beginScope();

		/***************************/
		/* [2] Semant Data Members */
		/***************************/
		body.semantMe();

		/*****************/
		/* [3] End Scope */
		/*****************/
		getSymbolTable().endScope();

		if (elseBody != null)
		{
			/*************************/
			/* [1] Begin Else Scope */
			/*************************/
			getSymbolTable().beginScope();

			/***************************/
			/* [2] Semant Data Members */
			/***************************/
			elseBody.semantMe();

			/*****************/
			/* [3] End Scope */
			/*****************/
			getSymbolTable().endScope();
		}

		/***************************************************/
		/* [4] Return value is irrelevant for if statement */
		/**************************************************/
		return null;
	}

    public Temp irMe() {
        /*******************************************************************/
        /* [1] Create fresh labels for the 'else' block and the 'end' block */
        /*******************************************************************/
        String labelElse = IrCommand.getFreshLabel("if_else");
        String labelEnd  = IrCommand.getFreshLabel("if_end");

        /*******************************************************************/
        /* [2] Generate IR for the condition. It returns a Temp with 1 or 0 */
        /*******************************************************************/
        Temp condTemp = cond.irMe();

        /*******************************************************************/
        /* [3] If condition is false (0), jump to the 'else' label.         */
        /* If there is no else, it will jump to the end of the if.     */
        /*******************************************************************/
        addIrCommand(new IrCommandJumpIfEqToZero(condTemp, labelElse));

        /*******************************************************************/
        /* [4] Generate IR for the 'then' body (if the condition was true)  */
        /*******************************************************************/
        if (body != null) {
            body.irMe();
        }

        /*******************************************************************/
        /* [5] After 'then' body, jump to the end to skip the 'else' block  */
        /*******************************************************************/
        addIrCommand(new IrCommandJumpLabel(labelEnd));

        /*******************************************************************/
        /* [6] Place the 'else' label here.                                */
        /*******************************************************************/
        addIrCommand(new IrCommandLabel(labelElse));

        /*******************************************************************/
        /* [7] Generate IR for the 'else' body (if it exists)              */
        /*******************************************************************/
        if (elseBody != null) {
            elseBody.irMe();
        }

        /*******************************************************************/
        /* [8] Place the 'end' label to mark the exit of the if-else block */
        /*******************************************************************/
        addIrCommand(new IrCommandLabel(labelEnd));

        /*******************************************************************/
        /* [9] Statements do not produce a value, so we return null        */
        /*******************************************************************/
        return null;
    }
}