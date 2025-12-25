SUMMARY = "Synaptics TSP TA"
SECTION = "devtools"
LICENSE = "CLOSED"
LICENSE_FLAGS = "Synaptics-EULA"
require recipes-devtools/synasdk/synasdk-config.inc
inherit synasdk-optee-ta

# TODO will uncomment these after dependencies merged
# DEPENDS += "synasdk-bridge-ta synasdk-drmse-ta"
DEPENDS += "synasdk-tools-native"

SRC_URI = "${SYNA_SRC_OPTEE_DEV}"
PV = "${ASTRA_VERSION}+git${SRCPV}"
SRCREV = "${SYNA_SRCREV_OPTEE_DEV}"

S = "${WORKDIR}/${SYNA_SOURCE_PREFIX}/tee/optee_dev/ta/tsp/tsp"
B = "${WORKDIR}/build"

SOC_VER:platypus = "platypus_rdk"
SOC_VER:dolphin = "dolphin_rdk"

EXTRA_OEMAKE +=" CHIP_FULL_NAME=${SOC_VER} "

# TODO will uncomment these after dependencies merged
#EXTRA_OEMAKE +=" BRIDGE_DRMSE_HEADER_PATH=${STAGING_INCDIR}/bridge_drmse "
#EXTRA_OEMAKE +=" BRIDGE_DRMSE_LIB_PATH=${STAGING_LIBDIR} "
#EXTRA_OEMAKE +=" DRM_COMMON_TA_HEADER_PATH=${STAGING_INCDIR}/bridge_drmse/drm_common "
#EXTRA_OEMAKE +=" BCM_HEADER_PATH=${STAGING_INCDIR}/bridge_drmse/bcm "
#EXTRA_OEMAKE +=" DRMSE_TA_HEADER_PATH=${STAGING_INCDIR}/drmse_ta "

do_compile() {
    . ${CONFIG_FILE}
    if [ "${CONFIG_GENX_ENABLE}" = "y" ] ; then
        oe_runmake -f Makefile.op -C ${S} CONFIG_GENX_ENABLE=${CONFIG_GENX_ENABLE}
    else
        oe_runmake -f Makefile.op -C ${S}
    fi
}

do_install:append() {
    install -d ${D}${includedir}/tsp
    install -m 0644 ${S}/include/tsp_cmd.h ${D}${includedir}/tsp/
}

do_deploy() {

}

#FILES_${PN}-dev += "${includedir}/tsp/*.h"
