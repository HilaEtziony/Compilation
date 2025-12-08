import java.io.*;

import java_cup.runtime.Symbol;

import semanticError.SemanticErrorException;
import ast.*;

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
			
			try
			{
				/*************************/
				/* [6] Print the AST ... */
				/*************************/
				ast.printMe();

				AstGraphviz.getInstance().finalizeFile();

				/**************************/
				/* [7] Semant the AST ... */
				/**************************/
				ast.semantMe();

				/*************************************/
				/* [8] Finalize AST GRAPHIZ DOT file */
				/*************************************/
				// AstGraphviz.getInstance().finalizeFile();
			}
			catch (SemanticErrorException e) 
			{
				fileWriter.close();
				fileReader.close();
				throw e;
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				throw e;
			}

			/*************************/
			/* [7] Close output file */
			/*************************/
			fileWriter.print("OK");
			System.out.println("OK");
			fileWriter.close();
			
    	}
			    
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


