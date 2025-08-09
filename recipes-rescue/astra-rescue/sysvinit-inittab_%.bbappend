FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = " file://inittab"

do_install:append() {
    install -d ${D}${sysconfdir}
    install -m 0644 ${WORKDIR}/inittab ${D}${sysconfdir}/inittab
}
