/***********/
/* PACKAGE */
/***********/
package mips;

/*******************/
/* GENERAL IMPORTS */
/*******************/
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import ir.IrCommand;
/*******************/
/* PROJECT IMPORTS */
/*******************/
import temp.*;

public class MipsGenerator 
{
	private static final int WORD_SIZE = 4;
	/***********************/
	/* The file writer ... */
	/***********************/
	private PrintWriter fileWriter;

	/***********************/
	/* Create Segments... */
	/***********************/
	private StringBuilder dataSection = new StringBuilder(".data\n");
    private StringBuilder textSection = new StringBuilder(".text\n");

	/***********************/
	/* The file writer ... */
	/***********************/
	public void finalizeFile() {
		fileWriter.print(dataSection.toString());
		fileWriter.print("\n.text\n");
		fileWriter.print(".globl main\n"); 
		fileWriter.print(textSection.toString());

		fileWriter.print("\tli $v0, 10\n");
		fileWriter.print("\tsyscall\n");
		fileWriter.close();
	}

	/**************************************/
    /* Library Function & System Calls    */
    /**************************************/
	public void printInt(Temp t) {
        String reg = codegen.RegisterAllocator.getRegister(t);
        textSection.append(String.format("\tmove $a0, %s\n", reg));
        textSection.append(String.format("\tli $v0, 1\n"));
        textSection.append(String.format("\tsyscall\n"));
        
        // Print space after int
        textSection.append(String.format("\tli $a0, 32\n"));
        textSection.append(String.format("\tli $v0, 11\n"));
        textSection.append(String.format("\tsyscall\n"));
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

	public void printString(Temp t) {
		String reg = codegen.RegisterAllocator.getRegister(t);
		textSection.append(String.format("\tmove $a0, %s\n", reg));
		textSection.append(String.format("\tli $v0, 4\n"));
		textSection.append(String.format("\tsyscall\n"));
	}

    public void malloc(Temp dst, Temp size) {
        textSection.append(String.format("\tmove $a0, %s\n", codegen.RegisterAllocator.getRegister(size)));
        textSection.append(String.format("\tli $v0, 9\n"));
        textSection.append(String.format("\tsyscall\n"));
        textSection.append(String.format("\tmove %s, $v0\n", codegen.RegisterAllocator.getRegister(dst)));
    }

	public void exit() {
		textSection.append(String.format("\tli $v0, 10\n"));
		textSection.append(String.format("\tsyscall\n"));
	}

	public void addStringLiteral(String label, String value) {
		dataSection.append(String.format("%s: .asciiz %s\n", label, value));
	}

	public void la(Temp dst, String label) {
		String d = codegen.RegisterAllocator.getRegister(dst);
    	textSection.append(String.format("\tla %s, %s\n", d, label));
	}

	public void allocate(String varName) {
		dataSection.append(String.format("\tglobal_%s: .word 721\n", varName));
	}

	public void load(Temp dst, String varName, int offset, boolean isGlobal) {
		String d = codegen.RegisterAllocator.getRegister(dst);
		if (isGlobal) {
			textSection.append(String.format("\tlw %s, global_%s\n", d, varName));
		} else {
			textSection.append(String.format("\tlw %s, %d($fp)\n", d, offset));
		}
	}

	public void store(String varName, Temp src, int offset, boolean isGlobal) {
		String s = codegen.RegisterAllocator.getRegister(src);
		if (isGlobal) {
			textSection.append(String.format("\tsw %s, global_%s\n", s, varName));
		} else {
			textSection.append(String.format("\tsw %s, %d($fp)\n", s, offset));
		}
	}

	public void li(Temp t, int value) {
		textSection.append(String.format("\tli %s, %d\n", codegen.RegisterAllocator.getRegister(t), value));
	}

	private void applySaturation(String regName) {
		String labelLower = IrCommand.getFreshLabel("label_lower");
		String labelEnd   = IrCommand.getFreshLabel("label_end");

		// Check upper bound (2^15 - 1) 
		textSection.append(String.format("\tli $s0, 32767\n")); 
		textSection.append(String.format("\tble %s, $s0, %s\n", regName, labelLower));
		
		// Clamp to max value 
		textSection.append(String.format("\tmove %s, $s0\n", regName));
		textSection.append(String.format("\tj %s\n", labelEnd));

		textSection.append(String.format("%s:\n", labelLower));
		// Check lower bound (-2^15) 
		textSection.append(String.format("\tli $s0, -32768\n"));
		textSection.append(String.format("\tbge %s, $s0, %s\n", regName, labelEnd));
		
		// Clamp to min value
		textSection.append(String.format("\tmove %s, $s0\n", regName));

		textSection.append(String.format("%s:\n", labelEnd));
	}

	public void add(Temp dst, Temp oprnd1, Temp oprnd2) {
		String d = codegen.RegisterAllocator.getRegister(dst);
		String s1 = codegen.RegisterAllocator.getRegister(oprnd1);
		String s2 = codegen.RegisterAllocator.getRegister(oprnd2);

		textSection.append(String.format("\tadd %s, %s, %s\n", d, s1, s2));
		applySaturation(d);
	}

	public void sub(Temp dst, Temp oprnd1, Temp oprnd2) {
		String d = codegen.RegisterAllocator.getRegister(dst);
		String s1 = codegen.RegisterAllocator.getRegister(oprnd1);
		String s2 = codegen.RegisterAllocator.getRegister(oprnd2);

		textSection.append(String.format("\tsub %s, %s, %s\n", d, s1, s2));
		applySaturation(d);
	}

	public void mul(Temp dst, Temp oprnd1, Temp oprnd2) {
		String d = codegen.RegisterAllocator.getRegister(dst);
		String s1 = codegen.RegisterAllocator.getRegister(oprnd1);
		String s2 = codegen.RegisterAllocator.getRegister(oprnd2);

		textSection.append(String.format("\tmul %s, %s, %s\n", d, s1, s2));
		applySaturation(d);
	}

	/*******************************/
	/* Division with zero check    */
	/*******************************/
	public void div(Temp dst, Temp t1, Temp t2) {
		String d  = codegen.RegisterAllocator.getRegister(dst);
		String s1 = codegen.RegisterAllocator.getRegister(t1);
		String s2 = codegen.RegisterAllocator.getRegister(t2);

		textSection.append(String.format("\tbeq %s, $zero, illegal_div_by_0_handler\n", s2));
		textSection.append(String.format("\tdiv %s, %s\n", s1, s2));
		textSection.append(String.format("\tmflo %s\n", d));
		applySaturation(d);
	}

	public void label(String inlabel) {
		textSection.append(String.format("%s:\n", inlabel));
	}

	public void jump(String inlabel) {
		textSection.append(String.format("\tj %s\n", inlabel));
	}

	public void blt(Temp oprnd1, Temp oprnd2, String label) {
		String s1 = codegen.RegisterAllocator.getRegister(oprnd1);
    	String s2 = codegen.RegisterAllocator.getRegister(oprnd2);
    	textSection.append(String.format("\tblt %s, %s, %s\n", s1, s2, label));
	}

	public void bge(Temp oprnd1, Temp oprnd2, String label) {
		String s1 = codegen.RegisterAllocator.getRegister(oprnd1);
		String s2 = codegen.RegisterAllocator.getRegister(oprnd2);
		textSection.append(String.format("\tbge %s, %s, %s\n", s1, s2, label));	
	}

	public void bne(Temp oprnd1, Temp oprnd2, String label) {
		String s1 = codegen.RegisterAllocator.getRegister(oprnd1);
    	String s2 = codegen.RegisterAllocator.getRegister(oprnd2);
    	textSection.append(String.format("\tbne %s, %s, %s\n", s1, s2, label));
	}

	public void beq(Temp oprnd1, Temp oprnd2, String label) {
		String s1 = codegen.RegisterAllocator.getRegister(oprnd1);
    	String s2 = codegen.RegisterAllocator.getRegister(oprnd2);
    	textSection.append(String.format("\tbeq %s, %s, %s\n", s1, s2, label));
	}

	public void beqz(Temp oprnd1, String label) {
		String s1 = codegen.RegisterAllocator.getRegister(oprnd1);
    	textSection.append(String.format("\tbeq %s, $zero, %s\n", s1, label));
	}

	/*******************************/
	/* Array operations */
	/*******************************/
	public void newArray(Temp dst, Temp size) {
		String d = codegen.RegisterAllocator.getRegister(dst);
		String s = codegen.RegisterAllocator.getRegister(size);

		// Calculate bytes needed: (size + 1) * 4
		textSection.append(String.format("\taddi $s0, %s, 1\n", s));
		textSection.append(String.format("\tsll $s0, $s0, 2\n"));
		textSection.append(String.format("\tmove $a0, $s0\n"));
		textSection.append(String.format("\tli $v0, 9\n"));
		textSection.append(String.format("\tsyscall\n"));
		textSection.append(String.format("\tsw %s, 0($v0)\n", s));
		textSection.append(String.format("\tmove %s, $v0\n", d));
	}

	// Runtime checks: null ptr -> invalid_ptr_dref_handler, bounds -> access_violation_handler
	public void arrayLoad(Temp dst, Temp base, Temp index) {
		String d = codegen.RegisterAllocator.getRegister(dst);
		String b = codegen.RegisterAllocator.getRegister(base);
		String i = codegen.RegisterAllocator.getRegister(index);

		// 1. Null pointer check (section 2.5: Invalid Pointer Dereference)
		textSection.append(String.format("\tbeq %s, $zero, invalid_ptr_dref_handler\n", b));
		// 2. Load array length from offset 0
		textSection.append(String.format("\tlw $s0, 0(%s)\n", b));
		// 3. Bounds check: index < 0 (section 2.5: Access Violation)
		textSection.append(String.format("\tbltz %s, access_violation_handler\n", i));
		// 4. Bounds check: index >= length
		textSection.append(String.format("\tbge %s, $s0, access_violation_handler\n", i));
		// 5. Calculate address: base + (index + 1) * 4
		textSection.append(String.format("\taddi $s0, %s, 1\n", i));
		textSection.append(String.format("\tsll $s0, $s0, 2\n"));
		textSection.append(String.format("\tadd $s0, %s, $s0\n", b));
		textSection.append(String.format("\tlw %s, 0($s0)\n", d));
	}

	public void arrayStore(Temp base, Temp index, Temp src) {
		String regBase  = codegen.RegisterAllocator.getRegister(base);
        String regIndex = codegen.RegisterAllocator.getRegister(index);
        String regSrc   = codegen.RegisterAllocator.getRegister(src);

		// 1. Null pointer check (section 2.5: Invalid Pointer Dereference)
		textSection.append(String.format("\tbeq %s, $zero, invalid_ptr_dref_handler\n", regBase));
		// 2. Load array length from offset 0
		textSection.append(String.format("\tlw $s0, 0(%s)\n", regBase));
		// 3. Bounds check: index < 0 (section 2.5: Access Violation)
		textSection.append(String.format("\tbltz %s, access_violation_handler\n", regIndex));
		// 4. Bounds check: index >= length
		textSection.append(String.format("\tbge %s, $s0, access_violation_handler\n", regIndex));
		// 5. Calculate address and store
		textSection.append(String.format("\taddi $s0, %s, 1\n", regIndex));
        textSection.append("\tsll $s0, $s0, 2\n");
        textSection.append(String.format("\tadd $s0, %s, $s0\n", regBase));
        textSection.append(String.format("\tsw %s, 0($s0)\n", regSrc));
	}

	/*******************************/
	/* Class operations */
	/*******************************/
	public void newClass(Temp dst, int sizeInBytes) {
		String regDst = codegen.RegisterAllocator.getRegister(dst);

		textSection.append(String.format("\tli $a0, %d\n", sizeInBytes));
		textSection.append("\tli $v0, 9\n");
		textSection.append("\tsyscall\n");
		textSection.append(String.format("\tmove %s, $v0\n", regDst));
	}

	public void storeVTable(Temp obj, String vtableName) {
		String regObj = codegen.RegisterAllocator.getRegister(obj);

		textSection.append(String.format("\tla $s0, %s\n", vtableName));
		textSection.append(String.format("\tsw $s0, 0(%s)\n", regObj));
	}

	public void defineVTable(String className, types.TypeList methods) {
		dataSection.append(String.format("VTable_%s:\n", className));

		types.TypeList it = methods;
		while (it != null) {
			if (it.head instanceof types.TypeFunction) {
				types.TypeFunction func = (types.TypeFunction) it.head;
				dataSection.append(String.format("\t.word %s_%s\n", func.className, func.name));
			}

			it = it.tail;
		}
	}

	public void fieldLoad(Temp dst, Temp base, int offset) {
		String regDst = codegen.RegisterAllocator.getRegister(dst);
    	String regBase = codegen.RegisterAllocator.getRegister(base);

		// Null pointer check (section 2.5: Invalid Pointer Dereference)
		textSection.append(String.format("\tbeq %s, $zero, invalid_ptr_dref_handler\n", regBase));
		textSection.append(String.format("\tlw %s, %d(%s)\n", regDst, offset, regBase));
	}

	public void fieldStore(Temp base, int offset, Temp src) {
		String regBase = codegen.RegisterAllocator.getRegister(base);
		String regSrc = codegen.RegisterAllocator.getRegister(src);

		// Null pointer check (section 2.5: Invalid Pointer Dereference)
		textSection.append(String.format("\tbeq %s, $zero, invalid_ptr_dref_handler\n", regBase));
    	textSection.append(String.format("\tsw %s, %d(%s)\n", regSrc, offset, regBase));
	}

	public void virtualCall(Temp dst, Temp obj, int methodOffset, TempList args) {
		String objReg = codegen.RegisterAllocator.getRegister(obj);
		textSection.append(String.format("\tbeq %s, $zero, invalid_ptr_dref_handler\n", objReg));

		// Push args
		TempList it = args;
		int argCount = 0;
		while (it != null) {
			String argReg = codegen.RegisterAllocator.getRegister(it.head);
			textSection.append("\tsubu $sp, $sp, 4\n");
			textSection.append(String.format("\tsw %s, 0($sp)\n", argReg));
			it = it.tail;
			argCount++;
		}

		textSection.append(String.format("\tlw $s0, 0(%s)\n", objReg));
		textSection.append(String.format("\tlw $s0, %d($s0)\n", methodOffset));
		textSection.append("\tjalr $s0\n");

		if (argCount > 0) {
			textSection.append(String.format("\taddu $sp, $sp, %d\n", argCount * 4));
		}

		if (dst != null) {
			String d = codegen.RegisterAllocator.getRegister(dst);
			textSection.append(String.format("\tmove %s, $v0\n", d));
		}
	}

	public void prologue(int frameSize) {
		// 1. Save old return address and old frame pointer (8 bytes total)
		textSection.append("\tsubu $sp, $sp, 8\n");
		textSection.append("\tsw $ra, 4($sp)\n");
		textSection.append("\tsw $fp, 0($sp)\n");
		
		// 2. Set the new frame pointer to the current stack pointer
		textSection.append("\tmove $fp, $sp\n");
		
		// 3. Allocate space for local variables on the stack
		if (frameSize > 0) {
			textSection.append(String.format("\tsubu $sp, $sp, %d\n", frameSize));
		}
	}

	public void epilogue() {
		// 1. Restore stack pointer to frame pointer (deallocates local variables)
		textSection.append("\tmove $sp, $fp\n");
		
		// 2. Restore the old frame pointer and return address
		textSection.append("\tlw $fp, 0($sp)\n");
		textSection.append("\tlw $ra, 4($sp)\n");
		
		// 3. Pop the saved fp/ra from stack
		textSection.append("\taddu $sp, $sp, 8\n");
		
		// 4. Return to caller
		textSection.append("\tjr $ra\n");
	}

	public void functionCall(Temp res, String funcName, TempList args) {
		// 1. Push arguments to stack in reverse order
		int argCount = 0;
		List<Temp> tempArgs = new ArrayList<>();
		for (TempList tl = args; tl != null; tl = tl.tail) { 
			tempArgs.add(tl.head); 
		}
		
		// Iterate backwards to push the first argument last (so it's at the top of $sp)
		for (int i = tempArgs.size() - 1; i >= 0; i--) {
			String reg = codegen.RegisterAllocator.getRegister(tempArgs.get(i));
			textSection.append("\tsubu $sp, $sp, 4\n");
			textSection.append(String.format("\tsw %s, 0($sp)\n", reg));
			argCount++;
		}

		// 2. Execute Jump and Link
		textSection.append(String.format("\tjal %s\n", funcName));

		// 3. Clean up arguments from stack (caller responsibility)
		if (argCount > 0) {
			textSection.append(String.format("\taddu $sp, $sp, %d\n", argCount * 4));
		}

		// 4. Move return value from $v0 to the destination temporary
		if (res != null) {
			String dstReg = codegen.RegisterAllocator.getRegister(res);
			textSection.append(String.format("\tmove %s, $v0\n", dstReg));
		}
	}

	public void returnCommand(Temp res) {
		if (res != null) {
			String reg = codegen.RegisterAllocator.getRegister(res);
			textSection.append(String.format("\tmove $v0, %s\n", reg));
		}
	}

	/**************************************/
    /* String Concatenation 			  */
    /**************************************/
	public void stringConcat(Temp dst, Temp t1, Temp t2) {
        String regDst = codegen.RegisterAllocator.getRegister(dst);
        String regT1  = codegen.RegisterAllocator.getRegister(t1);
        String regT2  = codegen.RegisterAllocator.getRegister(t2);
        String label  = IrCommand.getFreshLabel("str_concat");

        // 1. Calculate length of t1 (result in $a0)
        textSection.append(String.format("\tmove $a0, $zero\n"));
        textSection.append(String.format("len1_%s:\n", label));
        textSection.append(String.format("\tadd $s0, %s, $a0\n", regT1));
        textSection.append(String.format("\tlb $s1, 0($s0)\n"));
        textSection.append(String.format("\tbeqz $s1, len2_start_%s\n", label));
        textSection.append(String.format("\taddi $a0, $a0, 1\n"));
        textSection.append(String.format("\tj len1_%s\n", label));

        // 2. Add length of t2
        textSection.append(String.format("len2_start_%s:\n", label));
        textSection.append(String.format("\tmove $t0, $zero\n"));
        textSection.append(String.format("len2_%s:\n", label));
        textSection.append(String.format("\tadd $s0, %s, $t0\n", regT2));
        textSection.append(String.format("\tlb $s1, 0($s0)\n"));
        textSection.append(String.format("\tbeqz $s1, alloc_%s\n", label));
        textSection.append(String.format("\taddi $t0, $t0, 1\n"));
        textSection.append(String.format("\tj len2_%s\n", label));

        // 3. Malloc (len1 + len2 + 1)
        textSection.append(String.format("alloc_%s:\n", label));
        textSection.append(String.format("\tadd $a0, $a0, $t0\n")); // a0 = len1 + len2
        textSection.append(String.format("\taddi $a0, $a0, 1\n"));  // null terminator
        textSection.append(String.format("\tli $v0, 9\n"));         // sbrk
        textSection.append(String.format("\tsyscall\n"));
        textSection.append(String.format("\tmove %s, $v0\n", regDst));

        // 4. Copy t1 to dst
        // $s0 will be our current offset in the destination string
        textSection.append(String.format("\tmove $s0, $zero\n"));
        copyString("copy1_" + label, regT1, regDst);

        // 5. Copy t2 to dst (starting from t1's null terminator)
        // We subtract 1 from $s0 because copy1 stopped at the null terminator, 
        // and we want t2 to overwrite that null terminator.
        textSection.append(String.format("\taddi $s0, $s0, -1\n"));
        copyString("copy2_" + label, regT2, regDst);
    }

	public void lb(String regDst, String regBase, int offset) {
		textSection.append(String.format("\tlb %s, %d(%s)\n", regDst, offset, regBase));
	}

	public void sb(String regSrc, String regBase, int offset) {
		textSection.append(String.format("\tsb %s, %d(%s)\n", regSrc, offset, regBase));
	}

	private void copyString(String label, String regSrc, String regDst) {
        textSection.append(String.format("\tmove $s1, $zero\n")); 
        textSection.append(String.format("%s:\n", label));
        
        textSection.append(String.format("\tadd $t0, %s, $s1\n", regSrc));
        textSection.append(String.format("\tlb $t1, 0($t0)\n"));
        
        textSection.append(String.format("\tadd $t2, %s, $s0\n", regDst)); 
        textSection.append(String.format("\tsb $t1, 0($t2)\n"));
        
        textSection.append(String.format("\tbeqz $t1, end_%s\n", label));
        textSection.append(String.format("\taddi $s1, $s1, 1\n"));
        textSection.append(String.format("\taddi $s0, $s0, 1\n"));
        textSection.append(String.format("\tj %s\n", label));
        textSection.append(String.format("end_%s:\n", label));
    }

    /**************************************/
    /* String Equality				      */
    /**************************************/
    public void stringEq(Temp dst, Temp t1, Temp t2) {
		String d = codegen.RegisterAllocator.getRegister(dst);
		String s1 = codegen.RegisterAllocator.getRegister(t1);
		String s2 = codegen.RegisterAllocator.getRegister(t2);
		String label = IrCommand.getFreshLabel("str_eq");

        textSection.append(String.format("\tmove $s0,$zero\n"));
        textSection.append(String.format("loop_%s:\n", label));
        textSection.append(String.format("\tadd $t0,Temp_%d,$s0\n", s1));
        textSection.append(String.format("\tadd $t1,Temp_%d,$s0\n", s2));
        textSection.append(String.format("\tlb $t2,0($t0)\n"));
        textSection.append(String.format("\tlb $t3,0($t1)\n"));
        textSection.append(String.format("\tbne $t2,$t3,not_equal_%s\n", label));
        textSection.append(String.format("\tbeqz $t2,equal_%s\n", label));
        textSection.append(String.format("\taddi $s0,$s0,1\n"));
        textSection.append(String.format("\tj loop_%s\n", label));
        
        textSection.append(String.format("equal_%s:\n", label));
        textSection.append(String.format("\tli Temp_%d,1\n", d));
        textSection.append(String.format("\tj end_%s\n", label));
        
        textSection.append(String.format("not_equal_%s:\n", label));
        textSection.append(String.format("\tli Temp_%d,0\n", d));
        
        textSection.append(String.format("end_%s:\n", label));
    }
	
	/***************************************/
	/* Runtime error handlers (section 2.5) */
	/***************************************/
	public void emitErrorHandlers() {
		textSection.append("access_violation_handler:\n");
		textSection.append("\tla $a0,string_access_violation\n");
		textSection.append("\tli $v0,4\n");
		textSection.append("\tsyscall\n");
		textSection.append("\tli $v0,10\n");
		textSection.append("\tsyscall\n");

		textSection.append("invalid_ptr_dref_handler:\n");
		textSection.append("\tla $a0,string_invalid_ptr_dref\n");
		textSection.append("\tli $v0,4\n");
		textSection.append("\tsyscall\n");
		textSection.append("\tli $v0,10\n");
		textSection.append("\tsyscall\n");

		textSection.append("illegal_div_by_0_handler:\n");
		textSection.append("\tla $a0,string_illegal_div_by_0\n");
		textSection.append("\tli $v0,4\n");
		textSection.append("\tsyscall\n");
		textSection.append("\tli $v0,10\n");
		textSection.append("\tsyscall\n");
	}

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
			instance.dataSection.append(".data\n");
			instance.dataSection.append("string_access_violation: .asciiz \"Access Violation\"\n");
			instance.dataSection.append("string_illegal_div_by_0: .asciiz \"Illegal Division By Zero\"\n");
			instance.dataSection.append("string_invalid_ptr_dref: .asciiz \"Invalid Pointer Dereference\"\n");
		}
		return instance;
	}
}
