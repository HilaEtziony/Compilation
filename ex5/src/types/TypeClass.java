package types;

public class TypeClass extends Type
{
	/*********************************************************************/
	/* If this class does not extend a father class this should be null  */
	/*********************************************************************/
	public TypeClass father;
	public int size;
	public TypeList methods;

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

	private TypeList copyTypeList(TypeList original) {
		if (original == null) return null;
		return new TypeList(original.head, copyTypeList(original.tail));
	}

	private void updateOrAddMethod(Type newMethod) {
		TypeList curr = this.methods;
		TypeList prev = null;

		while (curr != null) {
			if (curr.head.name.equals(newMethod.name)) {
				curr.head = newMethod;
				return;
			}
			prev = curr;
			curr = curr.tail;
		}

		if (prev == null) {
			this.methods = new TypeList(newMethod, null);
		} else {
			prev.tail = new TypeList(newMethod, null);
		}
	}

	private TypeList reverseTypeList(TypeList list) {
		TypeList reversed = null;
		for (TypeList it = list; it != null; it = it.tail) {
			reversed = new TypeList(it.head, reversed);
		}
		return reversed;
	}

	public void buildMethodsList(TypeList localMembers) {
		if (this.father != null) {
			this.methods = copyTypeList(this.father.methods);
		}

		TypeList orderedMembers = reverseTypeList(localMembers);

		for (TypeList it = localMembers; it != null; it = it.tail) {
			if (it.head.isFunction()) {
				updateOrAddMethod(it.head);
			}
		}
	}

	public int getMethodOffset(String methodName) {
		int offset = 0;
		TypeList curr = this.methods;
		while (curr != null) {
			if (curr.head.name.equals(methodName)) {
				return offset * 4; 
			}
			offset++;
			curr = curr.tail;
		}
		return -1; 
	}
}
