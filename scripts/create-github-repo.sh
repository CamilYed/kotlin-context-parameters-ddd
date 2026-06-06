#!/usr/bin/env bash
set -euo pipefail

repo="CamilYed/kotlin-context-parameters-ddd"

if ! command -v gradle >/dev/null 2>&1; then
  echo "Gradle is required to generate the wrapper. Install Gradle 9.5.1 first." >&2
  exit 1
fi

gradle wrapper --gradle-version 9.5.1
./gradlew test

git init
git add .
git commit -m "Initial context parameters example"
git branch -M main

gh repo create "$repo" \
  --public \
  --description "Kotlin context parameters in a small DDD / hexagonal architecture example" \
  --source=. \
  --remote=origin \
  --push
