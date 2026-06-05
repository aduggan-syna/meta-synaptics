DESCRIPTION = "Bootloader for Synaptics platform."
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=2ca5f2c35c8cc335f0a19756634782f1"
SECTION = "bootloaders"

PR = "r3"

inherit deploy nopackages syna-security

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

EXTRA_OEMAKE = 'KBUILD_OUTPUT=${B} CROSS_COMPILE=${TARGET_PREFIX} CC="${TARGET_PREFIX}gcc ${TOOLCHAIN_OPTIONS} ${DEBUG_PREFIX_MAP}" V=1'
EXTRA_OEMAKE += 'HOSTCC="${BUILD_CC} ${BUILD_CFLAGS} ${BUILD_LDFLAGS}"'

do_compile[depends] += "synasdk-sm:do_populate_sysroot"

COMPATIBLE_MACHINE = "syna"

SRC_URI = " \
    ${SYNA_SRC_UBOOT} \
    ${SYNA_SRC_TEE} \
    file://generate_boot_manifest.py \
"

SRCREV_uboot = "${SYNA_SRCREV_UBOOT}"
SRCREV_tee = "${SYNA_SRCREV_TEE}"

SRCREV_FORMAT = "uboot_tee"

PV = "2025.01+git${SRCPV}"

require recipes-bsp/u-boot/u-boot-configure.inc
require recipes-devtools/synasdk/synasdk-build.inc
S = "${WORKDIR}/${SYNA_SOURCE_PREFIX}/boot/u-boot"
B = "${WORKDIR}/${BP}/uboot/intermediate/output_uboot"

do_compile () {
    security_tools_path="${STAGING_DIR_NATIVE}${prefix}/libexec/syna/"
    security_keys_path="${STAGING_DATADIR_NATIVE}/syna/keys/${syna_chip_name}/${syna_chip_rev}"

    if [ "null${SYNA_SDK_REVISION}" != "null" ]; then
        export LOCALVERSION=".${SYNA_SDK_REVISION}"
    fi
    if [ -f "${WORKDIR}/recipe-sysroot/${nonarch_base_libdir}/output_sm/bin/sm.bin" ]; then
        install -D ${WORKDIR}/recipe-sysroot/${nonarch_base_libdir}/output_sm/bin/sm.bin ${B}/../output_sm/bin/sm.bin
    fi

    oe_runmake -C ${S} O=${B} EXT_DTB=arch/arm/dts/${UBOOT_DTS_FILE}
    cp -ad ${B}/u-boot.bin ${B}/../uboot_raw.bin
    sha256sum ${B}/../uboot_raw.bin

    binary2enc="uboot_raw.bin"
    if [ "is${CONFIG_GENX_MCU}" = "isy" ]; then
        f_mr=${S}/../../tee/tee/products/${syna_chip_name}/${CONFIG_TZK_MEM_LAYOUT}/mr_config
        [ -f ${f_mr} ]
        uboot_dest=$(awk '/bootloader/{print $2}' ${f_mr})
        ${security_tools_path}in_extras.py "BOOT_LOADER_GENX_V3" ${B}/../../in_boot_loader_extras.bin 0x00000000 0x00000000 ${uboot_dest} ${uboot_dest}
    else
        ${security_tools_path}in_extras.py "BOOT_LOADER" ${B}/../../in_boot_loader_extras.bin 0x00000001
        dd if=/dev/zero of=${B}/../uboot_prepending.bin bs=1 count=48
        cat ${B}/../uboot_prepending.bin ${B}/../uboot_raw.bin > ${B}/../uboot_prepending_raw.bin
        binary2enc="uboot_prepending_raw.bin"
    fi
    genx_secure_image "BOOT_LOADER" "ree" ${B}/../../in_boot_loader_extras.bin 0x0 ${B}/../${binary2enc} ${B}/../../uboot_en.bin
}

do_deploy () {
    # bootloader.subimg
    if [ "is${CONFIG_GENX_MCU}" = "isy" ]; then
        cp "${B}/../../uboot_en.bin" "${DEPLOYDIR}/bootloader_nopreload.subimg"
    else
        prepend_image_info.sh "${B}/../../uboot_en.bin" "${DEPLOYDIR}/bootloader_nopreload.subimg"
    fi

    if [ "${MACHINE}" != "sl1620usb" ] && [ "${MACHINE}" != "sl1640usb" ] && [ "${MACHINE}" != "sl1680usb" ] && [ "${MACHINE}" != "sl2619usb" ] && [ "${MACHINE}" != "sl2619-coralboard-usb" ] ; then
        if [ -f "${B}/../../sm_fw_en.bin" ]; then
            install -m 0644 "${B}/../../sm_fw_en.bin" "${DEPLOYDIR}"
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

        if [ "${MACHINE}" = "sl2619xspi" ]; then
            exec_cmd="parse_pt_xspi 101 101 \
                ${CONFIG_XSPI_BLOCK_SIZE} ${CONFIG_XSPI_TOTAL_SIZE}"
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
            --uboot_binary ${B}/../../uboot_en.bin \
            --sdk_config ${STAGING_DATADIR_NATIVE}/syna/build/.config \
            --uboot_config ${B}/.config \
            --output ${WORKDIR}/manifest.yaml
        install -m 0644 ${WORKDIR}/manifest.yaml ${DEPLOYDIR}/manifest.yaml
    fi
}

addtask deploy before do_package after do_install

do_install[noexec] = "1"
