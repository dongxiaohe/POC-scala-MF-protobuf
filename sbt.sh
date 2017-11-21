#!/bin/bash
set -eu
here="$(dirname "$0")"
cache="$here/.cache"
if [ ! -d "$cache" ]; then
	mkdir "$cache"
fi

# share cache directory so ZDI doesn't redownload the world
exec sbt \
	-Dsbt.ivy.home="$cache/ivy2" \
	-Dsbt.boot.directory="$cache/sbt-boot" \
	"$@"
