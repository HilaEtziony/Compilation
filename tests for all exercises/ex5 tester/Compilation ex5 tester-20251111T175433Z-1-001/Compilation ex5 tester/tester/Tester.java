import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Tester {
	// Use a system property or an environment variable to get the absolute base directory
	private static final String LEADING_PATH = System.getProperty("user.dir");  // Or use a custom env var
	private static final Path BASE_DIR = Paths.get(LEADING_PATH); // Dynamically create path
	private static final Path COMPILER_DIR = Paths.get(LEADING_PATH, "..");
	private static final String COMPILER_JAR = "COMPILER";

	public static void main(String[] args) {
		Path inputDir = BASE_DIR.resolve("inputs");
		Path outputDir = BASE_DIR.resolve("outputs");
		Path expectedDir = BASE_DIR.resolve("expected_outputs");
		Path mipsDir = BASE_DIR.resolve("mips_files");
		System.out.println(inputDir);

		if (!Files.exists(mipsDir)||!Files.exists(inputDir) || !Files.exists(outputDir) || !Files.exists(expectedDir)) {
			System.err.println("Error: One or more required directories are missing.");
			return;
		}

		// List input files
		File[] inputFiles = inputDir.toFile().listFiles((dir, name) -> name.matches("input\\d+\\.txt"));
		if (inputFiles == null || inputFiles.length == 0) {
			System.err.println("Error: No input files found.");
			return;
		}

		int failedTests = 0;
		for (File inputFile : inputFiles) {
			String fileNumber = inputFile.getName().replaceAll("\\D+", "");
			Path mipsFile = mipsDir.resolve("mips" + fileNumber + ".txt");
			Path outputFile = outputDir.resolve("output" + fileNumber + ".txt");
			Path expectedFile = expectedDir.resolve("expected_output" + fileNumber + ".txt");

			if (!Files.exists(expectedFile)) {
				System.err.println("Warning: Expected output file missing for test " + fileNumber);
				continue;
			}

			try {
				ProcessBuilder processBuilder = new ProcessBuilder(
						"java", "-jar", COMPILER_JAR,
						inputFile.getAbsolutePath(), mipsFile.toAbsolutePath().toString()
				);
				processBuilder.directory(COMPILER_DIR.toFile()); // Set working directory
				processBuilder.redirectErrorStream(true);
				Process process = processBuilder.start();
				process.waitFor();
				processBuilder = new ProcessBuilder(
						"spim", "-f", mipsFile.toAbsolutePath().toString()
				);


				processBuilder.directory(COMPILER_DIR.toFile());
				processBuilder.redirectOutput(outputFile.toFile());
				processBuilder.redirectErrorStream(true);
				process = processBuilder.start();
				process.waitFor();

				if (compareFiles(outputFile.toFile(), expectedFile.toFile())) {
					System.out.println("Test " + fileNumber + " PASSED.");
				} else {
					System.out.println("Test " + fileNumber + " FAILED.");
					failedTests++;
				}

			} catch (IOException | InterruptedException e) {
				System.err.println("Error running test " + fileNumber + ": " + e.getMessage());
			}
		}
		System.out.println("Total failed tests: " + failedTests);
	}

	private static boolean compareFiles(File file1, File file2) throws IOException {
		List<String> lines1 = Files.readAllLines(file1.toPath());
		List<String> lines2 = Files.readAllLines(file2.toPath());

		// Trim trailing spaces and empty lines
		trimTrailingEmptyLines(lines1);
		trimTrailingEmptyLines(lines2);

		// Remove trailing spaces on each line
		trimTrailingSpaces(lines1);
		trimTrailingSpaces(lines2);

		return lines1.equals(lines2);
	}

	private static void trimTrailingEmptyLines(List<String> lines) {
		while (!lines.isEmpty() && lines.get(lines.size() - 1).trim().isEmpty()) {
			lines.remove(lines.size() - 1);
		}
	}

	private static void trimTrailingSpaces(List<String> lines) {
		for (int i = 0; i < lines.size(); i++) {
			lines.set(i, lines.get(i).trim());
		}
	}
}
