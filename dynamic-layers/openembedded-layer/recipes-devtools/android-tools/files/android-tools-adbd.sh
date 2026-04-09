#!/bin/sh

# Source function library
. /etc/init.d/functions

NAME=adbd
PATH=/sbin:/bin:/usr/sbin:/usr/bin
DESC="Android Tools ADBD"

DAEMON=/usr/bin/$NAME

test -f $DAEMON || exit 0

test -f /etc/default/$NAME && . /etc/default/$NAME
test -f /etc/default/rcS && . /etc/default/rcS

SSD_OPTIONS="--oknodo --quiet --exec $DAEMON -- $OPTARGS"

set -e

case $1 in
    start)
        /usr/bin/android-gadget-setup
	    echo -n "Starting $DESC: "
	    start-stop-daemon --start $SSD_OPTIONS &
        echo "${DAEMON##*/}."
        /usr/bin/android-gadget-start
        ;;
    stop)
	    echo -n "Stopping $DESC: "
	    start-stop-daemon --stop $SSD_OPTIONS
        echo "${DAEMON##*/}."
        /usr/bin/android-gadget-cleanup
        ;;
    restart|force-reload)
	    $0 stop
	    sleep 1
	    $0 start
        ;;
    status)
        status ${DAEMON} || exit $?
        ;;
    *)
        echo "Usage: $0 {start|stop|restart|force-reload|status}" >&2
        exit 1
        ;;
esac

exit 0
