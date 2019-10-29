#!/bin/sh
#
# Copyright 2015 The CHOReVOLUTION project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

# about.properties
# contains externalized strings for about.ini
# java.io.Properties file (ISO 8859-1 with "\" escapes)
# fill-ins are supplied by about.mappings
# This file should be translated.

exit_error_occurred(){
    ret=$1
    if [ $ret -ne 0 ]; then
        exit $ret
    fi
}

usage() {
  echo "usage: $(basename "$0") [options]"
  echo "Options:"
  echo "-u, --update-only-dependencies         Update dependencies without compiling CHOReVOLUTION Studio"
  echo "-f, --force-update-dependencies        Forces a check for missing releases and updated"
  echo "                                       snapshots on remote repositories."
  echo "-b, --build-ide                        Generate the CHOReVOLUTION Studio Bundle."
  echo "                                       You can find the bundle in"
  echo "                                       $(basename "$PWD")/releng/eu.chorevolution.studio.eclipse.product/target/products"
  echo "-s, --skip-update-dependencies         Skip the compiling of CHOReVOLUTION Studio dependencies."
  echo "-h, --help                             Display help information."

  exit 1
}

update_only_depencencies=false
force_update_dependency=false
build_ide=false
skip_update_dependency=false

# Process command line options
for arg in "$@" ; do
  if [ "$arg" = "-u" -o "$arg" = "--update-only-dependencies" ] ; then
    update_only_depencencies=true
  elif [ "$arg" = "-f" -o "$arg" = "--force-update-dependencies" ] ; then
    force_update_dependency=true
  elif [ "$arg" = "-b" -o "$arg" = "--build-ide" ] ; then
    build_ide=true
  elif [ "$arg" = "-s" -o "$arg" = "--skip-update-dependencies" ] ; then
    skip_update_dependency=true
  elif [ "$arg" = "-h" -o "$arg" = "--help" ] ; then
    usage
  else
    echo "unknown option: $arg"
    usage
  fi
done

#update CHOReVOLUTION Studio dependencies and force download dependency if specified
if [ "$skip_update_dependency" = false ]; then
    cd extra/eu.chorevolution.studio.eclipse.core.configurator
    if [ "$force_update_dependency" = true ]; then
        echo execute mvn -U clean in $PWD
        mvn -U clean
        exit_error_occurred $?
    else
        echo execute mvn clean in $PWD
        mvn clean
        exit_error_occurred $?
    fi
    cd ../../
fi

# create CHOReVOLUTION Studio Bundle or compile it only if specified
if [ "$update_only_depencencies" = false ]; then
	if [ "$build_ide" = true ]; then
	    echo execute -Pbuild-ide in $PWD
	    mvn -Pbuild-ide
	    exit_error_occurred $?
	else
		echo execute mvn clean verify in $PWD
    	mvn clean verify
    	exit_error_occurred $?	
	fi
fi

exit