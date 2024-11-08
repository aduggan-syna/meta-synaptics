require optee-syna.inc

SRC_URI += "${SYNA_SRC_TA_ENC}"

SRCREV_taenc = "${SYNA_SRCREV_TA_ENC}"

SRCREV_FORMAT = "taenc"

SYNA_TA_PATH = "${WORKDIR}/${SYNA_SOURCE_PREFIX}/ta_enc"

EARLY_SYNA_TA:myna2 =" ${SYNA_TA_PATH}/libmipi_dsi.ta/myna2/A0/genx/1316a183-894d-43fe-9893-bb946ae1043e.stripped.elf \
"

EARLY_SYNA_TA:platypus=" ${SYNA_TA_PATH}/libfastlogo.ta/platypus/A0/1316a183-894d-43fe-9893-bb946ae103f5.stripped.elf \
                         ${SYNA_TA_PATH}/libgfx_img_linux.ta/platypus/A0/1316a183-894d-43fe-9893-bb946ae10436.stripped.elf \
                         ${SYNA_TA_PATH}/libptm.ta/platypus/A0/1316a183-894d-43fe-9893-bb946ae1042d.stripped.elf \
                         ${SYNA_TA_PATH}/libsynap.ta/platypus/A0/1316a183-894d-43fe-9893bb946ae1042f.stripped.elf \
                         ${SYNA_TA_PATH}/libvmeta.ta/platypus/A0/1316a183-894d-43fe-9893-bb946ae103f0.stripped.elf \
"
EARLY_SYNA_TA:dolphin=" ${SYNA_TA_PATH}/libfastlogo.ta/dolphin/A0/genx/1316a183-894d-43fe-9893-bb946ae103f5.stripped.elf \
                        ${SYNA_TA_PATH}/libgfx_img_linux.ta/dolphin/A0/genx/1316a183-894d-43fe-9893-bb946ae10436.stripped.elf \
                        ${SYNA_TA_PATH}/libptm.ta/dolphin/A0/genx/1316a183-894d-43fe-9893-bb946ae1042d.stripped.elf \
                        ${SYNA_TA_PATH}/libsynap.ta/dolphin/A0/genx/1316a183-894d-43fe-9893bb946ae1042f.stripped.elf \
                        ${SYNA_TA_PATH}/libvmeta.ta/dolphin/A0/genx/1316a183-894d-43fe-9893-bb946ae103f0.stripped.elf \
                        ${SYNA_TA_PATH}/libdhub.ta/dolphin/A0/genx/1316a183-894d-43fe-9893-bb946ae103f3.stripped.elf \
"
EXTRA_OEMAKE += " CFG_EARLY_TA=y EARLY_TA_PATHS="${EARLY_SYNA_TA}""

do_install:append() {
	export security_dir="${S}/security/genx"

	enc_tool=${S}/security/genx/enc.sh
	in_bin=${B}/core/tee-pager_v2.bin
	out_bin=${D}${nonarch_base_libdir}/firmware/tz2_en.bin
	${enc_tool} ${SYNA_CHIP} A0 TZK ${in_bin} ${out_bin}
}
