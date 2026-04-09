#!/bin/sh

# Source function library
. /etc/init.d/functions

NAME=eth0mac
PATH=/sbin:/bin:/usr/sbin:/usr/bin
DESC="Set MACADDR"

DAEMON=@sbindir@/$NAME

test -f $DAEMON || exit 0

test -f @sysconfdir@/default/$NAME && . @sysconfdir@/default/$NAME
test -f @sysconfdir@/default/rcS && . @sysconfdir@/default/rcS

SSD_OPTIONS="--oknodo --quiet --exec $DAEMON -- -d ${OPTARGS}"
set -e

case $1 in
    start)
	    echo -n "Starting $DESC: "
	    start-stop-daemon --start ${SSD_OPTIONS}
        echo "${DAEMON##*/}."
        ;;
    stop)
	    echo -n "Stopping $DESC: "
	    start-stop-daemon --stop ${SSD_OPTIONS}
        echo "${DAEMON##*/}."
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
