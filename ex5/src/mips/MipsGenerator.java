/***********/
/* PACKAGE */
/***********/
package mips;

/*******************/
/* GENERAL IMPORTS */
/*******************/
import java.io.PrintWriter;

/*******************/
/* PROJECT IMPORTS */
/*******************/
import temp.*;

public class MipsGenerator {
	private static final int WORD_SIZE = 4;
	/***********************/
	/* The file writer ... */
	/***********************/
	private PrintWriter fileWriter;

	/***********************/
	/* The file writer ... */
	/***********************/
	public void finalizeFile() {
		fileWriter.print("\tli $v0,10\n");
		fileWriter.print("\tsyscall\n");
		fileWriter.close();
	}

	public void printInt(Temp t) {
		int idx = t.getSerialNumber();
		// fileWriter.format("\taddi $a0,Temp_%d,0\n",idx);
		fileWriter.format("\tmove $a0,Temp_%d\n", idx);
		fileWriter.format("\tli $v0,1\n");
		fileWriter.format("\tsyscall\n");
		fileWriter.format("\tli $a0,32\n");
		fileWriter.format("\tli $v0,11\n");
		fileWriter.format("\tsyscall\n");
	}

	// public Temp addressLocalVar(int serialLocalVarNum)
	// {
	// Temp t = TempFactory.getInstance().getFreshTemp();
	// int idx = t.getSerialNumber();
	//
	// fileWriter.format("\taddi
	// Temp_%d,$fp,%d\n",idx,-serialLocalVarNum*WORD_SIZE);
	//
	// return t;
	// }

	public void allocate(String varName) {
		fileWriter.format(".data\n");
		fileWriter.format("\tglobal_%s: .word 721\n", varName);
	}

	public void load(Temp dst, String varName) {
		int idxdst = dst.getSerialNumber();
		fileWriter.format("\tlw Temp_%d,global_%s\n", idxdst, varName);
	}

	public void store(String varName, Temp src) {
		int idxsrc = src.getSerialNumber();
		fileWriter.format("\tsw Temp_%d,global_%s\n", idxsrc, varName);
	}

	public void li(Temp t, int value) {
		int idx = t.getSerialNumber();
		fileWriter.format("\tli Temp_%d,%d\n", idx, value);
	}

	public void add(Temp dst, Temp oprnd1, Temp oprnd2) {
		int i1 = oprnd1.getSerialNumber();
		int i2 = oprnd2.getSerialNumber();
		int dstidx = dst.getSerialNumber();

		fileWriter.format("\tadd Temp_%d,Temp_%d,Temp_%d\n", dstidx, i1, i2);
	}

	public void mul(Temp dst, Temp oprnd1, Temp oprnd2) {
		int i1 = oprnd1.getSerialNumber();
		int i2 = oprnd2.getSerialNumber();
		int dstidx = dst.getSerialNumber();

		fileWriter.format("\tmul Temp_%d,Temp_%d,Temp_%d\n", dstidx, i1, i2);
	}

	public void label(String inlabel) {
		if (inlabel.equals("main")) {
			fileWriter.format(".text\n");
			fileWriter.format("%s:\n", inlabel);
		} else {
			fileWriter.format("%s:\n", inlabel);
		}
	}

	public void jump(String inlabel) {
		fileWriter.format("\tj %s\n", inlabel);
	}

	public void blt(Temp oprnd1, Temp oprnd2, String label) {
		int i1 = oprnd1.getSerialNumber();
		int i2 = oprnd2.getSerialNumber();

		fileWriter.format("\tblt Temp_%d,Temp_%d,%s\n", i1, i2, label);
	}

	public void bge(Temp oprnd1, Temp oprnd2, String label) {
		int i1 = oprnd1.getSerialNumber();
		int i2 = oprnd2.getSerialNumber();

		fileWriter.format("\tbge Temp_%d,Temp_%d,%s\n", i1, i2, label);
	}

	public void bne(Temp oprnd1, Temp oprnd2, String label) {
		int i1 = oprnd1.getSerialNumber();
		int i2 = oprnd2.getSerialNumber();

		fileWriter.format("\tbne Temp_%d,Temp_%d,%s\n", i1, i2, label);
	}

	public void beq(Temp oprnd1, Temp oprnd2, String label) {
		int i1 = oprnd1.getSerialNumber();
		int i2 = oprnd2.getSerialNumber();

		fileWriter.format("\tbeq Temp_%d,Temp_%d,%s\n", i1, i2, label);
	}

	public void beqz(Temp oprnd1, String label) {
		int i1 = oprnd1.getSerialNumber();

		fileWriter.format("\tbeq Temp_%d,$zero,%s\n", i1, label);
	}

