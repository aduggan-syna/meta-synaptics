#!/bin/bash
echo 0 > /sys/class/rfkill/rfkill2/state
echo 1 > /sys/class/rfkill/rfkill2/state

echo 0 > /sys/class/rfkill/rfkill0/state
echo 1 > /sys/class/rfkill/rfkill0/state


systemctl stop bluetooth.service

systemctl status bluetooth.service

systemctl stop brcm_bt_start.service

systemctl status brcm_bt_start.service

/usr/bin/brcm_patchram_plus -d --patchram  /lib/firmware/bcm/BCM4381A1_Generic_UART_60MHz_WLBGA_Ref_iLNA_CN_merged.hcd --baudrate 115200 --no2bytes --tosleep 5000 /dev/ttyS1
rm -rf color_light_dimmable_*
rm -rf macsplit_device.trace
