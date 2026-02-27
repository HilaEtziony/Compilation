package types;

public class TypeFunction extends Type
{
	/***********************************/
	/* The return type of the function */
	/***********************************/
	public Type returnType;

	/*************************/
	/* types of input params */
	/*************************/
	public TypeList paramTypes;

    /*************************/
	/* class name for methods */
	/*************************/
    public String className; 
	
	/****************/
	/* CTROR(S) ... */
	/****************/
    public TypeFunction(Type returnType, String name, TypeList paramTypes) {
        this.name = name;
        this.returnType = returnType;
        this.paramTypes = paramTypes;
    }

    @Override
    public boolean isFunction() { return true; }

    @Override
    public TypeList getParamTypes() { return paramTypes; }

    @Override
    public Type getReturnType() { return returnType; }
}
