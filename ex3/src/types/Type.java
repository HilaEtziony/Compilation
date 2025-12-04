package types;

public abstract class Type
{
	/******************************/
	/*  Every type has a name ... */
	/******************************/
	public String name;

	/*************/
	/* isClass() */
	/*************/
	public boolean isClass(){ return false;}

	/*************/
	/* isArray() */
	/*************/
	public boolean isArray(){ return false;}

	public boolean isCompatible(Type other)
	{
		if (this == other) return true;

		// T2 is nil and T1 is a Class/Array
		if (other.name.equals("nil") && (this.isClass() || this.isArray())) return true;
		if (this.name.equals("nil") && (other.isClass() || other.isArray())) return true;

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


		return false;
	}
}
