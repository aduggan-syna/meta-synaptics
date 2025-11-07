SUMMARY = "Synaptics TORQ kernel module"
DESCRIPTION = "Out-of-tree kmod built against the staged kernel for SL26xx"
LICENSE = "GPL-2.0 WITH Linux-syscall-note"
LIC_FILES_CHKSUM = "file://${STAGING_KERNEL_DIR}/COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

SRC_URI = "git://github.com/synaptics-torq/torq-compiler.git;branch=main;protocol=https;submodules=1;subpath=runtime/torq_hw/hal/SL2610;destsuffix=git/runtime/torq_hw/hal/SL2610;name=torq_driver"
SRC_URI += "file://0001-torq_kernel_module-build-fix.patch"

SRCREV_torq_driver = "c898d9e6aa23e8d3e57972162911fcd7914ca16f"
SRCREV_FORMAT = "torq_driver"
PV = "git+${@d.getVar('SRCREV_torq_driver')[:8]}"
S = "${WORKDIR}/git"

TORQ_KMOD_SRCDIR ?= "${S}/runtime/torq_hw/hal/SL2610"

inherit module
DEPENDS += "virtual/kernel"

# Drive kbuild against the staged kernel
EXTRA_OEMAKE = "-C ${STAGING_KERNEL_DIR} O=${STAGING_KERNEL_BUILDDIR} ARCH=${ARCH} CROSS_COMPILE=${TARGET_PREFIX} M=${TORQ_KMOD_SRCDIR}"

do_install:append() {
    rm -f ${D}/lib/modules/${KERNEL_VERSION}/extra/modules.order.* || true
}

RPROVIDES:${PN} += "kernel-module-syna-npu"
KERNEL_MODULE_AUTOLOAD:append = " syna_npu"
KERNEL_MODULE_PROBECONF:append = " syna_npu"

COMPATIBLE_MACHINE = "syna"
SYNAMACH:klamath = "sl2619"

