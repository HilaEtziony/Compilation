import glob
import subprocess

INPUT_DIR = "./input"
EXPECTED_OUTPUT_DIR = "./expected_output"
OUTPUT_DIR = "./output"

expected_output_files = [
    f.replace(".txt", "") for f in glob.glob("*.txt", root_dir="./expected_output")
]
input_files = [f.replace(".txt", "") for f in glob.glob("*.txt", root_dir="./input")]

files = [
    [f, f if f in expected_output_files else f"{f}_Expected_Output"]
    for f in sorted(input_files)
    if f in expected_output_files or f"{f}_Expected_Output" in expected_output_files
]


for index, [input_file, expected_file] in enumerate(files):
    print(f'{index+1}/{len(files)} Testing "{input_file}":')
    subprocess.run(
        [
            "java",
            "-jar",
            "./LEXER",
            f"{INPUT_DIR}/{input_file}.txt",
            f"{OUTPUT_DIR}/{input_file}.txt",
        ]
    )
    with open(f"{EXPECTED_OUTPUT_DIR}/{expected_file}.txt") as expected:
        with open(f"{OUTPUT_DIR}/{input_file}.txt") as actual:
            if expected.read() == actual.read():
                print("    Passed")
            else:
                print("    Failed")
