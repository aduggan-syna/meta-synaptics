#!/bin/sh
### BEGIN INIT INFO
# Provides:          rescue-network
# Default-Start:     S
# Description:       Setup network and sync time
### END INIT INFO

echo "[rescue] Bringing up eth0..."
ifconfig eth0 up >/dev/null 2>&1
sleep 1

echo "[rescue] Starting DHCP..."
udhcpc -i eth0 >/dev/null 2>&1 &
UDHCPC_PID=$!

COUNT=0
while [ $COUNT -lt 10 ]; do
    if ! kill -0 $UDHCPC_PID 2>/dev/null; then
        break
    fi
    sleep 1
    COUNT=$((COUNT + 1))
done

if kill -0 $UDHCPC_PID 2>/dev/null; then
    echo "[rescue] udhcpc timeout, killing..."
    kill $UDHCPC_PID >/dev/null 2>&1
    sleep 1
    kill -9 $UDHCPC_PID >/dev/null 2>&1
fi
