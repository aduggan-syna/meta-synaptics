#!/bin/sh

if [ -e /dev/$1 ] && [[ $1 =~ sd[a-z][0-9] ]]; then
    mount -m /dev/$1 /media/$1
fi
