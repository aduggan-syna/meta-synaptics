DESCRIPTION = "Synaptics Trusted Execution Environment EL1 image"
SECTION = "devtools"
LICENSE = "CLOSED"
LICENSE_FLAGS = "Synaptics-EULA"
PR = "r2"

inherit nopackages deploy

PACKAGE_ARCH = "${MACHINE_ARCH}"

DEPENDS += "synasdk-security-native synasdk-tools-native vim-native bc-native optee-os arm-trusted-firmware cmake-native synasdk-tee-bootparam-native"

COMPATIBLE_MACHINE = "syna"

SRC_URI = "${SYNA_SRC_TEE}"
SRCREV_tee = "${SYNA_SRCREV_TEE}"

SRCREV_FORMAT = "tee"

require synasdk-build.inc

PV = "${ASTRA_VERSION}+git${SRCPV}"

do_compile:append () {
    security_tools_path="${STAGING_DIR_NATIVE}${prefix}/libexec/syna/"
    security_keys_path="${STAGING_DATADIR_NATIVE}/syna/keys/${syna_chip_name}/${syna_chip_rev}"
    if [ "is${syna_chip_name}" = "isdolphin" ]; then
      dst_dir="${S}/tee/tee/products/${syna_chip_name}/genx/${CONFIG_TZK_MEM_LAYOUT}/${syna_chip_rev}/"
      dst_tz_bin="${dst_dir}/tz2_op_en.bin"
      dst_atf_bin="${dst_dir}/tz1_op_en.bin"
    else
      dst_dir="${S}/tee/tee/products/${syna_chip_name}/${CONFIG_TZK_MEM_LAYOUT}/${syna_chip_rev}/"
      dst_tz_bin="${dst_dir}/tz2_op_en.bin"
      dst_atf_bin="${dst_dir}/tz1_op_en.bin"
    fi

    mkdir -p ${dst_dir}

    if [ -f ${STAGING_DIR_HOST}${nonarch_base_libdir}/firmware/tz1_en.bin ]; then
      cp ${STAGING_DIR_HOST}${nonarch_base_libdir}/firmware/tz1_en.bin ${dst_atf_bin}
    fi
    if [ -f ${STAGING_DIR_HOST}${nonarch_base_libdir}/firmware/tz2_en.bin ]; then
      cp ${STAGING_DIR_HOST}${nonarch_base_libdir}/firmware/tz2_en.bin ${dst_tz_bin}
    fi

    if [ "is${syna_chip_name}" = "isdolphin" ]; then
      cp -av ${STAGING_DIR_NATIVE}${datadir}/syna/tee/bootparam/${syna_chip_name}/genx/* ${S}/tee/tee/products/${syna_chip_name}/genx/${CONFIG_TZK_MEM_LAYOUT}/
    else
      cp -av ${STAGING_DIR_NATIVE}${datadir}/syna/tee/bootparam/${syna_chip_name}/* ${S}/tee/tee/products/${syna_chip_name}/${CONFIG_TZK_MEM_LAYOUT}/
    fi

    . build/module/tee/build.sh
}

do_deploy() {
    process_cmd="prepend_image_info.sh"
    if [ "is${CONFIG_GENX_MCU}" = "isy" ]; then
      process_cmd="cp"
    fi
    install -m 0644 ${B}/target/tee/tzk/tee_en.bin ${DEPLOYDIR}
    install -m 0644 ${B}/target/tee/tzk/tee_recovery_en.bin ${DEPLOYDIR}
    ${process_cmd} ${B}/target/tee/tzk/tee_en.bin ${DEPLOYDIR}/tee.subimg
    ${process_cmd} ${B}/target/tee/tzk/tee_recovery_en.bin ${DEPLOYDIR}/tee_recovery.subimg
}

addtask deploy after do_compile
