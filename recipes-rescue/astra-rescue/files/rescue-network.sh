#!/bin/sh
### BEGIN INIT INFO
# Provides:          rescue-network
# Default-Start:     S
# Description:       Setup network and sync time
### END INIT INFO

echo "[rescue] Bringing up eth0..."
ifconfig eth0 up > /dev/null 2>&1
sleep 1

echo "[rescue] Requesting DHCP lease..."
if udhcpc -i eth0 -q -n -t 10 >/dev/null 2>&1; then
    echo "[rescue] DHCP succeeded."
else
    echo "[rescue] DHCP failed or timed out."
fi

