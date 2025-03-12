#!/bin/bash

SCRIPT_NAME=$(basename "$0")

function show_usage {
  echo "Usage:"
  echo "  $SCRIPT_NAME [options]"
  echo
  echo "Options:"
  echo "  -h, --help                Show this help message"
  echo "  -b, --branch <branch>     Specify base branch (default: main)"
  echo
}

BRANCH="main"

while [[ $# -gt 0 ]]; do
  case $1 in
    -h|--help)
      show_usage
      exit 0
      ;;
    -b|--branch)
      BRANCH="$2"
      shift 2
      ;;
    *)
      echo "Warning: Unknown option: $1"
      shift
      ;;
  esac
done

if ! git rev-parse --is-inside-work-tree > /dev/null 2>&1; then
  echo "Error: Not a git repository. Please run this script from a git repository."
  exit 1
fi

if ! git show-ref --verify --quiet refs/heads/$BRANCH && ! git show-ref --verify --quiet refs/remotes/origin/$BRANCH; then
  echo "Error: Branch '$BRANCH' does not exist locally or remotely."
  exit 1
fi

echo "Fetching latest updates from remote..."
git fetch origin > /dev/null 2>&1

CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD)
echo "Current branch: $CURRENT_BRANCH"

COMMIT_RANGE="origin/${BRANCH}..HEAD"
echo "Generating changelog from $BRANCH to HEAD"

COMMIT_COUNT=$(git rev-list --count $COMMIT_RANGE)
echo "Found $COMMIT_COUNT commits"

echo "----------------------------------------"
echo "CHANGELOG: $BRANCH → $CURRENT_BRANCH"
echo "----------------------------------------"

COMMITS=$(git rev-list --reverse $COMMIT_RANGE)

for COMMIT in $COMMITS; do
  SUBJECT=$(git log -1 --pretty=format:"%s" $COMMIT)

  BODY=$(git log -1 --pretty=format:"%b" $COMMIT | sed '/^$/d')

  echo "* $SUBJECT"

  if [ ! -z "$BODY" ]; then
    while IFS= read -r line; do
      cleaned_line=$(echo "$line" | sed 's/^[[:space:]]*-*[[:space:]]*//')
      echo "  - $cleaned_line"
    done <<< "$BODY"
  fi

  echo ""
done

exit 0