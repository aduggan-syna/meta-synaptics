DESCRIPTION = "Synaptics SDK generate bootinfo subimg"
SECTION = "devtools"
LICENSE = "CLOSED"
LICENSE_FLAGS = "Synaptics-EULA"

PR = "r1"

inherit deploy

COMPATIBLE_MACHINE = "sl2619xspi"

DEPENDS = "xxd-native synasdk-tools-native"

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

    TOPDIR=${S}
    . $KCONFIG_CONFIG
    outdir_subimg_intermediate=${B}/target CONFIG_SYNA_SDK_OUT_HOST_REL_PATH="${STAGING_DIR_NATIVE}/usr/bin/" . module/image/lib/subimage/bootinfo/xspi.bashrc
}

do_deploy() {
    install -m 0644 target/bootinfo.subimg ${DEPLOYDIR}/bootinfo.subimg
}

addtask deploy before do_package after do_install