	/*******************************/
	/* Array operations */
	/*******************************/
	public void newArray(Temp dst, Temp size) {
		int idxDst = dst.getSerialNumber();
		int idxSize = size.getSerialNumber();

		// Calculate bytes needed: (size + 1) * 4
		fileWriter.format("\taddi $s0,Temp_%d,1\n", idxSize); // $s0 = size + 1
		fileWriter.format("\tsll $s0,$s0,2\n"); // $s0 = (size + 1) * 4
		fileWriter.format("\tmove $a0,$s0\n"); // $a0 = allocation size
		fileWriter.format("\tli $v0,9\n"); // syscall 9 = malloc
		fileWriter.format("\tsyscall\n");
		fileWriter.format("\tsw Temp_%d,0($v0)\n", idxSize); // store length at offset 0
		fileWriter.format("\tmove Temp_%d,$v0\n", idxDst); // dst = base address
	}

	// TODO hila 2.5 — runtime checks in arrayLoad/arrayStore depend on handler
	// labels (access_violation_handler, invalid_ptr_dref_handler)
	public void arrayLoad(Temp dst, Temp base, Temp index) {
		int idxDst = dst.getSerialNumber();
		int idxBase = base.getSerialNumber();
		int idxIndex = index.getSerialNumber();

		// TODO hila 2.5 — null pointer check (needs invalid_ptr_dref_handler)
		fileWriter.format("\tbeq Temp_%d,$zero,invalid_ptr_dref_handler\n", idxBase);

		// 2. Load array length from offset 0
		fileWriter.format("\tlw $s0,0(Temp_%d)\n", idxBase);

		// TODO hila 2.5 — bounds checks (need access_violation_handler)
		// 3. Bounds check: index < 0
		fileWriter.format("\tbltz Temp_%d,access_violation_handler\n", idxIndex);

		// 4. Bounds check: index >= length
		fileWriter.format("\tbge Temp_%d,$s0,access_violation_handler\n", idxIndex);

		// 5. Calculate address: base + (index + 1) * 4
		fileWriter.format("\taddi $s0,Temp_%d,1\n", idxIndex);
		fileWriter.format("\tsll $s0,$s0,2\n");
		fileWriter.format("\tadd $s0,Temp_%d,$s0\n", idxBase);
		fileWriter.format("\tlw Temp_%d,0($s0)\n", idxDst);
	}

	public void arrayStore(Temp base, Temp index, Temp src) {
		int idxBase = base.getSerialNumber();
		int idxIndex = index.getSerialNumber();
		int idxSrc = src.getSerialNumber();

		// TODO hila 2.5 — null pointer check (needs invalid_ptr_dref_handler label)
		fileWriter.format("\tbeq Temp_%d,$zero,invalid_ptr_dref_handler\n", idxBase);

		// 2. Load array length from offset 0
		fileWriter.format("\tlw $s0,0(Temp_%d)\n", idxBase);

		// TODO hila 2.5 — bounds checks (need access_violation_handler label)
		// 3. Bounds check: index < 0
		fileWriter.format("\tbltz Temp_%d,access_violation_handler\n", idxIndex);

		// 4. Bounds check: index >= length
		fileWriter.format("\tbge Temp_%d,$s0,access_violation_handler\n", idxIndex);

		// 5. Calculate address and store
		fileWriter.format("\taddi $s0,Temp_%d,1\n", idxIndex);
		fileWriter.format("\tsll $s0,$s0,2\n");
		fileWriter.format("\tadd $s0,Temp_%d,$s0\n", idxBase);
		fileWriter.format("\tsw Temp_%d,0($s0)\n", idxSrc);
	}

	// TODO hila 2.5 — add emitErrorHandlers() method here that emits
	// access_violation_handler, invalid_ptr_dref_handler, illegal_div_by_0_handler
	// labels with their error message printing + exit syscall code

	/**************************************/
	/* USUAL SINGLETON IMPLEMENTATION ... */
	/**************************************/
	private static MipsGenerator instance = null;

	/*****************************/
	/* PREVENT INSTANTIATION ... */
	/*****************************/
	protected MipsGenerator() {
	}

	/******************************/
	/* GET SINGLETON INSTANCE ... */
	/******************************/
	public static MipsGenerator getInstance() {
		if (instance == null) {
			/*******************************/
			/* [0] The instance itself ... */
			/*******************************/
			instance = new MipsGenerator();

			try {
				/*********************************************************************************/
				/*
				 * [1] Open the MIPS text file and write data section with error message strings
				 */
				/*********************************************************************************/
				String dirname = "./output/";
				String filename = String.format("MIPS.txt");

				/***************************************/
				/* [2] Open MIPS text file for writing */
				/***************************************/
				instance.fileWriter = new PrintWriter(dirname + filename);
			} catch (Exception e) {
				e.printStackTrace();
			}

			/*****************************************************/
			/* [3] Print data section with error message strings */
			/*****************************************************/
			instance.fileWriter.print(".data\n");
			instance.fileWriter.print("string_access_violation: .asciiz \"Access Violation\"\n");
			instance.fileWriter.print("string_illegal_div_by_0: .asciiz \"Illegal Division By Zero\"\n");
			instance.fileWriter.print("string_invalid_ptr_dref: .asciiz \"Invalid Pointer Dereference\"\n");
		}
		return instance;
	}
}
