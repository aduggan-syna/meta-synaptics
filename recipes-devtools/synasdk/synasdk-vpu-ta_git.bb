SUMMARY = "Synaptics VPU TA"
SECTION = "devtools"
LICENSE = "CLOSED"
LICENSE_FLAGS = "Synaptics-EULA"

require recipes-security/optee/optee.inc

inherit python3native

DEPENDS = "python3-cryptography-native optee-os-tadevkit"

SRC_URI = "${SYNA_SRC_OPTEE_DEV}"

S = "${WORKDIR}/${SYNA_SOURCE_PREFIX}/tee/optee_dev/ta/vpu"
B = "${S}/ta"
OUT = "${WORKDIR}/OUT"

CHIP_FULL_NAME:platypus = "platypus"
CHIP_FULL_NAME:dolphin = "dolphin_a0"

EXTRA_OEMAKE += "TA_DEV_KIT_DIR=${TA_DEV_KIT_DIR} \
                 CHIP_FULL_NAME=${CHIP_FULL_NAME} \
                 CROSS_COMPILE=${HOST_PREFIX} \
                 TEE_CATEGORY=OPTEE \
                 O=${OUT}"

do_install() {
    install -d ${D}${base_libdir}/optee_armtz/
    install -m 0755 ${OUT}/*.ta ${D}${base_libdir}/optee_armtz/
    install -m 0755 ${OUT}/*.elf ${D}${base_libdir}/optee_armtz/
}

FILES:${PN} = "${base_libdir}/optee_armtz/"
INSANE_SKIP:${PN} += " already-stripped"
