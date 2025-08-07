ALTERNATIVE_PRIORITY[resolv-conf] = "20"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += "file://NetworkManager.conf"
SRC_URI += "file://99-disable_wifi_info_debug.rules"

do_install:append() {
    install -m 0644 ${WORKDIR}/NetworkManager.conf ${D}/etc/NetworkManager/NetworkManager.conf

    install -d ${D}${sysconfdir}/udev/rules.d
    install -m 0644 ${WORKDIR}/99-disable_wifi_info_debug.rules ${D}${sysconfdir}/udev/rules.d/
}
