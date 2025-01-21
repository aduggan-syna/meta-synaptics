#!/bin/sh


NAME=isp_media_server
PATH=/sbin:/bin:/usr/sbin:/usr/bin
DESC="ISP Media Server"

DAEMON=/usr/bin/$NAME

mkdir -p /run/$NAME
RUN=/run/$NAME

PIDFILE="$RUN/$NAME.pid"

mkdir -p /var/log
VAR_LOG="/var/log"

LOGFILE="$VAR_LOG/isp.log"

test -f $DAEMON || exit 0

SSD_OPTIONS="--oknodo --quiet  --make-pidfile --pidfile $PIDFILE  --exec $DAEMON"
SSD_KILL="--signal KILL --retry 5"

set -e

case $1 in
    start)
        echo "sensor=ov5647 xml=/usr/share/OV5647_480p.xml manu_json=/usr/share/ISP_Manual_IMX258.json \
        auto_json=/usr/share/ISP_Auto_OV5647.json i2c_bus_id=3 mipi_id=0 mode=0" > /proc/vsi/isp_subdev0
        echo "1 sensor=ov5647 1 xml=/usr/share/OV5647_480p.xml 1 manu_json=/usr/share/ISP_Manual_IMX258.json \
        1 auto_json=/usr/share/ISP_Auto_OV5647.json 1 i2c_bus_id=0 1 mipi_id=1 1 mode=0" > /proc/vsi/isp_subdev0
        echo -n "Starting $DESC: "
        start-stop-daemon --start $SSD_OPTIONS  > $LOGFILE &
        echo "${DAEMON##*/}."
        ;;
    stop)
        echo -n "Stopping $DESC: "
        start-stop-daemon  --stop $SSD_OPTIONS $SSD_KILL
        echo "${DAEMON##*/}."
        ;;
    restart|force-reload)
        $0 stop
        sleep 1
        $0 start
        exit 0
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
