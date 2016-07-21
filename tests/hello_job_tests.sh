#!/bin/bash
set -e
echo "hello job testing"

# change this REPO to your location
REPO=git@github.com:dcdr/boise_devops_2016_hello.git
rm -rf tmp
mkdir -p tmp
cd tmp
git clone $REPO test
cd test
TIMESTAMP=$(date +%s)
BRANCHES=("qa" "staging" "prod" "benchtest/br_$TIMESTAMP" "releases/br_$TIMESTAMP" "benchtest/br_1" "releases/br_1")
for branch in "${BRANCHES[@]}"
do
  git checkout origin/master
  echo "echo hello from branch= $branch" > hello.sh
  git commit -a -m "changes to $branch"
  git push -f origin HEAD:refs/heads/$branch
  echo $branch
done

rm -rf tmp
