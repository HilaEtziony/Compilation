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

    /***********************************************/
    /* Print the TypeList - for debugging only    */
    /***********************************************/
    public void print()
    {
        System.out.print("TypeList: ");
        for (TypeList it = this; it != null; it = it.tail) {
            System.out.print(it.head.name + " ");
        }
        System.out.println();
    }

    public static boolean sameTypes(TypeList a, TypeList b)
    {
        // Both are empty
        if (a == null && b == null)
            return true;

        // One is empty, the other is not
        if (a == null || b == null)
            return false;

        while (a != null && b != null)
        {
            if (a.head != b.head)
                return false;

            a = a.tail;
            b = b.tail;
        }

        return a == null && b == null;
    }

}
