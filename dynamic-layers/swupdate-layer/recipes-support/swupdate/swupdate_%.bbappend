FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = " \
    file://09-swupdate-args.in \
    file://swupdate.cfg.in \
    file://swupdate-wrapper \
    file://astra-swupdate.cfg \
"

do_install:append() {
    # To install the files on respective directory on target
    install -Dm 0644 ${WORKDIR}/swupdate.cfg.in ${D}${sysconfdir}/swupdate.cfg
    install -Dm 0644 ${WORKDIR}/09-swupdate-args.in ${D}${libdir}/swupdate/conf.d/09-swupdate-args.sh
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/swupdate-wrapper ${D}${bindir}/swupdate-wrapper
    ln -s ${bindir}/swupdate-wrapper ${D}${bindir}/update
}
