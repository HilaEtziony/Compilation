package types;

public class TypeArray extends Type
{
    public Type type_of_array;

    /****************/
	/* CTROR(S) ... */
	/****************/
	public TypeArray(String name, Type type_of_array)
	{
        this.name = name;
		this.type_of_array = type_of_array;
	}

	/****************/
	/* Functions... */
	/****************/

	@Override
	public boolean isArray(){ return true;}
}
