FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += "file://bluealsa-override.conf"

do_install:append() {
    install -d ${D}${systemd_system_unitdir}/bluealsa.service.d
    install -m 0644 ${WORKDIR}/bluealsa-override.conf ${D}${systemd_system_unitdir}/bluealsa.service.d/override.conf
}

FILES:${PN} += "${systemd_system_unitdir}/bluealsa.service.d/override.conf"