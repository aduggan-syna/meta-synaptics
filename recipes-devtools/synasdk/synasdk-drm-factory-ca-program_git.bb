SUMMARY = "Synaptics DRM FACTORY CA PROGRAM"
SECTION = "devtools"
LICENSE = "CLOSED"
LICENSE_FLAGS = "Synaptics-EULA"

require recipes-security/optee/optee.inc

inherit python3native

DEPENDS = "python3-cryptography-native optee-os-tadevkit optee-client"

SRC_URI = "${SYNA_SRC_OPTEE_DEV}"

SRCREV = "${SYNA_SRCREV_OPTEE_DEV}"
PV = "${ASTRA_VERSION}+git${SRCPV}"

S = "${WORKDIR}/${SYNA_SOURCE_PREFIX}/tee/optee_dev/host/drm"
B = "${S}"

CHIP_FULL_NAME:platypus = "platypus"
CHIP_FULL_NAME:dolphin = "dolphin_a0"
CHIP_FULL_NAME:myna2 = "myna2"
CHIP_FULL_NAME:klamath = "klamath"

EXTRA_OEMAKE += "TA_DEV_KIT_DIR=${TA_DEV_KIT_DIR} \
                 CHIP_FULL_NAME=${CHIP_FULL_NAME} \
                 CROSS_COMPILE=${HOST_PREFIX} \
                 TA_CROSS_COMPILE=${HOST_PREFIX} \
                 TEE_CATEGORY=OPTEE"

do_compile() {
    oe_runmake -f Makefile.op -C ${S}/otp_program O=${B}/out
    oe_runmake -f Makefile.op -C ${S}/write_rkekid O=${B}/out
    oe_runmake -f Makefile.op -C ${S}/read_rkekid O=${B}/out
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${B}/otp_program/otp_program ${D}${bindir}
    install -m 0755 ${B}/write_rkekid/write_rkekid ${D}${bindir}
    install -m 0755 ${B}/read_rkekid/read_rkekid ${D}${bindir}
}

FILES:${PN} += "${bindir}/otp_program"
FILES:${PN} += "${bindir}/write_rkekid"
FILES:${PN} += "${bindir}/read_rkekid"
INSANE_SKIP:${PN} += " already-stripped"
