DESCRIPTION = "Synaptics SDK"
SECTION = "devtools"
LICENSE = "CLOSED"
LICENSE_FLAGS = "Synaptics-EULA"

PR = "r1"

inherit deploy

DEPENDS = " \
    synasdk-tools-native \
"

# Fix me
# Need security/keys for klamath
DEPENDS:append:klamath = " \
    synasdk-security-native \
"

COMPATIBLE_MACHINE = "syna"
PACKAGE_ARCH = "${MACHINE_ARCH}"

SRC_URI = " \
    ${SYNA_SRC_BOOT} \
    ${SYNA_SRC_PREBOOT} \
"

# klamath need boot/mcu instead of boot/preboot
SRC_URI:remove:klamath = "${SYNA_SRC_PREBOOT}"

SRCREV_boot = "${SYNA_SRCREV_BOOT}"
SRCREV_preboot = "${SYNA_SRCREV_PREBOOT}"

SRCREV_FORMAT = "boot_preboot"

require synasdk-build.inc

PV = "${ASTRA_VERSION}+git${SRCPV}"

do_compile () {
    security_tools_path="${STAGING_DIR_NATIVE}${prefix}/libexec/syna/"
    security_keys_path="${STAGING_DATADIR_NATIVE}/syna/keys/${syna_chip_name}/${syna_chip_rev}"

    clean=0 preboot_module_dir="${S}/build/module/preboot" . build/module/preboot/build.sh
    if [ $? -ne 0 ]; then
        echo 'preboot build failed!'
        exit 1
    fi
}

do_deploy() {
    if [ "${MACHINE}" = "sl1620usb" ] || [ "${MACHINE}" = "sl1640usb" ] || [ "${MACHINE}" = "sl1680usb" ]; then
        cat target/preboot/intermediate/release/K0_BOOT_store.bin > "${DEPLOYDIR}/gen3_scs.bin.usb"
        cat target/preboot/intermediate/release/K0_TEE_store.bin >> "${DEPLOYDIR}/gen3_scs.bin.usb"
        cat target/preboot/intermediate/release/K1_BOOT_A_store.bin >> "${DEPLOYDIR}/gen3_scs.bin.usb"
        cat target/preboot/intermediate/release/K1_BOOT_B_store.bin >> "${DEPLOYDIR}/gen3_scs.bin.usb"
        cat target/preboot/intermediate/release/K1_TEE_A_store.bin >> "${DEPLOYDIR}/gen3_scs.bin.usb"

        cat target/preboot/intermediate/release/K0_BOOT_store.bin > "${DEPLOYDIR}/gen3_bkl.bin.usb"
        cat target/preboot/intermediate/release/K0_TEE_store.bin >> "${DEPLOYDIR}/gen3_bkl.bin.usb"
        cat target/preboot/intermediate/release/K1_BOOT_A_store.bin >> "${DEPLOYDIR}/gen3_bkl.bin.usb"
        cat target/preboot/intermediate/release/K1_BOOT_B_store.bin >> "${DEPLOYDIR}/gen3_bkl.bin.usb"
        cat target/preboot/intermediate/release/K1_TEE_A_store.bin >> "${DEPLOYDIR}/gen3_bkl.bin.usb"
        cat target/preboot/intermediate/release/bcm_kernel.bin >> "${DEPLOYDIR}/gen3_bkl.bin.usb"
        cat target/preboot/intermediate/release/K1_TEE_B_store.bin >> "${DEPLOYDIR}/gen3_bkl.bin.usb"
        cat target/preboot/intermediate/release/K1_TEE_C_store.bin >> "${DEPLOYDIR}/gen3_bkl.bin.usb"
        cat target/preboot/intermediate/release/K1_TEE_D_store.bin >> "${DEPLOYDIR}/gen3_bkl.bin.usb"

        install -m 0644 target/preboot/intermediate/release/erom.bin "${DEPLOYDIR}/gen3_erom.bin.usb"
        install -m 0644 target/preboot/intermediate/release/boot_monitor.bin "${DEPLOYDIR}/gen3_boot_monitor.bin.usb"
        install -m 0644 target/preboot/intermediate/release/scs_data_param.sign "${DEPLOYDIR}/gen3_scs_param.bin.usb"
        install -m 0644 target/preboot/intermediate/release/sysinit_en.bin "${DEPLOYDIR}/gen3_sysinit.bin.usb"
        install -m 0644 target/preboot/intermediate/release/miniloader_en.bin "${DEPLOYDIR}/gen3_miniloader.bin.usb"

        if [ "${MACHINE}" = "sl1680usb" ]; then
             install -m 0644 target/preboot/intermediate/release/gen3_ddr_phy_fw_0.bin "${DEPLOYDIR}/gen3_ddr_phy_fw_0.bin.usb"
             install -m 0644 target/preboot/intermediate/release/gen3_ddr_phy_fw_1.bin "${DEPLOYDIR}/gen3_ddr_phy_fw_1.bin.usb"
        fi
    else
        if [ "is${CONFIG_GENX_MCU}" = "isy" ]; then
            if [ "${MACHINE}" = "sl2619usb" ] || [ "${MACHINE}" = "sl2619-coralboard-usb" ]; then
                install -m 0644 target/preboot/intermediate/release/key.bin ${DEPLOYDIR}/key.bin
                install -m 0644 target/preboot/intermediate/release/spk_raw.bin ${DEPLOYDIR}/spk.bin
                install -m 0644 target/preboot/intermediate/release/bl_en.bin ${DEPLOYDIR}/m52bl.bin
            else
                install -m 0644 target/preboot/preboot_ksb.bin ${DEPLOYDIR}/preboot.subimg
            fi
            install -m 0644 target/preboot/sysmgr_en.bin ${DEPLOYDIR}/sysmgr.subimg
        else
            install -m 0644 target/preboot/preboot_esmt.bin ${DEPLOYDIR}/preboot.subimg
        fi
    fi
}

addtask deploy before do_package after do_install
