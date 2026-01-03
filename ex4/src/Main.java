import java.io.*;
import java.io.PrintWriter;
import java_cup.runtime.Symbol;
import ast.*;
import ir.BasicBlock;
import ir.Graph;
import ir.Ir;
import java.util.ArrayList;

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
			// ast.printMe();

			/**************************/
			/* [7] Semant the AST ... */
			/**************************/
			ast.semantMe();

			/**********************/
			/* [8] IR the AST ... */
			/**********************/
			ast.irMe();

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
			// Create and run the use-before-def checker
			UseBeforeDefCheck ubdc = new UseBeforeDefCheck(cfg);
			ArrayList<String> errors = ubdc.useBeforeDef();
			StringBuilder sb = new StringBuilder();
			if (errors.isEmpty()){
				System.out.println("!OK");
				fileWriter.print("!OK");
			}
			else{
				for (String error: errors){
					System.out.println("Use before definition error: " + error);
					sb.append(error + "\n");
				}
				sb.deleteCharAt(sb.length() - 1);
				fileWriter.print(sb.toString());
			}


			/**************************/
			/* [9] Close output file */
			/**************************/
			fileWriter.close();

			/*************************************/
			/* [10] Finalize AST GRAPHIZ DOT file */
			/*************************************/
			// AstGraphviz.getInstance().finalizeFile();
		}

		catch (Exception e)
		{
			try {
				fileWriter = new PrintWriter(outputFileName);
				fileWriter.println(e.getMessage());
				e.printStackTrace(fileWriter);
				fileWriter.close();
			} catch (Exception e2) {
			}
			e.printStackTrace();
		}
	}
}


