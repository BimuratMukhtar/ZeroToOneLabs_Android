#!/usr/bin/env bash

DIR="$( cd "$( dirname "$0" )" && pwd )"


# Quick and hacky generator to get started on new projects fast

function pause(){
   read -p "$*"
}

if [ $# -lt 1 ] ; then
	echo "usage ./configure-project.sh <app_name>"
	echo "  for example:"
	echo "    ./configure-project.sh \"app_example\" "
	echo "  This command resets all files/directories to a new state with the given parameters"
	exit
fi

# Warning
echo "** This command will overwrite most of your project directories **"
echo "** NEVER RUN THIS FILE WHEN IT IS IN A DIRECTORY OTHER THAN THE $1 DIRECTORY **"
pause 'Press [Enter] key to continue...'

package=$1
appname=$1

dirpackage=${package//[\.]//}

(
	cd $DIR/app/src/main/java/com/zerotoonelabs/$package
	mkdir api
	mkdir binding
	mkdir common
	mkdir data
	mkdir db
	mkdir di
	mkdir printutils
	mkdir repository
	mkdir ui
	mkdir util
	mkdir viewmodel
	mkdir vo
)