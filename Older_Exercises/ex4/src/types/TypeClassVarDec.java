package types;

public class TypeClassVarDec extends Type
{
	public Type t;
	public String name;
	public int offset; // added to help with offsets of class fields

	public TypeClassVarDec(Type t, String name, int offset)
	{
		this.t = t;
		this.name = name;
		this.offset = offset;
	}

}
