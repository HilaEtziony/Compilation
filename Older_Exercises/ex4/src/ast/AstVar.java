package ast;

import types.*;
import symboltable.*;
import semanticError.SemanticErrorException;

public abstract class AstVar extends AstNode
{
	public Type semantMe()
	{
		return null;
	}

	public String getPath() {
		return null;
	}
}
