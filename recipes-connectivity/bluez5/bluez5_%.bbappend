#/*******************************************************************************
#*
#*             Copyright 2021, Beechwoods Software, Inc.
#*
#*******************************************************************************/

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = " \
    file://0001-change-service-type-to-notify-for-syna.patch \
    file://0001-enable-userspace-hid-syna.patch \
    file://0001-disable-bond-check-for-syna.patch \
    file://0001-enable-auto-enable-syna.patch \
"

do_install:append(){
    if [ -f ${S}/src/main.conf ]; then
        install -d ${D}/${sysconfdir}/bluetooth
        install -m 0644 ${S}/src/main.conf ${D}/${sysconfdir}/bluetooth/
    fi
}
