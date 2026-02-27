package types;

public class TypeClassVarDec extends Type
{
	public Type t;
	// Note: 'name' and 'offset' are inherited from Type

	public TypeClassVarDec(Type t, String name, int offset)
	{
		this.t = t;
		this.name = name;
		this.offset = offset;
	}

}