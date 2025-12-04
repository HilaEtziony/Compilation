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
		// TODO $T2$ is nil and $T1$ is a Class/Array1111
		// TODO $T2$ is a subclass of $T1$ (Inheritance check)
	}
}
