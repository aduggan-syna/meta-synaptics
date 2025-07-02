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

    install -d ${D}${nonarch_base_libdir}/firmware/isp
    install -m 0644 ${STAGING_KERNEL_DIR}/firmware/isp/DEWARP_LUT.isp ${D}${nonarch_base_libdir}/firmware/isp
    install -m 0644 ${STAGING_KERNEL_DIR}/firmware/isp/DEWARP_LUT_IMX415.isp ${D}${nonarch_base_libdir}/firmware/isp
}

FILES:${PN} = " \
    ${nonarch_base_libdir} \
"
