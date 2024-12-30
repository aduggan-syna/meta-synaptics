require optee-syna.inc
require recipes-devtools/synasdk/synasdk-config.inc

SRC_URI += "${SYNA_SRC_TA_ENC}"

SRCREV_taenc = "${SYNA_SRCREV_TA_ENC}"

SRCREV_FORMAT = "taenc"

DEPENDS:append = " \
    synasdk-tools-native \
    synasdk-security-native \
    synasdk-vpu-ta \
"

SYNA_TA_PATH = "${WORKDIR}/${SYNA_SOURCE_PREFIX}/ta_enc"

EARLY_SYNA_TA:myna2 =" ${SYNA_TA_PATH}/libmipi_dsi.ta/myna2/A0/genx/1316a183-894d-43fe-9893-bb946ae1043e.stripped.elf \
"

EARLY_SYNA_TA:platypus=" ${SYNA_TA_PATH}/libfastlogo.ta/platypus/A0/1316a183-894d-43fe-9893-bb946ae103f5.stripped.elf \
                         ${SYNA_TA_PATH}/libgfx_img_linux.ta/platypus/A0/1316a183-894d-43fe-9893-bb946ae10436.stripped.elf \
                         ${SYNA_TA_PATH}/libptm.ta/platypus/A0/1316a183-894d-43fe-9893-bb946ae1042d.stripped.elf \
                         ${SYNA_TA_PATH}/libsynap.ta/platypus/A0/1316a183-894d-43fe-9893bb946ae1042f.stripped.elf \
                         ${STAGING_BASELIBDIR}/optee_armtz/1316a183-894d-43fe-9893-bb946ae103f0.stripped.elf \
"
EARLY_SYNA_TA:dolphin=" ${SYNA_TA_PATH}/libfastlogo.ta/dolphin/A0/genx/1316a183-894d-43fe-9893-bb946ae103f5.stripped.elf \
                        ${SYNA_TA_PATH}/libgfx_img_linux.ta/dolphin/A0/genx/1316a183-894d-43fe-9893-bb946ae10436.stripped.elf \
                        ${SYNA_TA_PATH}/libptm.ta/dolphin/A0/genx/1316a183-894d-43fe-9893-bb946ae1042d.stripped.elf \
                        ${SYNA_TA_PATH}/libsynap.ta/dolphin/A0/genx/1316a183-894d-43fe-9893bb946ae1042f.stripped.elf \
                        ${STAGING_BASELIBDIR}/optee_armtz/1316a183-894d-43fe-9893-bb946ae103f0.stripped.elf \
                        ${SYNA_TA_PATH}/libdhub.ta/dolphin/A0/genx/1316a183-894d-43fe-9893-bb946ae103f3.stripped.elf \
"
EARLY_TA_CFG = " CFG_EARLY_TA=y EARLY_TA_PATHS="${EARLY_SYNA_TA}""
EARLY_TA_CFG:sl1680spi = ""
EXTRA_OEMAKE += "${EARLY_TA_CFG}"

do_install:append() {
    # Launch script to generate required configurations (ex. ${syna_chip_rev})
    . ${CONFIG_FILE}
    . ${CHIP_RC_FILE}

    SYNA_KEY_PATH="${STAGING_DIR_NATIVE}/usr/share/syna/keys"
    security_keys_path="${SYNA_KEY_PATH}/${syna_chip_name}/${syna_chip_rev}"
    security_libexec_path="${STAGING_DIR_NATIVE}/usr/libexec/syna"

    in_bin=${B}/core/tee-pager_v2.bin
    out_bin=${D}${nonarch_base_libdir}/firmware/tz2_en.bin

    # Prepare tzk_extra.bin
    prod_image_flag=0x00000000
    destination_addr=0x00160000
    ${security_libexec_path}/in_extras.py "TZ_KERNEL" ${B}/tzk_extras.bin ${prod_image_flag} ${destination_addr}

    # Generate image
    gen_x_secure_image --chip-name=${syna_chip_name} \
                       --chip-rev=${syna_chip_rev} \
                       --img_type="TZ_KERNEL" \
                       --key_type="ree" \
                       --length=0x0 --extras=${B}/tzk_extras.bin \
                       --workdir-security-tools=${security_libexec_path} \
                       --workdir-security-keys=${security_keys_path} \
                       --in_payload=${in_bin} \
                       --out_store=${out_bin}
}
