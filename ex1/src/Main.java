import java.io.*;
import java.io.PrintWriter;
import java.util.*;

import java_cup.runtime.Symbol;
   
public class Main
{
	private static final String[] TOKEN_NAMES = {
	"EOF","LPAREN","RPAREN","LBRACK","RBRACK","LBRACE","RBRACE",
	"PLUS","MINUS","TIMES","DIVIDE","COMMA","DOT","SEMICOLON",
	"TYPE_INT","TYPE_STRING","TYPE_VOID","ASSIGN","EQ","LT","GT",
	"ARRAY","CLASS","RETURN","WHILE","IF","ELSE","NEW","EXTENDS",
	"NIL","INT","STRING","ID", "INT_W_LEADING_Z", "COMMENT", "ERROR"
	};

	static public void main(String argv[])
	{
		Lexer l;
		Symbol s;
		FileReader fileReader;
		PrintWriter fileWriter;
		String inputFileName = argv[0];
		String outputFileName = argv[1];
		
		// working with queues - writing to file only after all tokens are read
		Queue<String> tokenQueue = new LinkedList<>();
		String line;

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

			/***********************/
			/* [4] Read next token */
			/***********************/
			s = l.next_token();

			/********************************/
			/* [5] Main reading tokens loop */
			/********************************/
			while (s.sym != TokenNames.EOF)
			{
				if(s.sym == TokenNames.ERROR){
					fileWriter.print("ERROR");
					fileWriter.close();
					throw new RuntimeException("Lexical Error at line " + l.getLine() + ", position " + l.getTokenStartPosition());
				}
				else if (s.sym == TokenNames.COMMENT){
					s = l.next_token();
					continue;
				}
				/************************/
				/* [6] Print to console */
				/************************/
				System.out.print(TOKEN_NAMES[s.sym]);
				line = TOKEN_NAMES[s.sym];
				if(s.value != null) {
				    System.out.print("(" + s.value + ")");
				    line += "(" + s.value + ")";
				}
				System.out.print("[" + l.getLine() + "," + l.getTokenStartPosition() + "]\n");
				line += "[" + l.getLine() + "," + l.getTokenStartPosition() + "]";
				tokenQueue.add(line);
				/***********************/
				/* [8] Read next token */
				/***********************/
				s = l.next_token();
			}

			while((line=tokenQueue.poll()) != null){
				/*********************/
				/* [7] Print to file */
				/*********************/
				fileWriter.print(line);
				if(tokenQueue.size() > 0){
					fileWriter.print("\n");
				}
			}
			
			/******************************/
			/* [9] Close lexer input file */
			/******************************/
			l.yyclose();

			/**************************/
			/* [10] Close output file */
			/**************************/
			fileWriter.close();
    	}
			     
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}


