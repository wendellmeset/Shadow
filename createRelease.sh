#!/usr/bin/env bash
echo "Input changelog, enter \"end\" when done"
echo -n "" > ./src/main/resources/changelogLatest.txt
while [[ true ]]; do
  read -p "> " line
  if [ "$line" == "end" ]; then
    break
    fi
  echo $line >> ./src/main/resources/changelogLatest.txt
done

echo "Running build"
export JAVA_HOME="$HOME/.jdks/openjdk-17.0.1/"
./gradlew build
if [[ ! -d bin ]]; then
  mkdir bin
  fi
mv ./build/libs/sipoverprivate-1.0.0.jar bin
echo "Made release"