DESCRIPTION = "Bluetooth connection GUI"
LICENSE = "CLOSED"
SRC_URI = "file://bl_app.py \
           file://test_connection.py \
           file://scan_devices.py \
           file://known_devices.py \
           file://bt_auto \
           file://bt_auto.service"

S = "${WORKDIR}"

do_install() {
    install -d ${D}${bindir}
    install -d ${D}/usr/bin
    install -m 0755 ${S}/bl_app.py ${D}${bindir}/bl_app.py
    install -m 0755 ${S}/test_connection.py ${D}${bindir}/test_connection.py
    install -m 0755 ${S}/scan_devices.py ${D}${bindir}/scan_devices.py
    install -m 0755 ${S}/known_devices.py ${D}${bindir}/known_devices.py
    install -m 0755 ${S}/bt_auto ${D}${bindir}/bt_auto

    install -d ${D}${sysconfdir}/systemd/system/
    install -m 0644 ${WORKDIR}/bt_auto.service ${D}${sysconfdir}/systemd/system/bt_auto.service
}

SYSTEMD_SERVICE_${PN} = "bt_auto.service"

inherit systemd

# Automatically enable the service on installation
SYSTEMD_AUTO_ENABLE = "enable"

# Automatically start the service on boot
SYSTEMD_AUTO_START = "1"

FILES_${PN} += "${systemd_system_unitdir}/bt_auto.service"
