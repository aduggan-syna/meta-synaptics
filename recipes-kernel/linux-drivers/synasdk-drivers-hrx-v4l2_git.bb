SUMMARY = "Synaptics HRX V4L2 Kernel Module"
DESCRIPTION = "${SUMMARY}"

LICENSE = "CLOSED"

COMPATIBLE_MACHINE = "dolphin"

KERNEL_MODULE_AUTOLOAD:append:dolphin = " hrx-v4l2"
KERNEL_MODULE_PROBECONF:append:dolphin = " hrx-v4l2"

inherit module

SRC_URI = "file://COPYING \
           ${SYNA_SRC_LINUX_5_15_MODULES}"

SRCREV = "${SYNA_SRCREV_LINUX_5_15_MODULES}"

PV = "${ASTRA_VERSION}+git${SRCPV}"

S = "${WORKDIR}/${SYNA_MODULES_SOURCE_PREFIX}/drivers/hrx-v4l2"

do_install:append() {
    rm -f ${D}/lib/modules/${KERNEL_VERSION}/extra/modules.order.*
}

RPROVIDES:${PN} += "kernel-module-hrx-v4l2"
