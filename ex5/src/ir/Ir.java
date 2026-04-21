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
		java.util.Set<Integer> globalInitIndices = new java.util.HashSet<>();

		// Scan once so we can separate global initializers from function bodies
		// even when function labels appear earlier in the IR list.
		boolean inFunction = false;
		for (int i = 0; i < commands.size(); i++) {
			IrCommand cmd = commands.get(i);

			if (cmd instanceof IrCommandLabel) {
				boolean isFunctionEntry = (i + 1 < commands.size()) && (commands.get(i + 1) instanceof IrCommandPrologue);
				if (isFunctionEntry) {
					inFunction = true;
				}
			}

			if (!cmd.isDataCommand() && !inFunction && !(cmd instanceof IrCommandLabel)) {
				globalInitIndices.add(i);
			}

			if (inFunction && cmd instanceof IrCommandEpilogue) {
				inFunction = false;
			}
		}

		// SPIM entry point - execution starts here
		mips.MipsGenerator.getInstance().label("main");

		// Phase 1: emit all global initialization instructions before the jump
		// over the function section.
		for (int i = 0; i < commands.size(); i++) {
			if (globalInitIndices.contains(i)) {
				commands.get(i).mipsMe();
			}
		}

		// After global inits, jump past all function definitions to main's body
		mips.MipsGenerator.getInstance().jump("main_body");

		// Phase 2: emit function/class-init definitions and main body
		for (int i = 0; i < commands.size(); i++) {
			IrCommand cmd = commands.get(i);
			if (cmd.isDataCommand() || globalInitIndices.contains(i)) {
				continue;
			}

			// Replace IR's main function label with main_body (jump target from above)
			if (cmd instanceof IrCommandLabel && ((IrCommandLabel) cmd).labelName.equals("func_main")) {
				mips.MipsGenerator.getInstance().label("main_body");
				continue;
			}

			cmd.mipsMe();
		}
	}
}
