require optee-syna.inc
inherit syna-security

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
                          synasdk-gpu-ta \
                          synasdk-tsp-ta \
                          synasdk-m2m-ta \
                          synasdk-ovp-ta"


DEPENDS:append:platypus = "synasdk-vpu-ta \
                           synasdk-fastlogo-ta \
                           synasdk-ptm-ta \
                           synasdk-gpu-ta \
                           synasdk-tsp-ta \
                           synasdk-m2m-ta \
                           synasdk-ovp-ta"

SYNA_TA_PATH = "${WORKDIR}/${SYNA_SOURCE_PREFIX}/ta_enc"

STAGING_NONARCH_BASELIBDIR = "${STAGING_DIR_HOST}/${nonarch_base_libdir}"

EARLY_SYNA_TA =""
EARLY_SYNA_TA:platypus=" ${STAGING_NONARCH_BASELIBDIR}/optee_armtz/1316a183-894d-43fe-9893-bb946ae103f5.stripped.elf \
                         ${STAGING_NONARCH_BASELIBDIR}/optee_armtz/1316a183-894d-43fe-9893-bb946ae10436.stripped.elf \
                         ${STAGING_NONARCH_BASELIBDIR}/optee_armtz/1316a183-894d-43fe-9893-bb946ae1042d.stripped.elf \
                         ${SYNA_TA_PATH}/libsynap.ta/platypus/A0/1316a183-894d-43fe-9893bb946ae1042f.stripped.elf \
                         ${STAGING_NONARCH_BASELIBDIR}/optee_armtz/1316a183-894d-43fe-9893-bb946ae103f0.stripped.elf \
                         ${STAGING_NONARCH_BASELIBDIR}/optee_armtz/1316a183-894d-43fe-9893-bb946ae103f4.stripped.elf \
"
EARLY_SYNA_TA:dolphin=" ${STAGING_NONARCH_BASELIBDIR}/optee_armtz/1316a183-894d-43fe-9893-bb946ae103f5.stripped.elf \
                        ${STAGING_NONARCH_BASELIBDIR}/optee_armtz/1316a183-894d-43fe-9893-bb946ae10436.stripped.elf \
                        ${STAGING_NONARCH_BASELIBDIR}/optee_armtz/1316a183-894d-43fe-9893-bb946ae1042d.stripped.elf \
                        ${SYNA_TA_PATH}/libsynap.ta/dolphin/A0/genx/1316a183-894d-43fe-9893bb946ae1042f.stripped.elf \
                        ${STAGING_NONARCH_BASELIBDIR}/optee_armtz/1316a183-894d-43fe-9893-bb946ae103f0.stripped.elf \
                        ${STAGING_NONARCH_BASELIBDIR}/optee_armtz/1316a183-894d-43fe-9893-bb946ae103f3.stripped.elf \
                        ${STAGING_NONARCH_BASELIBDIR}/optee_armtz/1316a183-894d-43fe-9893-bb946ae103f4.stripped.elf \
"
SYNA_SMP_TA = " \
                 ${STAGING_NONARCH_BASELIBDIR}/optee_armtz/1316a183-894d-43fe-9893-bb946ae103e8.stripped.elf \
                 ${STAGING_NONARCH_BASELIBDIR}/optee_armtz/1316a183-894d-43fe-9893-bb946ae103e9.stripped.elf \
"
EARLY_SYNA_TA:append = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'smp', '${SYNA_SMP_TA}', '', d)} \
"
EXTRA_OEMAKE += " CFG_TEE_CORE_LOG_LEVEL=1 "
EARLY_TA_CFG = " CFG_EARLY_TA=y EARLY_TA_PATHS="${EARLY_SYNA_TA}""
EARLY_TA_CFG:sl1680spi = ""
EARLY_TA_CFG:sl1620spi = ""
EARLY_TA_CFG:sl1640spi = ""
EARLY_TA_CFG:sl2619xspi = ""
EXTRA_OEMAKE += "${EARLY_TA_CFG}"

do_install:append() {
    # Launch script to generate required configurations (ex. ${syna_chip_rev})
    . ${CONFIG_FILE}
    . ${CHIP_RC_FILE}

    SYNA_KEY_PATH="${STAGING_DIR_NATIVE}/usr/share/syna/keys"
    security_keys_path="${SYNA_KEY_PATH}/${syna_chip_name}/${syna_chip_rev}"
    security_tools_path="${STAGING_DIR_NATIVE}${prefix}/libexec/syna/"

    in_bin=${B}/core/tee-pager_v2.bin
    out_bin=${D}${nonarch_base_libdir}/firmware/tz2_en.bin

    # Prepare tzk_extra.bin
    prod_image_flag=0x00000000
    destination_addr=0x00160000
    ${security_tools_path}/in_extras.py "TZ_KERNEL" ${B}/tzk_extras.bin ${prod_image_flag} ${destination_addr}

    # Generate image
    genx_secure_image "TZ_KERNEL" "ree" ${B}/tzk_extras.bin 0x0 ${in_bin} ${out_bin}
}
