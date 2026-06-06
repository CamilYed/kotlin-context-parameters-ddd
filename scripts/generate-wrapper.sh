#!/usr/bin/env bash
set -euo pipefail

GRADLE_VERSION="9.5.1"

echo "Generating Gradle Wrapper ${GRADLE_VERSION}"

if command -v sdk >/dev/null 2>&1; then
  sdk install gradle "${GRADLE_VERSION}" || true
  sdk use gradle "${GRADLE_VERSION}"
fi

if ! command -v gradle >/dev/null 2>&1; then
  echo "Gradle is not installed."
  echo "Install it first, for example:"
  echo "  sdk install gradle ${GRADLE_VERSION}"
  exit 1
fi

gradle wrapper --gradle-version="${GRADLE_VERSION}" --distribution-type=bin
./gradlew --version
