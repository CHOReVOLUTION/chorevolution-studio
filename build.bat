@REM Copyright 2015 The CHOReVOLUTION project
@REM
@REM Licensed under the Apache License, Version 2.0 (the "License");
@REM you may not use this file except in compliance with the License.
@REM You may obtain a copy of the License at
@REM
@REM      http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing, software
@REM distributed under the License is distributed on an "AS IS" BASIS,
@REM WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@REM See the License for the specific language governing permissions and
@REM limitations under the License.
@REM

@echo off

set update_only_depencencies=false
set force_update_dependency=false
set build_ide=false
set skip_update_dependency=false
set ARG=

@REM Process command line options
for %%i in (%*) do (
  set exist=
  if "%%i" == "-u" (
	set exist=1
    set update_only_depencencies=true
  )
  if "%%i"=="--update-only-dependencies" (
	set exist=1
    set update_only_depencencies=true
  )
  if "%%i"=="-f" (
    set exist=1
    set force_update_dependency=true
  )
  if "%%i"=="--force-update-dependencies" (
    set exist=1
    set force_update_dependency=true
  )
  if "%%i"=="-b" (
    set exist=1
    set build_ide=true
  )
  if "%%i"=="--build-ide" (
    set exist=1
    set build_ide=true
  )
  if "%%i"=="-s" (
    set exist=1
    set skip_update_dependency=true
  )
  if "%%i"=="--skip-update-dependencies" (
    set exist=1
    set skip_update_dependency=true
  )
  if "%%i"=="-h" (
    set exist=1
    goto usage
  )
  if "%%i"=="--help" (
    set exist=1
    goto usage
  )

  if not defined exist (
  	set arg=%%i
  	goto undefined 
  )
	    
)

@REM update CHOReVOLUTION Studio dependencies and force download dependency if specified
if %skip_update_dependency% == false (
    cd extra/eu.chorevolution.studio.eclipse.core.configurator
    if %force_update_dependency% == true (
        echo execute mvn -U clean in chorevolution-studio/extra/eu.chorevolution.studio.eclipse.core.configurator
        call mvn -U clean
    ) else (
        echo execute mvn clean in chorevolution-studio/extra/eu.chorevolution.studio.eclipse.core.configurator
        call mvn clean
    )
    cd ../../
)

@REM create CHOReVOLUTION Studio Bundle or compile it only if specified
if %update_only_depencencies% == false (
  if %build_ide% == true (
      echo execute -Pbuild-ide in chorevolution-studio/
      call mvn -Pbuild-ide
  ) else (
    echo execute mvn clean verify in chorevolution-studio/
      call mvn clean verify
  )
)

exit 1

:undefined
  echo unknown option "%arg%"

:usage
  echo usage: build.bat [options]
  echo Options:
  echo -u, --update-only-dependencies         Update dependencies without compiling CHOReVOLUTION Studio
  echo -f, --force-update-dependencies        Forces a check for missing releases and updated
  echo                                        snapshots on remote repositories.
  echo -b, --build-ide                        Generate the CHOReVOLUTION Studio Bundle.
  echo                                        You can find the bundle in
  echo                                        chorevolution-studio/releng/eu.chorevolution.studio.eclipse.product/target/products
  echo -s, --skip-update-dependencies         Skip the compiling of CHOReVOLUTION Studio dependencies.
  echo -h, --help                             Display help information.
