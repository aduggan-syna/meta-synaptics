DESCRIPTION = "Synaptic OVP TA"
SECTION = "devtools"
LICENSE = "CLOSED"
LICENSE_FLAGS = "Synaptics-EULA"
PR = "r1"

inherit python3native
require recipes-security/optee/optee.inc
require synasdk-config.inc

DEPENDS = "optee-client optee-os-tadevkit python3-cryptography-native"
DEPENDS += " synasdk-config-native"

SRC_URI = " \
   ${SYNA_SRC_OPTEE_DEV} \
   file://99-video-deint.rules \
"

SRCREV = "${SYNA_SRCREV_OPTEE_DEV}"
PV = "${ASTRA_VERSION}+git${SRCPV}"

S = "${WORKDIR}/${SYNA_SOURCE_PREFIX}/tee/optee_dev/ta/display/ta/ovp"
B = "${WORKDIR}/build"

MODELS_DIR:dolphin = "dolphin"
SOC_VER:dolphin = "dolphin"

MODELS_DIR:platypus = "platypus"
SOC_VER:platypus = "platypus"

do_configure () {
    . ${CONFIG_FILE}
    . ${CHIP_RC_FILE}
}

do_compile() {
    . ${CONFIG_FILE}
    . ${CHIP_RC_FILE}

    export CFLAGS="${CFLAGS} --sysroot=${STAGING_DIR_HOST}"
    export LIBGCC_LOCATE_CFLAGS=--sysroot=${STAGING_DIR_HOST}
    export TA_CROSS_COMPILE=${HOST_PREFIX}
    export TA_DEV_KIT_DIR=${TA_DEV_KIT_DIR}

    cd ${S}
    make -f Makefile.op TA_DEV_KIT_DIR=${TA_DEV_KIT_DIR} CROSS_COMPILE=${HOST_PREFIX} SoC_Ver=${SOC_VER} Single_Instance=y O=${B}/out
}

do_install() {
    mkdir -p ${D}${nonarch_base_libdir}/optee_armtz
    install -D -p -m0444 ${B}/out/*.ta ${D}${nonarch_base_libdir}/optee_armtz/
    install -D -p -m0444 ${B}/out/*.elf ${D}${nonarch_base_libdir}/optee_armtz
    install -d ${D}${sysconfdir}/udev/rules.d
    install -m 0644 ${WORKDIR}/99-video-deint.rules ${D}${sysconfdir}/udev/rules.d/99-video-deint.rules
}

do_deploy () {
}

FILES:${PN} += "${nonarch_base_libdir}/optee_armtz/"
