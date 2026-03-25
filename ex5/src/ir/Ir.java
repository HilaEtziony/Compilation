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

public class Ir {
	private IrCommand head = null;
	private IrCommandList tail = null;
	public Temp currentObjectPtr = null;

	/******************/
	/* Add Ir command */
	/******************/
	public void AddIrCommand(IrCommand cmd) {
		if ((head == null) && (tail == null)) {
			this.head = cmd;
		} else if ((head != null) && (tail == null)) {
			this.tail = new IrCommandList(cmd, null);
		} else {
			IrCommandList it = tail;
			while ((it != null) && (it.tail != null)) {
				it = it.tail;
			}
			it.tail = new IrCommandList(cmd, null);
		}
	}

	/***************************/
	/* Retrieve IR commands... */
	/***************************/
	public List<IrCommand> getCommands() {
		if ((head == null) && (tail == null)) {
			return Collections.emptyList();
		}

		List<IrCommand> commands = new ArrayList<>();
		if (head != null) {
			commands.add(head);
		}

		IrCommandList it = tail;
		while (it != null) {
			if (it.head != null) {
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
	protected Ir() {
	}

	/******************************/
	/* GET SINGLETON INSTANCE ... */
	/******************************/
	public static Ir getInstance() {
		if (instance == null) {
			/*******************************/
			/* [0] The instance itself ... */
			/*******************************/
			instance = new Ir();
		}
		return instance;
	}

	public void mipsMeData() {
		for (IrCommand cmd : getCommands()) {
			if (cmd.isDataCommand()) {
				cmd.mipsMe();
			}
		}
	}

	/**
	 * Emit MIPS .text section.
	 *
	 * IR layout is: [global var inits] [func defs (A, B, ...)] [main body]
	 * 
	 * Problem: after executing global inits, MIPS falls through into the
	 * first function definition instead of jumping to main's body.
	 *
	 * Solution (2-phase emit):
	 *   Phase 1 - emit global init code (everything before the first IrCommandLabel)
	 *   Then emit "j main_body" to skip over function definitions
	 *   Phase 2 - emit all remaining code (function defs + main body)
	 *             When we hit IR's "main" label, replace it with "main_body"
	 *             (since SPIM's entry point "main:" was already emitted above)
	 */
	public void mipsMeText() {
		List<IrCommand> commands = getCommands();

		// SPIM entry point - execution starts here
		mips.MipsGenerator.getInstance().label("main");

		// Phase 1: emit global variable initialization code
		// (all IR commands before the first function label)
		int firstFuncIdx = 0;
		for (int i = 0; i < commands.size(); i++) {
			IrCommand cmd = commands.get(i);
			if (cmd instanceof IrCommandLabel) {
				// Found first function definition - stop emitting global inits
				firstFuncIdx = i;
				break;
			}
			if (!cmd.isDataCommand()) {
				cmd.mipsMe();
			}
		}

		// After global inits, jump past all function definitions to main's body
		mips.MipsGenerator.getInstance().jump("main_body");

		// Phase 2: emit function definitions and main body
		for (int i = firstFuncIdx; i < commands.size(); i++) {
			IrCommand cmd = commands.get(i);
			if (!cmd.isDataCommand()) {
				// Replace IR's "main" label with "main_body" (the jump target from above)
				if (cmd instanceof IrCommandLabel && ((IrCommandLabel) cmd).labelName.equals("main")) {
					mips.MipsGenerator.getInstance().label("main_body");
					continue;
				}
				cmd.mipsMe();
			}
		}
	}
}
