package symboltable;

import java.io.PrintWriter;
import types.*;

public class SymbolTable
{
	private int hashArraySize = 13;
	
	public static final String SCOPE_BOUNDARY = "SCOPE-BOUNDARY";

	/**********************************************/
	/* The actual symbol table data structure ... */
	/**********************************************/
	private SymbolTableEntry[] table = new SymbolTableEntry[hashArraySize];
	private SymbolTableEntry top;
	private int topIndex = 0;
	public TypeClass currentClass = null;
	/**************************************************************/
	/* A very primitive hash function for exposition purposes ... */
	/**************************************************************/
	private int hash(String s)
	{
		if (s == null) return 0;
		return (s.hashCode() & 0x7fffffff) % hashArraySize;
	}

	/****************************************************************************/
	/* Enter a variable, function, class type or array type to the symbol table */
	/****************************************************************************/
	public void enter(String name, Type t)
	{
		/*************************************************/
		/* [1] Compute the hash value for this new entry */
		/*************************************************/
		int hashValue = hash(name);

		/******************************************************************************/
		/* [2] Extract what will eventually be the next entry in the hashed position  */
		/*     NOTE: this entry can very well be null, but the behaviour is identical */
		/******************************************************************************/
		SymbolTableEntry next = table[hashValue];
	
		/**************************************************************************/
		/* [3] Prepare a new symbol table entry with name, type, next and prevtop */
		/**************************************************************************/
		SymbolTableEntry e = new SymbolTableEntry(name,t,hashValue,next,top, topIndex++);

		/**********************************************/
		/* [4] Update the top of the symbol table ... */
		/**********************************************/
		top = e;
		
		/****************************************/
		/* [5] Enter the new entry to the table */
		/****************************************/
		table[hashValue] = e;
		
		/**************************/
		/* [6] Print Symbol Table */
		/**************************/
		// printMe();
		// this.printStackTopDown(5);
	}

	/***********************************************/
	/* Find the inner-most scope element with name */
	/***********************************************/
	public Type find(String name)
	{
		if (name == null) return null;
		SymbolTableEntry e;
				
		for (e = table[hash(name)]; e != null; e = e.next)
		{
			if (name.equals(e.name))
			{
				return e.type;
			}
		}
		
		return null;
	}

	/*****************************************************************/
	/* Find the inner-most scope element with name in current scope  */
	/*****************************************************************/
	public Type findInCurrentScope(String name)
	{
		if (name == null) return null;
		
		for (SymbolTableEntry e = top; e != null && !e.name.equals(SCOPE_BOUNDARY); e = e.prevtop)
		{
			if (name.equals(e.name))
			{
				return e.type;
			}
		}
		
		return null;
	}

	/***************************************************************************/
	/* begine scope = Enter the <SCOPE-BOUNDARY> element to the data structure */
	/***************************************************************************/
	public void beginScope()
	{
		// this.printStackTopDown(7);
		/************************************************************************/
		/* Though <SCOPE-BOUNDARY> entries are present inside the symbol table, */
		/* they are not really types. In order to be able to debug print them,  */
		/* a special TYPE_FOR_SCOPE_BOUNDARIES was developed for them. This     */
		/* class only contain their type name which is the bottom sign: _|_     */
		/************************************************************************/
		enter(
			SCOPE_BOUNDARY,
			new TypeForScopeBoundaries("NONE"));

		// System.out.println("BEGINNING NEW SCOPE" + " of type: " + top.type + " with name: " + top.name);
		/*********************************************/
		/* Print the symbol table after every change */
		/*********************************************/
		printMe();
		// this.printStackTopDown(7);
	}

	/********************************************************************************/
	/* end scope = Keep popping elements out of the data structure,                 */
	/* from most recent element entered, until a <NEW-SCOPE> element is encountered */
	/********************************************************************************/
	public void endScope()
	{
		// System.out.println("ENDING SCOPE");
		// this.printStackTopDown(5);
		// printMe();
		/**************************************************************************/
		/* Pop elements from the symbol table stack until a SCOPE-BOUNDARY is hit */		
		/**************************************************************************/		
		while (!top.name.equals(SCOPE_BOUNDARY))
		{
			//System.out.println("Popping symbol: " + top.name + " of type: " + top.type);
			table[top.index] = top.next;
			topIndex--;
			top = top.prevtop;
		}
		/**************************************/
		/* Pop the SCOPE-BOUNDARY sign itself */		
		/**************************************/
		//System.out.println("Popping SCOPE BOUNDARY" + " of type: " + top.type + " with name: " + top.name);
		table[top.index] = top.next;
		topIndex = top.index;
		top = top.prevtop;

		/*********************************************/
		/* Print the symbol table after every change */		
		/*********************************************/
		printMe();
		// this.printStackTopDown(5);
		// System.out.println("SCOPE ENDED" );
	}
	
	public static int n=0;
	
