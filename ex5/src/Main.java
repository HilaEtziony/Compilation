import java.io.*;

import java_cup.runtime.Symbol;
import ast.*;
import ir.*;
import mips.*;
import semanticError.SemanticErrorException;

public class Main
{
	static public void main(String argv[])
	{
		Lexer l;
		Parser p;
		Symbol s;
		AstDecList ast;
		FileReader fileReader;
		PrintWriter fileWriter;
		String inputFileName = argv[0];
		String outputFileName = argv[1];

		try
		{
			/********************************/
			/* [1] Initialize a file reader */
			/********************************/
			fileReader = new FileReader(inputFileName);

			/********************************/
			/* [2] Initialize a file writer */
			/********************************/
			fileWriter = new PrintWriter(outputFileName);

			/******************************/
			/* [3] Initialize a new lexer */
			/******************************/
			l = new Lexer(fileReader);

			/*******************************/
			/* [4] Initialize a new parser */
			/*******************************/
			p = new Parser(l);

			/***********************************/
			/* [5] 3 ... 2 ... 1 ... Parse !!! */
			/***********************************/
			ast = (AstDecList) p.parse().value;

			/*************************/
			/* [6] Print the AST ... */
			/*************************/
			ast.printMe();

			// This try and 2 next catches handle error as in ex3 - as necessary for ex5
			try
			{
				/**************************/
				/* [7] Semant the AST ... */
				/**************************/
				ast.semantMe();
			}
			
			catch (SemanticErrorException e) 
			{
				fileWriter.close();
				fileReader.close();
				e.printStackTrace();
				throw e;
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				throw e;
			}

			/**********************/
			/* [8] Ir the AST ... */
			/**********************/
			ast.irMe();

			//from ex4
			/********************************************/
			/* [8.5] Build and print IR control-flow CFG */
			/********************************************/
			Graph cfg = Graph.fromIr(Ir.getInstance());
			System.out.println("CFG blocks (index: command -> successors)");
			for (BasicBlock block : cfg.getBlocks())
			{
				StringBuilder successors = new StringBuilder();
				for (BasicBlock succ : block.getSuccessors())
				{
					if (successors.length() > 0)
					{
						successors.append(", ");
					}
					successors.append(succ.getIndex());
				}
				System.out.printf("Block %d (%s) -> [%s]%n",
					block.getIndex(),
					block.getCommand(),
					successors.toString());
			}

			/***********************/
			/* [9] MIPS the Ir ... */
			/***********************/
			Ir.getInstance().mipsMe();

			/**************************************/
			/* [10] Finalize AST GRAPHIZ DOT file */
			/**************************************/
			AstGraphviz.getInstance().finalizeFile();

			/***************************/
			/* [11] Finalize MIPS file */
			/***************************/
			MipsGenerator.getInstance().finalizeFile();

			/**************************/
			/* [12] Close output file */
			/**************************/
			fileWriter.close();
		}

		// All next catches handle errors as ex3 - ERROR(line number) for syntax or semantic errors, ERROR for lexical error.
		catch (SemanticErrorException e) 
		{
			System.out.println("Semantic Error: " + e.getMessage());
			try
			{
				fileWriter = new PrintWriter(outputFileName);
				String message = e.getMessage();
				fileWriter.print(message);
				System.out.print(message);
				fileWriter.close();
			}
			catch (FileNotFoundException ex)
			{
				ex.printStackTrace();
			}

		}

		catch (Exception e)
		{
			e.printStackTrace();
		}

		catch(Error e)
		{
			try
			{
				fileWriter = new PrintWriter(outputFileName);
				String message = e.getMessage();
				if(message == null)
				{
					fileWriter.print("ERROR");
				}
				else
				{
					fileWriter.print(message);
				}
				fileWriter.close();
			}
			catch (FileNotFoundException ex)
			{
				ex.printStackTrace();
			}

		}
	}
}