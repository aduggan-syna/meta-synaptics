require optee-syna.inc
require recipes-devtools/synasdk/synasdk-config.inc

SRC_URI:append: = "${SYNA_SRC_TEE}"
SRC_URI:append:platypus = "${SYNA_SRC_TA_ENC}"
SRC_URI:append:dolphin = "${SYNA_SRC_TA_ENC}"

SRCREV_taenc = "${SYNA_SRCREV_TA_ENC}"

SRCREV_FORMAT = "taenc"

DEPENDS:append = " \
    synasdk-tools-native \
    synasdk-security-native \
"

DEPENDS:append:dolphin = "synasdk-vpu-ta \
                          synasdk-fastlogo-ta  \
                          synasdk-ptm-ta \
                          synasdk-dhub-ta \
                          synasdk-gpu-ta"

DEPENDS:append:platypus = "synasdk-vpu-ta \
                           synasdk-fastlogo-ta \
                           synasdk-ptm-ta \
                           synasdk-gpu-ta"

SYNA_TA_PATH = "${WORKDIR}/${SYNA_SOURCE_PREFIX}/ta_enc"
SYNA_TEE_PATH = "${WORKDIR}/${SYNA_SOURCE_PREFIX}/tee"

STAGING_NONARCH_BASELIBDIR = "${STAGING_DIR_HOST}/${nonarch_base_libdir}"

EARLY_SYNA_TA:platypus=" ${STAGING_NONARCH_BASELIBDIR}/optee_armtz/1316a183-894d-43fe-9893-bb946ae103f5.stripped.elf \
                         ${STAGING_NONARCH_BASELIBDIR}/optee_armtz/1316a183-894d-43fe-9893-bb946ae10436.stripped.elf \
                         ${STAGING_NONARCH_BASELIBDIR}/optee_armtz/1316a183-894d-43fe-9893-bb946ae1042d.stripped.elf \
                         ${SYNA_TA_PATH}/libsynap.ta/platypus/A0/1316a183-894d-43fe-9893bb946ae1042f.stripped.elf \
                         ${STAGING_NONARCH_BASELIBDIR}/optee_armtz/1316a183-894d-43fe-9893-bb946ae103f0.stripped.elf \
"
EARLY_SYNA_TA:dolphin=" ${STAGING_NONARCH_BASELIBDIR}/optee_armtz/1316a183-894d-43fe-9893-bb946ae103f5.stripped.elf \
                        ${STAGING_NONARCH_BASELIBDIR}/optee_armtz/1316a183-894d-43fe-9893-bb946ae10436.stripped.elf \
                        ${STAGING_NONARCH_BASELIBDIR}/optee_armtz/1316a183-894d-43fe-9893-bb946ae1042d.stripped.elf \
                        ${SYNA_TA_PATH}/libsynap.ta/dolphin/A0/genx/1316a183-894d-43fe-9893bb946ae1042f.stripped.elf \
                        ${STAGING_NONARCH_BASELIBDIR}/optee_armtz/1316a183-894d-43fe-9893-bb946ae103f0.stripped.elf \
                        ${STAGING_NONARCH_BASELIBDIR}/optee_armtz/1316a183-894d-43fe-9893-bb946ae103f3.stripped.elf \
"
EARLY_TA_CFG = " CFG_EARLY_TA=y EARLY_TA_PATHS="${EARLY_SYNA_TA}""
EARLY_TA_CFG:sl1680spi = ""
EARLY_TA_CFG:sl1620spi = ""
EARLY_TA_CFG:sl1640spi = ""
EXTRA_OEMAKE += "${EARLY_TA_CFG}"

do_compile:prepend() {
    . ${CONFIG_FILE}
    . ${CHIP_RC_FILE}

    if [ "is${syna_chip_name}" == "isdolphin" ];then
        mr_file="${SYNA_TEE_PATH}/tee/products/${syna_chip_name}/genx/${CONFIG_TZK_MEM_LAYOUT}/mr_config"
    else
        mr_file="${SYNA_TEE_PATH}/tee/products/${syna_chip_name}/${CONFIG_TZK_MEM_LAYOUT}/mr_config"
    fi
    bbnote "MR file: ${mr_file}"

    if [ ! -r "${mr_file}" ]; then
        bbfatal "Missing or unreadable MR file: ${mr_file}"
    fi

    spd_base="$(awk '/\<Secure\>/{print $2}' ${mr_file})"
    spd_size="$(awk '/\<Secure\>/{print $3}' ${mr_file})"

    if [ -z "${spd_base}" ] || [ -z "${spd_size}" ]; then
        EXTRA_OEMAKE="${EXTRA_OEMAKE} CFG_SECURE_DATA_PATH=n"
    else
        EXTRA_OEMAKE="${EXTRA_OEMAKE} CFG_SECURE_DATA_PATH=y CFG_TEE_SDP_MEM_BASE=${spd_base} CFG_TEE_SDP_MEM_SIZE=${spd_size}"
    fi
    export EXTRA_OEMAKE
}

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

    if [ "is${CONFIG_GENX_MCU}" = "isy" ]; then
        tool_version=genx_v3
    else
        tool_version=genx
    fi

    # Generate image
    gen_x_secure_image --chip-name=${syna_chip_name} \
                       --chip-rev=${syna_chip_rev} \
                       --img_type="TZ_KERNEL" \
                       --key_type="ree" \
                       --length=0x0 --extras=${B}/tzk_extras.bin \
                       --workdir-security-tools=${security_libexec_path} \
                       --workdir-security-keys=${security_keys_path} \
                       --tool-version=${tool_version} \
                       --in_payload=${in_bin} \
                       --out_store=${out_bin}
}
