import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Tester {
	public static void main(String[] args) {
		int counter = 0;
		// Define paths relative to the current directory
		String testerDir = "../tester";
		String inputsDir = testerDir + "/inputs";
		String outputsDir = testerDir + "/output";
		String expectedOutputsDir = testerDir + "/expected_outputs"; // Add path for expected outputs
		String compilerJar = "../COMPILER"; // Navigate up from 'tester' to 'ex3' directory

		// Ensure the output directory exists
		File outputDir = new File(outputsDir);
		if (!outputDir.exists()) {
			outputDir.mkdirs();
		}

		// Get all input files
		File inputDir = new File(inputsDir);
		File[] inputFiles = inputDir.listFiles((dir, name) -> name.endsWith(".txt"));

		if (inputFiles == null || inputFiles.length == 0) {
			System.out.println("No input files found in " + inputsDir);
			return;
		}

		// Get all expected output files
		File expectedOutputDir = new File(expectedOutputsDir);
		File[] expectedOutputFiles = expectedOutputDir.listFiles((dir, name) -> name.startsWith("expected_output") && name.endsWith(".txt"));

		if (expectedOutputFiles == null || expectedOutputFiles.length == 0) {
			System.out.println("No expected output files found in " + expectedOutputsDir);
			return;
		}

		// Process each input file
		for (File inputFile : inputFiles) {
			try {
				// Generate corresponding output file name
				String inputFileName = inputFile.getName();
				String outputFileName = inputFileName.replace("input", "output");
				File outputFile = new File(outputsDir + "/" + outputFileName);

				// Build the command
				String command = String.format("java -jar %s %s %s",
						compilerJar,
						inputFile.getCanonicalPath(),
						outputFile.getCanonicalPath());

				// Run the command
				System.out.println("Running: " + command);
				Process process = Runtime.getRuntime().exec(command);

				// Consume stdout and stderr to prevent blocking
				consumeStream(process.getInputStream(), "OUTPUT");
				consumeStream(process.getErrorStream(), "ERROR");

				// Wait for the process to finish
				int exitCode = process.waitFor();
				if (exitCode == 0) {
					System.out.println("Processed: " + inputFileName + " -> " + outputFileName);

					// Compare the generated output with the expected output
					String expectedFileName = "expected_output" + inputFileName.replace("input", "").replace(".txt", "") + ".txt";
					File expectedFile = new File(expectedOutputsDir + "/" + expectedFileName);

					if (!expectedFile.exists()) {
						System.err.println("Expected output file " + expectedFileName + " not found.");
						continue;
					}

					// Read the contents of both output and expected files
					String outputFileContent = new String(Files.readAllBytes(Paths.get(outputFile.getCanonicalPath()))).trim();
					String expectedFileContent = new String(Files.readAllBytes(Paths.get(expectedFile.getCanonicalPath()))).trim();

					// Compare the contents, ignoring trailing newlines
					if (!outputFileContent.equals(expectedFileContent)) {
						counter++;
						System.err.println("Error: Output for test " + inputFileName + " doesn't match expected output.");
					} else {
						System.out.println("Test " + inputFileName + " passed.");
					}
				} else {
					System.err.println("Error processing " + inputFileName + ". Exit code: " + exitCode);
				}
			} catch (IOException | InterruptedException e) {
				System.err.println("An error occurred while processing " + inputFile.getName());
				e.printStackTrace();
			}
		}
		System.out.println("Total numbers of tests not passed: "+counter);
	}


	private static void consumeStream(InputStream stream, String streamName) {
		new Thread(() -> {
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
				String line;
				while ((line = reader.readLine()) != null) {

				}
			} catch (IOException e) {

			}
		}).start();
	}
}
