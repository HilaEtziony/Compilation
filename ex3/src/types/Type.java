package types;

public abstract class Type
{
	/******************************/
	/*  Every type has a name ... */
	/******************************/
	public String name;

	/***************/
    /* Default implementations */
    /***************/
    public boolean isFunction() { return false; }
    public TypeList getParamTypes() { return null; }
    public Type getReturnType() { return null; }
	public boolean isClass(){ return false;}
	public boolean isArray(){ return false;}
	public boolean isNil() { return false; }

	/***************/
    /* isCompatible(Type other) */
    /***************/
	public boolean isCompatible(Type other)
	{
		if (this == other) return true;

		// T2 is nil and T1 is a Class/Array
		if (other.isNil() && (this.isClass() || this.isArray())) return true;
		if (this.isNil() && (other.isClass() || other.isArray())) return true;

		// T2 is a subclass of T1 (Inheritance check). TypeClass has field 'father'
		if (this.isClass() && other.isClass()) // if other is instance of this
		{
			TypeClass curr = (TypeClass)other;
			while (curr != null)
			{
				if (curr.name.equals(this.name))
				{
					return true;
				}

				curr = curr.father;
			}

			curr = (TypeClass)this;
			while (curr != null){ // if this is instance of other
				if (curr.name.equals(other.name))
				{
					return true;
				}

				curr = curr.father;
			}
		}

		// Arrays: only compatible if element types are exactly the same
		if (this.isArray() && other.isArray()) {
			TypeArray thisArr = (TypeArray)this;
			TypeArray otherArr = (TypeArray)other;
			if (thisArr.type_of_array == otherArr.type_of_array) {
				return true;
			}
		}

		return false;
	}
}
