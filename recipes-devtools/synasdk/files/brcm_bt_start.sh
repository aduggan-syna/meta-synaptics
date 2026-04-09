#!/bin/sh

# Source function library
. /etc/init.d/functions

NAME=brcm_patchram_plus
PATH=/sbin:/bin:/usr/sbin:/usr/bin
DESC="Start BRCM BT"

DAEMON=@bindir@/$NAME

test -f $DAEMON || exit 0

test -f @sysconfdir@/default/$NAME && . @sysconfdir@/default/$NAME
test -f @sysconfdir@/default/rcS && . @sysconfdir@/default/rcS

OPTARGS="--tosleep=300000 --baudrate 3000000 --use_baudrate_for_download --no2bytes --enable_hci --patchram /lib/firmware/bcm @btuart@"
SSD_OPTIONS="--oknodo --quiet --exec $DAEMON -- -d ${OPTARGS}"

set -e

case $1 in
    start)
	    rfkill unblock bluetooth
	    echo -n "Starting $DESC: "
	    start-stop-daemon --start $SSD_OPTIONS &
        echo "${DAEMON##*/}."
        ;;
    stop)
	    echo -n "Stopping $DESC: "
	    start-stop-daemon --stop $SSD_OPTIONS
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
