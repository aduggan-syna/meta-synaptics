DESCRIPTION = "Bootloader for Synaptics platform."
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://boot/u-boot/Licenses/README;md5=2ca5f2c35c8cc335f0a19756634782f1"
SECTION = "bootloaders"

PR = "r3"

inherit deploy nopackages

PROVIDES = " \
    virtual/bootloader \
"

PACKAGE_ARCH = "${MACHINE_ARCH}"

SECURITY_STACK_PROTECTOR = ""

DEPENDS = " \
    synasdk-security-native \
    synasdk-tools-native \
    vim-native \
    bc-native \
    bison-native \
    flex-native \
"

do_compile[depends] += "synasdk-sm:do_populate_sysroot"

COMPATIBLE_MACHINE = "syna"
PACKAGE_ARCH = "${MACHINE_ARCH}"


SRC_URI = " \
    ${SYNA_SRC_BOOT} \
    ${SYNA_SRC_UBOOT} \
    ${SYNA_SRC_EXTERNAL} \
    ${SYNA_SRC_TEE} \
    file://generate_boot_manifest.py \
"

SRCREV_uboot = "${SYNA_SRCREV_UBOOT}"
SRCREV_boot = "${SYNA_SRCREV_BOOT}"
SRCREV_external = "${SYNA_SRCREV_EXTERNAL}"
SRCREV_tee = "${SYNA_SRCREV_TEE}"

SRCREV_FORMAT = "uboot_boot_external"

require recipes-devtools/synasdk/synasdk-build.inc

PV = "2025.01+git${SRCPV}"

do_compile () {
    security_tools_path="${STAGING_DIR_NATIVE}${prefix}/libexec/syna/"
    security_keys_path="${STAGING_DATADIR_NATIVE}/syna/keys/${syna_chip_name}/${syna_chip_rev}"

    if [ "null${SYNA_SDK_REVISION}" != "null" ]; then
        export LOCALVERSION=".${SYNA_SDK_REVISION}"
    fi
    CONFIG_TOOLCHAIN_BSP=${HOST_PREFIX}
    if [ -f "${WORKDIR}/recipe-sysroot/${nonarch_base_libdir}/output_sm/bin/sm.bin" ]; then
        install -D ${WORKDIR}/recipe-sysroot/${nonarch_base_libdir}/output_sm/bin/sm.bin ${B}/target/release/uboot/intermediate/output_sm/bin/sm.bin
    fi
    clean=0 . build/module/uboot/build.sh "${CONFIG_FILE}"

    if [ $? -ne 0 ]; then
        echo 'bootloader build failed!'
        exit 1
    fi
}

do_deploy () {
    # bootloader.subimg
    if [ "is${CONFIG_GENX_MCU}" = "isy" ]; then
        cp "${B}/target/release/uboot/uboot_en.bin" "${DEPLOYDIR}/bootloader_nopreload.subimg"
    else
        prepend_image_info.sh "${B}/target/release/uboot/uboot_en.bin" "${DEPLOYDIR}/bootloader_nopreload.subimg"
    fi

    if [ "${MACHINE}" != "sl1620usb" ] && [ "${MACHINE}" != "sl1640usb" ] && [ "${MACHINE}" != "sl1680usb" ]; then
        if [ -f "${B}/target/release/uboot/sm_fw_en.bin" ]; then
            install -m 0644 "${B}/target/release/uboot/sm_fw_en.bin" "${DEPLOYDIR}"
        fi
        exec_cmd="parse_pt_emmc 101 101 \
                  ${CONFIG_EMMC_BLOCK_SIZE} ${CONFIG_EMMC_TOTAL_SIZE}"
        if [ "${MACHINE}" = "sl1680spi" ] || [ "${MACHINE}" = "sl1620spi" ] || [ "${MACHINE}" = "sl1640spi" ]; then
            . "${STAGING_DIR_NATIVE}/usr/share/syna/build/${SYNA_SDK_FLASH_TYPE_CFG_FILE}"
            exec_cmd="parse_pt 0 0 \
                      ${spi_block_size} ${spi_total_size}"
        fi

        if [ "${MACHINE}" = "sl2619nand" ] || [ "${MACHINE}" = "sl2611nand" ]; then
            exec_cmd="parse_pt 0 0 \
                 ${CONFIG_NAND_BLOCK_SIZE} ${CONFIG_NAND_TOTAL_SIZE}"
        fi

        exec_args="${EMMC_PT_FILE} \
                  ${DEPLOYDIR}/linux_params_mtdparts \
                  ${DEPLOYDIR}/version_table \
                  ${DEPLOYDIR}/subimglayout "
        if [ "${MACHINE}" != "sl1680spi" ] && [ "${MACHINE}" != "sl1620spi" ] && [ "${MACHINE}" != "sl1640spi" ] && [ "${MACHINE}" != "sl2619nand" ] && [ "${MACHINE}" != "sl2611nand" ]; then
            exec_args="${exec_args} \
                                  ${DEPLOYDIR}/emmc_part_table \
                                  ${DEPLOYDIR}/emmc_part_list \
                                  ${DEPLOYDIR}/emmc_image_list "
        fi

        # Parse pt file
        ${exec_cmd} ${exec_args}

        # Update the CRC of the version table
        crc -a "${DEPLOYDIR}/version_table"
        if [ "${MACHINE}" != "sl1680spi" ] && [ "${MACHINE}" != "sl1620spi" ] && [ "${MACHINE}" != "sl1640spi" ] && [ "${MACHINE}" != "sl2619nand" ] && [ "${MACHINE}" != "sl2611nand" ]; then
            # Change the subimage files to .gz
            sed -i -e 's:\([a-zA-Z0-9]\+\)\(_[a|b]\)\?\.subimg,:\1.subimg.gz,:' "${DEPLOYDIR}/emmc_image_list"
        fi
    else
        python3 ${WORKDIR}/generate_boot_manifest.py \
            --uboot_binary ${B}/target/release/uboot/uboot_en.bin \
            --sdk_config ${STAGING_DATADIR_NATIVE}/syna/build/.config \
            --uboot_config ${B}/target/release/uboot/intermediate/output_uboot/.config \
            --output ${WORKDIR}/manifest.yaml
        install -m 0644 ${WORKDIR}/manifest.yaml ${DEPLOYDIR}/manifest.yaml
    fi
}

addtask deploy before do_package after do_install

do_install[noexec] = "1"
