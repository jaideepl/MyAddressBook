#!/bin/bash

# input params
branchName=$1
buildType=$2
storePass=$3
keyAlias=$4
keyPass=$5

# helper method
setProperty() {
	sed -i.bak -e "s/\($1 *= *\).*/\1$2/" ${propertiesFile}
}

# -----------------------------------------------------------------
# ------------------------------ BUILD ----------------------------
# -----------------------------------------------------------------
propertiesFile='gradle.properties'
chmod +x ${propertiesFile}

# update key properties based on build type
if [ $buildType = 'debug' ]; then
	(setProperty "KEYSTORE" "debug.keystore")
	(setProperty "STORE_PASSWORD" "123456")
	(setProperty "KEY_ALIAS" "my_alias")
	(setProperty "KEY_PASSWORD" "123456")
elif [ $buildType = 'release' ]; then
	(setProperty "KEYSTORE" "release.keystore")
	(setProperty "STORE_PASSWORD" "$storePass")
	(setProperty "KEY_ALIAS" "$keyAlias")
	(setProperty "KEY_PASSWORD" "$keyPass")
fi

# clean project
chmod +x gradlew
./gradlew clean --stacktrace

# build
if [ $buildType = 'debug' ]; then
	./gradlew :app:bundleDebug --stacktrace
elif [ $buildType = 'release' ]; then
	./gradlew :app:bundleRelease --stacktrace
fi

# -----------------------------------------------------------------
# -------------------------- POST BUILD ---------------------------
# -----------------------------------------------------------------
bundleFileName="bundle.aab"
rm -r artifacts/
mkdir artifacts

# copy bundle to artifacts
if [ ! -e "app/build/outputs/bundle/$buildType/$bundleFileName" ]; then
    echo "ERROR: File not exists: (app/build/outputs/bundle/$buildType/$bundleFileName)"
    exit 1
fi
cp app/build/outputs/bundle/$buildType/$bundleFileName artifacts/
mv artifacts/{*.aab,app-$buildType.apk}

cat << "EOF"

EOF