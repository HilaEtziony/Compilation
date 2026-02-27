/***********/
/* PACKAGE */
/***********/
package ir;

/*******************/
/* GENERAL IMPORTS */
/*******************/
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import temp.Temp;

/*******************/
/* PROJECT IMPORTS */
/*******************/

public class Ir
{
	private IrCommand head=null;
	private IrCommandList tail=null;
	public Temp currentObjectPtr = null;


	/******************/
	/* Add Ir command */
	/******************/
	public void AddIrCommand(IrCommand cmd)
	{
		if ((head == null) && (tail == null))
		{
			this.head = cmd;
		}
		else if ((head != null) && (tail == null))
		{
			this.tail = new IrCommandList(cmd,null);
		}
		else
		{
			IrCommandList it = tail;
			while ((it != null) && (it.tail != null))
			{
				it = it.tail;
			}
			it.tail = new IrCommandList(cmd,null);
		}
	}

	/***************************/
	/* Retrieve IR commands... */
	/***************************/
	public List<IrCommand> getCommands()
	{
		if ((head == null) && (tail == null))
		{
			return Collections.emptyList();
		}

		List<IrCommand> commands = new ArrayList<>();
		if (head != null)
		{
			commands.add(head);
		}

		IrCommandList it = tail;
		while (it != null)
		{
			if (it.head != null)
			{
				commands.add(it.head);
			}
			it = it.tail;
		}

		return Collections.unmodifiableList(commands);
	}

	/**************************************/
	/* USUAL SINGLETON IMPLEMENTATION ... */
	/**************************************/
	private static Ir instance = null;

	/*****************************/
	/* PREVENT INSTANTIATION ... */
	/*****************************/
	protected Ir() {}

	/******************************/
	/* GET SINGLETON INSTANCE ... */
	/******************************/
	public static Ir getInstance()
	{
		if (instance == null)
		{
			/*******************************/
			/* [0] The instance itself ... */
			/*******************************/
			instance = new Ir();
		}
		return instance;
	}

	public void mipsMe(){
		// TODO: implement MIPS code generation
		// method signerature can be changed according 
		// to design decisions 
	}
}
