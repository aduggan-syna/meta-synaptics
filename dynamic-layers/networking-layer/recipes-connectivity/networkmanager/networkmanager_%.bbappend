ALTERNATIVE_PRIORITY[resolv-conf] = "20"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += "file://NetworkManager.conf"

do_install:append() {
        install -m 0644 ${WORKDIR}/NetworkManager.conf ${D}/etc/NetworkManager/NetworkManager.conf
}
