package types;

public class TypeClass extends Type
{
	/*********************************************************************/
	/* If this class does not extend a father class this should be null  */
	/*********************************************************************/
	public TypeClass father;
	public int size;

	/**************************************************/
	/* Gather up all data members in one place        */
	/* Note that data members coming from the AST are */
	/* packed together with the class methods         */
	/**************************************************/
	public TypeList dataMembers;
	
	/****************/
	/* CTROR(S) ... */
	/****************/
	public TypeClass(TypeClass father, String name, TypeList dataMembers)
	{
		this.name = name;
		this.father = father; // may be null
		this.dataMembers = dataMembers;
		this.size = 0;
	}

	/****************/
	/* Functions... */
	/****************/

	@Override
	public boolean isClass(){ return true;}

	/**********************************************/
    /* Get a method by name, searching recursively */
    /**********************************************/
    public Type getMethod(String id)
    {
        // Check current class members
        TypeList current = dataMembers;
        while (current != null)
        {
            Type t = current.head;
            if (t.isFunction() && t.name.equals(id))
            {
                return t;
            }
            current = current.tail;
        }

        // Check in father class recursively
        if (father != null)
        {
            return father.getMethod(id);
        }

        // Not found
        return null;
    }

	public boolean hasDataMemberInClass(String id)
	{
		TypeList current = dataMembers;
		while (current != null)
		{
			Type t = current.head;
			if (t.name.equals(id))
			{
				return true;
			}
			current = current.tail;
		}
		// Not found
		return false;
	}

	public int calcTotalFields()
	{
		int count = 0;
		TypeList current = dataMembers;

		while (current != null)
		{
			if (!current.head.isFunction())
			{
				count++;
			}
			current = current.tail;
		}

		if (father != null)
		{
			count += father.calcTotalFields();
		}

		return count;
	}

	public Type getDataMemberInClass(String id)
	{
		TypeClass curr_class = this;
		while (curr_class != null)
		{
			TypeList current_members = curr_class.dataMembers;
			while (current_members != null)
			{
				Type t = current_members.head;
				if (t != null && t.name != null && t.name.equals(id))
				{
					return t;
				}
				current_members = current_members.tail;
			}
			curr_class = curr_class.father;
		}
		return null;
	}
}
