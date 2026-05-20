SUMMARY = "Synaptics TORQ kernel module"
DESCRIPTION = "Out-of-tree kmod built against the staged kernel for SL26xx"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${WORKDIR}/COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

SRC_URI = "git://github.com/synaptics-torq/torq-compiler.git;protocol=https;tag=v2.0.0_beta;nobranch=1;name=torq_driver;submodules=1;subpath=runtime/torq_hw/hal/SL2610;destsuffix=git/runtime/torq_hw/hal/SL2610"
SRC_URI += "file://COPYING"


PV = "2.0.0_beta+git${SRCPV}"

S = "${WORKDIR}/git/runtime/torq_hw/hal/SL2610"
B = "${WORKDIR}/build"

inherit module

do_configure[cleandirs] = "${B}"

EXTRA_OEMAKE = "\
  -C ${STAGING_KERNEL_DIR} M=${S}"

MODULES_MODULE_SYMVERS_LOCATION = "."
do_compile:append() {
    install -d ${B}
    [ -f ${S}/Module.symvers ] || touch ${S}/Module.symvers
    [ -f ${B}/Module.symvers ] || ln -s ${S}/Module.symvers ${B}/Module.symvers
}

do_install:append() {
    rm -f ${D}/lib/modules/${KERNEL_VERSION}/extra/modules.order.* || true
}

RPROVIDES:${PN} += "kernel-module-syna_npu"
KERNEL_MODULE_AUTOLOAD:append = " syna_npu"
KERNEL_MODULE_PROBECONF:append = " syna_npu"

COMPATIBLE_MACHINE = "syna"
