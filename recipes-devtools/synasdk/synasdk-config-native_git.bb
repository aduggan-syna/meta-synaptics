DESCRIPTION = "Synaptics SDK configuration"
SECTION = "devtools"
LICENSE = "CLOSED"
LICENSE_FLAGS = "Synaptics-EULA"

PR = "r1"

inherit native

SRC_URI = " \
    ${SYNA_SRC_BUILD} \
    ${SYNA_SRC_CONFIGS} \
"

SRCREV_build = "${SYNA_SRCREV_BUILD}"
SRCREV_configs = "${SYNA_SRCREV_CONFIGS}"

SRCREV_FORMAT = "build_configs"

PV = "${ASTRA_VERSION}+git${SRCPV}"

S = "${WORKDIR}/${SYNA_SOURCE_PREFIX}"

do_compile() {

    cd ${S}/build

    export srctree=../
    export KCONFIG_CONFIG=${B}/.config
    export PYTHONPATH=tools/src/kconfiglib/
    export AMP_KCONFIG_MODE=build

    python3 -B tools/src/kconfiglib/defconfig.py \
        ../configs/product/${SYNA_SDK_CONFIG_NAME}/${SYNA_SDK_CONFIG_FILE} \
        --kconfig build/Kconfig.build

    sed -i -e 's/(TOPDIR)/{TOPDIR}/g' $KCONFIG_CONFIG
    sed -i -e 's/CONFIG_DYNAMIC_FFMPEG=.*$/CONFIG_DYNAMIC_FFMPEG=n/' $KCONFIG_CONFIG

}

do_install () {
    install -d ${D}${prefix}/share/syna/build
    install -m 0755 ${B}/.config ${D}${prefix}/share/syna/build
    install -m 0755 ${S}/build/chip.rc ${D}${prefix}/share/syna/build

    . ${B}/.config

    if [ "null${SYNA_SDK_PT_FILE}" != "null" ]; then
        install -m 0755 ${S}/configs/product/${SYNA_SDK_CONFIG_NAME}/${SYNA_SDK_PT_FILE} ${D}${prefix}/share/syna/build
    fi
}

PACKAGES = " \
    ${PN} \
"

FILES:${PN} = " \
    ${prefix}/share/syna/build/* \
"
