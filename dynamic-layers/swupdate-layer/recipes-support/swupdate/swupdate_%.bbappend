FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = " \
    file://09-swupdate-args.in \
    file://swupdate.cfg.in \
    file://swupdate_rescue.cfg.in \
    file://swupdate-wrapper \
    file://swupdate-wrapper-rescue \
    file://astra-swupdate.cfg \
    file://cfi.cfg \
    file://0001-support-erase-full-mtd-for-flash_write_nand.patch \
"

do_install:append() {
    # To install the files on respective directory on target
    install -Dm 0644 ${WORKDIR}/09-swupdate-args.in ${D}${libdir}/swupdate/conf.d/09-swupdate-args.sh
    install -d ${D}${bindir}
    if [ "${ENABLE_RESCUE_MODE}" = "1" ]; then
        install -Dm 0644 ${WORKDIR}/swupdate_rescue.cfg.in ${D}${sysconfdir}/swupdate.cfg
        install -m 0755 ${WORKDIR}/swupdate-wrapper-rescue ${D}${bindir}/swupdate-wrapper
    else
        install -Dm 0644 ${WORKDIR}/swupdate.cfg.in ${D}${sysconfdir}/swupdate.cfg
        install -m 0755 ${WORKDIR}/swupdate-wrapper ${D}${bindir}/swupdate-wrapper
    fi

    ln -s ${bindir}/swupdate-wrapper ${D}${bindir}/update
}
