#!/usr/bin/env sh
set -eu

MAVEN_VERSION=3.9.11
MAVEN_BASE="${HOME}/.m2/wrapper/dists/apache-maven-${MAVEN_VERSION}"
MAVEN_HOME="${MAVEN_BASE}/apache-maven-${MAVEN_VERSION}"
MAVEN_ZIP="${MAVEN_BASE}/apache-maven-${MAVEN_VERSION}-bin.zip"

if [ ! -x "${MAVEN_HOME}/bin/mvn" ]; then
  echo "Downloading Apache Maven ${MAVEN_VERSION}..."
  mkdir -p "${MAVEN_BASE}"
  if command -v curl >/dev/null 2>&1; then
    curl -fsSL "https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.zip" -o "${MAVEN_ZIP}"
  else
    wget -O "${MAVEN_ZIP}" "https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.zip"
  fi
  unzip -q -o "${MAVEN_ZIP}" -d "${MAVEN_BASE}"
fi

exec "${MAVEN_HOME}/bin/mvn" "$@"
