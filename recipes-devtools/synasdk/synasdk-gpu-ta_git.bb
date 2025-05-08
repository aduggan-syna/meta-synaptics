SUMMARY = "OP-TEE GPU TA"
DESCRIPTION = "OP-TEE GPU TA"

LICENSE = "CLOSED"

inherit python3native
require recipes-security/optee/optee.inc
require recipes-devtools/synasdk/synasdk-config.inc

SRC_URI = "${SYNA_SRC_OPTEE_DEV}"
SRCREV = "${SYNA_SRCREV_OPTEE_DEV}"

DEPENDS += "python3-cryptography-native optee-os-tadevkit"
DEPENDS += " synasdk-config-native"

PV = "${ASTRA_VERSION}+git${SRCPV}"

S = "${WORKDIR}/${SYNA_SOURCE_PREFIX}/tee/optee_dev/ta/ta_img_linux_ws"
B = "${WORKDIR}/build"

SOC_VER:platypus = "platypus"
SOC_VER:dolphin = "dolphin"

do_configure() {
    # For encryption.
    source ${CONFIG_FILE}
    source ${CHIP_RC_FILE}
}

do_compile() {
    export CFLAGS="${CFLAGS} --sysroot=${STAGING_DIR_HOST}"
    export LIBGCC_LOCATE_CFLAGS=--sysroot=${STAGING_DIR_HOST}
    export TA_CROSS_COMPILE=${HOST_PREFIX}
    export TA_DEV_KIT_DIR=${TA_DEV_KIT_DIR}

    echo "out is ${S}"

    cd ${S}
    make -f Makefile.op TA_DEV_KIT_DIR=${TA_DEV_KIT_DIR} CROSS_COMPILE=${HOST_PREFIX} O=${B}/out CHIP_FULL_NAME=${SOC_VER}
}

do_install() {
    mkdir -p ${D}${nonarch_base_libdir}/optee_armtz
    install -D -p -m0444 ${B}/out/*.ta ${D}${nonarch_base_libdir}/optee_armtz/
    install -D -p -m0444 ${B}/out/*.elf ${D}${nonarch_base_libdir}/optee_armtz/
}

do_deploy() {
}

FILES:${PN} += "${nonarch_base_libdir}/optee_armtz/"
