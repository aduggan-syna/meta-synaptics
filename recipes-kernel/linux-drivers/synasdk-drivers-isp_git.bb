SUMMARY = "Synaptics ISP V4L2 Kernel Module"
DESCRIPTION = "${SUMMARY}"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${WORKDIR}/COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

COMPATIBLE_MACHINE = "dolphin"

KERNEL_MODULE_AUTOLOAD:append:dolphin = " isp"
KERNEL_MODULE_PROBECONF:append:dolphin = " isp"

inherit module

SRC_URI = "file://COPYING \
           ${SYNA_SRC_EXTERNAL}"

SRCREV = "${SYNA_SRCREV_LINUX_6_12_MODULES}"

PV = "${ASTRA_VERSION}+git${SRCPV}"

S = "${WORKDIR}/${SYNA_EXTERNAL_DRIVERS_PREFIX}/drivers/isp"

do_install:append() {
    rm -f ${D}/lib/modules/${KERNEL_VERSION}/extra/modules.order.*
}

RPROVIDES:${PN} += "kernel-module-isp"
