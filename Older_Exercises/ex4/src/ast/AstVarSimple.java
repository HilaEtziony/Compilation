package ast;

import semanticError.SemanticErrorException;
import symboltable.*;
import types.*;
import ir.*;
import temp.*;

/*
USAGE:
	| ID:name															{: RESULT = new AstVarSimple(name);       			:}
*/

public class AstVarSimple extends AstVar
{
	/************************/
	/* simple variable name */
	/************************/
	public String name;
	private Integer cachedOffset = null;
    private Boolean cachedIsGlobal = null;
	private Boolean cachedIsClassField = false;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AstVarSimple(String name, int lineNumber)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		serialNumber = AstNodeSerialNumber.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.format("====================== var -> ID( %s )\n",name);

		/*******************************/
		/* COPY INPUT DATA MEMBERS ... */
		/*******************************/
		this.lineNumber = lineNumber;
		this.name = name;
	}

	/**************************************************/
	/* The printing message for a simple var AST node */
	/**************************************************/
	public void printMe()
	{
		/**********************************/
		/* AST NODE TYPE = AST SIMPLE VAR */
		/**********************************/
		System.out.format("AST NODE SIMPLE VAR( %s )\n",name);

		/*********************************/
		/* Print to AST GRAPHVIZ DOT file */
		/*********************************/
		AstGraphviz.getInstance().logNode(
				serialNumber,
			String.format("SIMPLE\nVAR\n(%s)",name));
	}

	public Type semantMe()
	{
		SymbolTableEntry entry = getSymbolTable().findEntry(name);

		if (entry == null)
		{
			System.out.format(">> ERROR: variable %s does not exist\n", name);
			throw new SemanticErrorException("ERROR(" + this.lineNumber + ")");
		}

		Type t = entry.type;

		TypeClass currentClass = getSymbolTable().currentClass;
		
		if (currentClass != null) {
			Type member = currentClass.getDataMemberInClass(name);
			
			if (member != null && !member.isFunction() && entry.offset >= 8) {
				this.cachedIsClassField = true;
			} else {
				this.cachedIsClassField = false;
			}
		}

		cacheEntryInfo();

		return t;
	}

	public Temp irMe()
	{
		ensureEntryInfoAvailable();
		Temp t = TempFactory.getInstance().getFreshTemp();

		if (this.cachedIsClassField && !this.cachedIsGlobal) {
			Temp tThis = TempFactory.getInstance().getFreshTemp();
			// Get object
			addIrCommand(new IrCommandLoad(tThis, "this", 8, false));

			// Get Field Object
			addIrCommand(new IrCommandFieldLoad(t, tThis, cachedOffset));
		} else {
			addIrCommand(new IrCommandLoad(t, name, cachedOffset, cachedIsGlobal));
		}
		
		return t;
	}

	public String getPath() {
		return this.name;
	}

	private void cacheEntryInfo()
    {
		SymbolTableEntry entry = getSymbolTable().findEntry(name);
		if (entry == null)
		{
			throw new IllegalStateException(String.format(
				"Symbol table entry for %s is unavailable during semantic analysis",
				name));
    	}
        this.cachedOffset = entry.offset;
        this.cachedIsGlobal = entry.isGlobal;
    }

    private void ensureEntryInfoAvailable()
    {
        if (cachedOffset == null || cachedIsGlobal == null)
        {
            throw new IllegalStateException(String.format(
                "Variable %s offset/global info not cached before IR generation",
                name));
        }
	}

	public int getCachedOffset()
   {
       ensureEntryInfoAvailable();
       return cachedOffset;
   }

   public boolean isGlobalVariable()
   {
       ensureEntryInfoAvailable();
       return cachedIsGlobal;
   }

   public boolean isClassField() { return cachedIsClassField; }
}
