SUMMARY = "Synaptics M2M TA"
SECTION = "devtools"
LICENSE = "CLOSED"
LICENSE_FLAGS = "Synaptics-EULA"

inherit synasdk-optee-ta

# TODO will uncomment these after dependencies merged
# DEPENDS += "synasdk-bridge-ta synasdk-drmse-ta"

SRC_URI = "${SYNA_SRC_OPTEE_DEV}"
PV = "${ASTRA_VERSION}+git${SRCPV}"
SRCREV = "${SYNA_SRCREV_OPTEE_DEV}"

S = "${WORKDIR}/${SYNA_SOURCE_PREFIX}/tee/optee_dev/ta/tsp/m2m"
B = "${WORKDIR}/build"

SOC_VER:platypus = "platypus_rdk"
SOC_VER:dolphin = "dolphin_rdk"

EXTRA_OEMAKE +=" CHIP_FULL_NAME=${SOC_VER}"
# TODO will uncomment these after dependencies merged
#EXTRA_OEMAKE +=" DRM_COMMON_TA_HEADER_PATH=${STAGING_INCDIR}/bridge_drmse/drm_common "
#EXTRA_OEMAKE +=" BCM_HEADER_PATH=${STAGING_INCDIR}/bridge_drmse/bcm "
#EXTRA_OEMAKE +=" DRMSE_TA_HEADER_PATH=${STAGING_INCDIR}/drmse_ta "

do_install:append() {
    install -d ${D}${includedir}/m2m
    install -m 0644 ${S}/include/m2m_crypto_types.h ${D}${includedir}/m2m/
    install -m 0644 ${S}/include/m2m_crypto_api.h ${D}${includedir}/m2m/
}

do_deploy() {

}

FILES_${PN}-dev += "${includedir}/m2m/*.h"
