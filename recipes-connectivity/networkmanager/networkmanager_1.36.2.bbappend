ALTERNATIVE_PRIORITY[resolv-conf] = "20"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += "file://NetworkManager.conf"
SRC_URI += "file://0001-wifi-disable-FT-in-AP-mode.patch"

do_install:append() {
        install -m 0644 ${WORKDIR}/NetworkManager.conf ${D}/etc/NetworkManager/NetworkManager.conf
}

