# Copyright 2021 - 2022, Synaptics Incorporated

DESCRIPTION = "Linux Kernel firmware for Synaptics STB SoCs"
SECTION = "kernel"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${STAGING_KERNEL_DIR}/COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

do_populate_lic[depends] += " \
    virtual/kernel:do_shared_workdir \
"
do_install[depends] += " \
    virtual/kernel:do_shared_workdir \
"

keep_sdio_fws() {
    rm -f ${D}${nonarch_base_libdir}/firmware/fw_bcm*.bin
    rm -rf ${D}${nonarch_base_libdir}/firmware/mrvl
}

do_install() {
    install -d ${D}${nonarch_base_libdir}/firmware/bcm
    install -m 0644 ${STAGING_KERNEL_DIR}/firmware/bcm/* ${D}${nonarch_base_libdir}/firmware/bcm
    # Need to remove firmware/bcm/brcmbt. It's for Android debug purpose and has mismatch format with RDK which makes build fail
    if [ -f ${D}${nonarch_base_libdir}/firmware/bcm/brcmbt ]; then
        rm ${D}${nonarch_base_libdir}/firmware/bcm/brcmbt
    fi
    cd ${D}${nonarch_base_libdir}/firmware/bcm || exit 1
        for ext in bin blob cal; do
            for f in *.$ext; do
                [ -e "$f" ] && mv "$f" ..
            done
        done
    cd - || exit 1

    install -d ${D}${nonarch_base_libdir}/firmware/mrvl
    install -m 0644 ${STAGING_KERNEL_DIR}/firmware/mrvl/* ${D}${nonarch_base_libdir}/firmware/mrvl
}

do_install:append:sl2611nand() {
    keep_sdio_fws
}

do_install:append:sl2615nand() {
    keep_sdio_fws
}

do_install:append:sl2619nand() {
    keep_sdio_fws
}

FILES:${PN} = " \
    ${nonarch_base_libdir} \
"
