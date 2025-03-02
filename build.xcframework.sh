#!/bin/bash

./gradlew :compose-remote-layout:clean
./gradlew :compose-remote-layout:assembleComposeRemoteLayoutCoreXCFramework
if [ $? -ne 0 ]; then
  echo "Gradle task failed."
  exit 1
fi

GENERATED_PATH="compose-remote-layout/build/XCFrameworks/release/ComposeRemoteLayoutCore.xcframework"
OUTPUT_DIR="../ComposeRemoteLayoutCoreBinary"
DEST_PATH="$OUTPUT_DIR/ComposeRemoteLayoutCore.xcframework"

mkdir -p "$DEST_PATH"

cp "$GENERATED_PATH" "$DEST_PATH"
if [ $? -ne 0 ]; then
  echo "Failed copy the XCFramework."
  exit 1
fi

#swift package compute-checksum "$ZIP_FILE"
