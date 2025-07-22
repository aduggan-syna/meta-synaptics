SUMMARY = "SyNAP kernel module"
DESCRIPTION = "${SUMMARY}"

LICENSE = "CLOSED"
LICENSE_FLAGS = "Synaptics-EULA"

COMPATIBLE_MACHINE = "platypus|dolphin"

inherit module

SRC_URI = "${SYNA_SRC_SYNAP_DRIVER}"

SRCREV = "${SYNA_SRCREV_SYNAP_DRIVER}"

PV = "${SYNAP_VERSION}"

S = "${WORKDIR}/${SYNA_SOURCE_PREFIX}/synap/vsi_npu_driver/kernel"

EXTRA_OEMAKE = "-C ${STAGING_KERNEL_DIR} M=${S}"

do_install:append() {
    rm -f ${D}/lib/modules/${KERNEL_VERSION}/extra/modules.order.*
}

RPROVIDES:${PN} += "kernel-module-synap"
KERNEL_MODULE_AUTOLOAD:append = " synap"
KERNEL_MODULE_PROBECONF:append = " synap"
module_conf_synap = "softdep synap pre: pvrsrvkm"
