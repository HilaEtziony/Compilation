package types;

import ast.AstExp;

public class TypeClassVarDec extends Type
{
	public Type t;
	public AstExp initValue;
	// Note: 'name' and 'offset' are inherited from Type

	public TypeClassVarDec(Type t, String name, int offset, AstExp initValue)
	{
		this.t = t;
		this.name = name;
		this.offset = offset;
		this.initValue = initValue;
	}

}