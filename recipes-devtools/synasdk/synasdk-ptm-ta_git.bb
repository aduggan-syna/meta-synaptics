SUMMARY = "Synaptics PTM TA"
SECTION = "devtools"
LICENSE = "CLOSED"
LICENSE_FLAGS = "Synaptics-EULA"

inherit synasdk-optee-ta

SRC_URI = "${SYNA_SRC_OPTEE_DEV}"
PV = "${ASTRA_VERSION}+git${SRCPV}"
SRCREV = "${SYNA_SRCREV_OPTEE_DEV}"

S = "${WORKDIR}/${SYNA_SOURCE_PREFIX}/tee/optee_dev/ta/ptm"
B = "${WORKDIR}/build"

SOC_VER:platypus = "platypus"
SOC_VER:dolphin = "dolphin"

EXTRA_OEMAKE +=" CHIP_FULL_NAME=${SOC_VER}"

do_deploy() {


}
