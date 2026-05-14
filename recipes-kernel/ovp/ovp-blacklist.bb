SUMMARY = "Blacklist OVP V4L2 module"
LICENSE = "CLOSED"

SRC_URI += "file://ovp-blacklist.conf"

do_install() {
    install -d ${D}${sysconfdir}/modprobe.d
    install -m 0644 ${WORKDIR}/ovp-blacklist.conf \
        ${D}${sysconfdir}/modprobe.d/ovp-blacklist.conf
}

FILES:${PN} += "${sysconfdir}/modprobe.d/ovp-blacklist.conf"
