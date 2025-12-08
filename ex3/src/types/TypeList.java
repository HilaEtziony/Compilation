package types;

public class TypeList
{
	/****************/
	/* DATA MEMBERS */
	/****************/
	public Type head;
	public TypeList tail;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public TypeList(Type head, TypeList tail)
	{
		this.head = head;
		this.tail = tail;
	}

	/***********************************************/
    /* Return the length of the TypeList          */
    /***********************************************/
    public int length()
    {
        if (tail == null) return 1;
        return 1 + tail.length();
    }

    /***********************************************/
    /* Return the i-th Type in the list           */
    /***********************************************/
    public Type get(int i)
    {
        if (i == 0) return head;
        return tail.get(i - 1);
    }
}