	public void printMe()
	{
		int i=0;
		int j=0;
		String dirname="./output/";
		String filename=String.format("SYMBOL_TABLE_%d_IN_GRAPHVIZ_DOT_FORMAT.txt",n++);

		try
		{
			/*******************************************/
			/* [1] Open Graphviz text file for writing */
			/*******************************************/
			PrintWriter fileWriter = new PrintWriter(dirname+filename);

			/*********************************/
			/* [2] Write Graphviz dot prolog */
			/*********************************/
			fileWriter.print("digraph structs {\n");
			fileWriter.print("rankdir = LR\n");
			fileWriter.print("node [shape=record];\n");

			/*******************************/
			/* [3] Write Hash Table Itself */
			/*******************************/
			fileWriter.print("hashTable [label=\"");
			for (i=0;i<hashArraySize-1;i++) { fileWriter.format("<f%d>\n%d\n|",i,i); }
			fileWriter.format("<f%d>\n%d\n\"];\n",hashArraySize-1,hashArraySize-1);
		
			/****************************************************************************/
			/* [4] Loop over hash table array and print all linked lists per array cell */
			/****************************************************************************/
			for (i=0;i<hashArraySize;i++)
			{
				if (table[i] != null)
				{
					/*****************************************************/
					/* [4a] Print hash table array[i] -> entry(i,0) edge */
					/*****************************************************/
					fileWriter.format("hashTable:f%d -> node_%d_0:f0;\n",i,i);
				}
				j=0;
				for (SymbolTableEntry it = table[i]; it!=null; it=it.next)
				{
					/*******************************/
					/* [4b] Print entry(i,it) node */
					/*******************************/
					fileWriter.format("node_%d_%d ",i,j);
					fileWriter.format("[label=\"<f0>%s|<f1>%s|<f2>prevtop=%d|<f3>next\"];\n",
						it.name,
						it.type.name,
						it.prevtopIndex);

					if (it.next != null)
					{
						/***************************************************/
						/* [4c] Print entry(i,it) -> entry(i,it.next) edge */
						/***************************************************/
						fileWriter.format(
							"node_%d_%d -> node_%d_%d [style=invis,weight=10];\n",
							i,j,i,j+1);
						fileWriter.format(
							"node_%d_%d:f3 -> node_%d_%d:f0;\n",
							i,j,i,j+1);
					}
					j++;
				}
			}
			fileWriter.print("}\n");
			fileWriter.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}		
	}
	
	/**************************************/
	/* USUAL SINGLETON IMPLEMENTATION ... */
	/**************************************/
	private static SymbolTable instance = null;

	/*****************************/
	/* PREVENT INSTANTIATION ... */
	/*****************************/
	protected SymbolTable() {}

	/******************************/
	/* GET SINGLETON INSTANCE ... */ // -> only 1 instance of symbol table is created
	/******************************/ // whenever we want to modify it, we call SymbolTable.getInstance().XXX
	public static SymbolTable getInstance()
	{
		if (instance == null)
		{
			/*******************************/
			/* [0] The instance itself ... */
			/*******************************/
			instance = new SymbolTable();
			// instance.beginScope();
			/*****************************************/
			/* [1] Enter primitive types int, string */
			/*****************************************/
			instance.enter("int",   TypeInt.getInstance());
			instance.enter("string", TypeString.getInstance());

			/*************************************/
			/* [2] How should we handle void ??? */
			/*************************************/

			instance.enter("void",  TypeVoid.getInstance());

			/***************************************/
			/* [3] Enter library functions */
			/***************************************/
			instance.enter(
				"PrintInt",
				new TypeFunction( // this PrintInt func represents a func obj, returns void, takes int as args
					TypeVoid.getInstance(),
					"PrintInt",
					new TypeList(
						TypeInt.getInstance(),
						null)));

			instance.enter(
				"PrintString",
				new TypeFunction( // this PrintString func represents a func obj, returns void, takes string as args
					TypeVoid.getInstance(),
					"PrintString",
					new TypeList(
						TypeString.getInstance(),
						null)));
		}

		return instance;
	}

	public boolean isInFunction(){
		SymbolTableEntry temp = this.top;
		while (temp != null){
			if (temp.type instanceof TypeFunction){
				return true;
			}

			temp = temp.prevtop;
		}

		return false;
	}

	public Type getCurrentFunctionReturnType() {
		SymbolTableEntry temp = this.top;
		// System.out.println("Getting current function return type...");
		while (temp != null){
			// System.out.println("Checking symbol: " + temp.name + " of type: " + temp.type);
			if (temp.type instanceof TypeFunction){
				TypeFunction funcType = (TypeFunction) temp.type;
				// System.out.println("Found function: " + funcType.name + " with return type: " + funcType.returnType);
				return funcType.returnType;
			}

			temp = temp.prevtop;
		}
		return TypeVoid.getInstance();
	}

	// Debug utility: print the top N entries of the stack
	public void printStackTopDown(int n) {
		SymbolTableEntry curr = top;
		int count = 0;
		System.out.print("[STACK] Top-down: ");
		while (curr != null && count < n) {
			System.out.print(curr.name + " (" + curr.type.name + ")");
			curr = curr.prevtop;
			count++;
			if (curr != null && count < n) System.out.print(" -> ");
		}
		System.out.println();
	}
}