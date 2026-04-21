#!/usr/bin/env bash
set -u

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
INPUT_DIR="$ROOT_DIR/input"
EXPECTED_DIR="$ROOT_DIR/expected_output"
OUTPUT_DIR="$ROOT_DIR/output"
LOG_DIR="$OUTPUT_DIR/ra_logs"

COMPILER_JAR="$ROOT_DIR/COMPILER"
MIPS_OUT="$OUTPUT_DIR/mips.txt"

mkdir -p "$OUTPUT_DIR" "$LOG_DIR"

cd "$ROOT_DIR" || exit 1

echo "[1/3] Building compiler (make)..."
if ! make >/dev/null; then
  echo "Build failed"
  exit 1
fi

if [[ ! -f "$COMPILER_JAR" ]]; then
  echo "ERROR: COMPILER jar not found at $COMPILER_JAR"
  exit 1
fi

echo "[2/3] Validating register-allocation failure expectations..."
printf "%-35s %-16s %-16s %-8s\n" "TEST" "EXPECTED" "ACTUAL" "RESULT"
printf "%-35s %-16s %-16s %-8s\n" "-----------------------------------" "----------------" "----------------" "--------"

pass_count=0
fail_count=0
skip_count=0

for input_file in "$INPUT_DIR"/TEST_*.txt; do
  test_name="$(basename "$input_file" .txt)"
  expected_file="$EXPECTED_DIR/${test_name}_Expected_Output.txt"
  log_file="$LOG_DIR/${test_name}.log"

  if [[ ! -f "$expected_file" ]]; then
    printf "%-35s %-16s %-16s %-8s\n" "$test_name" "N/A" "N/A" "SKIP"
    ((skip_count++))
    continue
  fi

  expected_last_line="$(awk 'NF { line=$0 } END { gsub(/\r/, "", line); print line }' "$expected_file")"
  expected_ra_fail=0
  if [[ "$expected_last_line" == "Register Allocation Failed" ]]; then
    expected_ra_fail=1
  fi
  expected_outcome="OK"
  if [[ $expected_ra_fail -eq 1 ]]; then
    expected_outcome="REG_ALLOC_FAIL"
  fi

  java -jar "$COMPILER_JAR" "$input_file" "$MIPS_OUT" >"$log_file" 2>&1
  compiler_rc=$?

  actual_ra_fail=0
  if grep -q "Register Allocation Failed" "$log_file"; then
    actual_ra_fail=1
  fi
  actual_outcome="OK"
  if [[ $actual_ra_fail -eq 1 ]]; then
    actual_outcome="REG_ALLOC_FAIL"
  fi

  if [[ $expected_ra_fail -eq $actual_ra_fail ]]; then
    printf "%-35s %-16s %-16s %-8s\n" "$test_name" "$expected_outcome" "$actual_outcome" "PASS"
    ((pass_count++))
  else
    printf "%-35s %-16s %-16s %-8s\n" "$test_name" "$expected_outcome" "$actual_outcome" "FAIL"
    ((fail_count++))
  fi

done

echo "[3/3] Summary"
echo "  PASS: $pass_count"
echo "  FAIL: $fail_count"
echo "  SKIP: $skip_count"
echo "  Logs: $LOG_DIR"

if (( fail_count > 0 )); then
  exit 1
fi

exit 0
