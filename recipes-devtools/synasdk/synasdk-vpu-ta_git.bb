SUMMARY = "Synaptics VPU TA"
SECTION = "devtools"
LICENSE = "CLOSED"
LICENSE_FLAGS = "Synaptics-EULA"

require recipes-security/optee/optee.inc

inherit python3native

DEPENDS = "python3-cryptography-native optee-os-tadevkit"

SRC_URI = "${SYNA_SRC_OPTEE_DEV}"

SRCREV = "${SYNA_SRCREV_OPTEE_DEV}"
PV = "${ASTRA_VERSION}+git${SRCPV}"

S = "${WORKDIR}/${SYNA_SOURCE_PREFIX}/tee/optee_dev/ta/vpu"
B = "${S}/ta"
OUT = "${WORKDIR}/OUT"

CHIP_FULL_NAME:platypus = "platypus"
CHIP_FULL_NAME:dolphin = "dolphin_a0"

EXTRA_OEMAKE += "TA_DEV_KIT_DIR=${TA_DEV_KIT_DIR} \
                 CHIP_FULL_NAME=${CHIP_FULL_NAME} \
                 CROSS_COMPILE=${HOST_PREFIX} \
                 O=${OUT}"

do_compile() {
     oe_runmake -f Makefile.op -C ${B}
}

do_install() {
    install -d ${D}${nonarch_base_libdir}/optee_armtz/
    install -m 0755 ${OUT}/*.ta ${D}${nonarch_base_libdir}/optee_armtz/
    install -m 0755 ${OUT}/*.elf ${D}${nonarch_base_libdir}/optee_armtz/
}

FILES:${PN} = "${nonarch_base_libdir}/optee_armtz/"
INSANE_SKIP:${PN} += " already-stripped"
