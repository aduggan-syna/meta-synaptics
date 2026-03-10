DESCRIPTION = "System Manager for Synaptics platform."
SECTION = "devtools"
LICENSE = "CLOSED"
LICENSE_FLAGS = "Synaptics-EULA"

PR = "r0"

inherit deploy

DEPENDS = " \
    synasdk-security-native \
    synasdk-tools-native \
"

COMPATIBLE_MACHINE = "syna"
TARGET_CC_ARCH = " -mfloat-abi=hard -mfpu=neon "

SRC_URI = " \
    ${SYNA_SRC_BOOT} \
"

SRCREV_boot = "${SYNA_SRCREV_BOOT}"

require synasdk-build.inc

# TODO: we should use the yocto toolchain
DEPENDS += "gcc-arm-aarch64-linux-gnu-native gcc-arm-arm-linux-gnueabihf-native"

compile () {
    security_tools_path="${STAGING_DIR_NATIVE}${prefix}/libexec/syna/"
    security_keys_path="${STAGING_DATADIR_NATIVE}/syna/keys/${syna_chip_name}/${syna_chip_rev}"

    if [ "null${SYNA_SDK_REVISION}" != "null" ]; then
        export LOCALVERSION=".${SYNA_SDK_REVISION}"
    fi
    export SIZE="${HOST_PREFIX}size"
    clean=0 . build/module/bootloader/build_sm.sh "${CONFIG_FILE}"

    if [ $? -ne 0 ]; then
        echo 'sm build failed!'
        exit 1
    fi
    if [ "is${CONFIG_GENX_ENABLE}" = "isy" ]; then
        . build/security.rc
        opt_outdir_release="${B}/target/release/uboot"
        opt_outdir_intermediate="${opt_outdir_release}/intermediate/output_sm/bin"
        [ -f ${opt_outdir_intermediate}/sm.bin ]
        cp -f ${opt_outdir_intermediate}/sm.bin ${opt_outdir_intermediate}/sm_fw_raw.bin
        sha256sum ${opt_outdir_intermediate}/sm_fw_raw.bin
        ${security_tools_path}in_extras.py "SM_FW" ${opt_outdir_release}/in_sm_fw_extras.bin 0x00000001
        genx_secure_image "SM_FW" "ree" ${opt_outdir_release}/in_sm_fw_extras.bin 0x0 ${opt_outdir_intermediate}/sm_fw_raw.bin ${opt_outdir_release}/sm_fw_en.bin
    fi
}

do_compile () {
    echo "Skip sm for $M{MACHINE}"
}

do_compile:dolphin () {
    compile
}

do_compile:platypus () {
    compile
}

do_install () {
    if [ -f ${B}/target/release/uboot/intermediate/output_sm/bin/sm.bin ]; then
        install -D ${B}/target/release/uboot/intermediate/output_sm/bin/sm.bin ${D}${nonarch_base_libdir}/output_sm/bin/sm.bin
    fi
}

do_deploy () {
    if [ -f "${B}/target/release/uboot/sm_fw_en.bin" ]; then
        install -m 0644 "${B}/target/release/uboot/sm_fw_en.bin" "${DEPLOYDIR}"
    fi
}

addtask deploy before do_package after do_install
FILES:${PN} += "${nonarch_base_libdir}/output_sm/"
