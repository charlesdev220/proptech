#!/usr/bin/env bash
set -euo pipefail

BASE_URL=${BASE_URL:-http://localhost:8080}
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

FAILED=0

run_script() {
  local name=$1
  local script=$2
  echo ""
  echo "▶ Running: $name"
  if k6 run -e BASE_URL="$BASE_URL" "$SCRIPT_DIR/$script"; then
    echo "✅ $name PASSED"
  else
    echo "❌ $name FAILED"
    FAILED=1
  fi
}

run_script "Login Smoke Test"       "smoke-login.js"
run_script "Properties Smoke Test"  "smoke-properties.js"
run_script "Trust Score Smoke Test" "smoke-trust-score.js"

echo ""
if [ $FAILED -eq 0 ]; then
  echo "✅ All smoke tests passed."
  exit 0
else
  echo "❌ One or more smoke tests failed. Review thresholds."
  exit 1
fi
