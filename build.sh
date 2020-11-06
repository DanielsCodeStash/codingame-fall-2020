#!/bin/bash
PACKAGE=$1
SRC_PATH="./src/main/kotlin/${PACKAGE}"
IGNORED="shared,${PACKAGE}"
OUT_FIXED="out/${PACKAGE}.kt"

./out/file-merger.sh "./src/main/kotlin/shared" "$SRC_PATH" kt

java -jar ./out/importFixer.jar out/Out.kt "$OUT_FIXED" "$IGNORED"

