SUMMARY = "Synaptics FACTORY TA"
SECTION = "devtools"
LICENSE = "CLOSED"
LICENSE_FLAGS = "Synaptics-EULA"

require recipes-security/optee/optee.inc

inherit python3native

DEPENDS = "python3-cryptography-native optee-os-tadevkit synasdk-drm-common-ta"

SRC_URI = "${SYNA_SRC_OPTEE_DEV}"

SRCREV = "${SYNA_SRCREV_OPTEE_DEV}"
PV = "${ASTRA_VERSION}+git${SRCPV}"

S = "${WORKDIR}/${SYNA_SOURCE_PREFIX}/tee/optee_dev/ta/drm/ta_factory"
B = "${S}"
OUT = "${WORKDIR}/OUT"

CHIP_FULL_NAME:platypus = "platypus"
CHIP_FULL_NAME:dolphin = "dolphin_a0"
CHIP_FULL_NAME:myna2 = "myna2"

TARGET_CFLAGS += "--sysroot=${STAGING_DIR_HOST}"
TARGET_LDFLAGS += "--sysroot=${STAGING_DIR_HOST}"

EXTRA_OEMAKE += "TA_DEV_KIT_DIR=${TA_DEV_KIT_DIR} \
                 CHIP_FULL_NAME=${CHIP_FULL_NAME} \
                 CROSS_COMPILE=${HOST_PREFIX} \
                 TEE_CATEGORY=OPTEE \
                 STAGING_DIR_HOST=${STAGING_DIR_HOST}"

EXTRA_OEMAKE += "CFLAGS='${TARGET_CFLAGS}' LDFLAGS='${TARGET_LDFLAGS}'"

do_compile() {
    oe_runmake -f Makefile.op -C ${S} O=${B}/out
}

do_install() {
    install -d ${D}${base_libdir}/optee_armtz/
    install -D -p -m0444 ${B}/out/*.ta ${D}${base_libdir}/optee_armtz/
}

FILES:${PN} = "${base_libdir}/optee_armtz/"
INSANE_SKIP:${PN} += " already-stripped"
