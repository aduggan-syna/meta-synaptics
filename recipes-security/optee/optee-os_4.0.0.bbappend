require optee-syna.inc

SRC_URI += "${SYNA_SRC_TA_ENC}"

EARLY_SYNA_TA:myna2 =" ${WORKDIR}/release/ta_enc/libmipi_dsi.ta/myna2/A0/genx/1316a183-894d-43fe-9893-bb946ae1043e.stripped.elf \
"

EARLY_SYNA_TA:platypus=" ${WORKDIR}/release/ta_enc/libfastlogo.ta/platypus/A0/1316a183-894d-43fe-9893-bb946ae103f5.stripped.elf \
                         ${WORKDIR}/release/ta_enc/libgfx_img_linux.ta/platypus/A0/1316a183-894d-43fe-9893-bb946ae10436.stripped.elf \
                         ${WORKDIR}/release/ta_enc/libptm.ta/platypus/A0/1316a183-894d-43fe-9893-bb946ae1042d.stripped.elf \
                         ${WORKDIR}/release/ta_enc/libsynap.ta/platypus/A0/1316a183-894d-43fe-9893bb946ae1042f.stripped.elf \
                         ${WORKDIR}/release/ta_enc/libvmeta.ta/platypus/A0/1316a183-894d-43fe-9893-bb946ae103f0.stripped.elf \
"
EARLY_SYNA_TA:dolphin=" ${WORKDIR}/release/ta_enc/libfastlogo.ta/dolphin/A0/genx/1316a183-894d-43fe-9893-bb946ae103f5.stripped.elf \
                         ${WORKDIR}/release/ta_enc/libgfx_img_linux.ta/dolphin/A0/genx/1316a183-894d-43fe-9893-bb946ae10436.stripped.elf \
                         ${WORKDIR}/release/ta_enc/libptm.ta/dolphin/A0/genx/1316a183-894d-43fe-9893-bb946ae1042d.stripped.elf \
                         ${WORKDIR}/release/ta_enc/libsynap.ta/dolphin/A0/genx/1316a183-894d-43fe-9893bb946ae1042f.stripped.elf \
                         ${WORKDIR}/release/ta_enc/libvmeta.ta/dolphin/A0/genx/1316a183-894d-43fe-9893-bb946ae103f0.stripped.elf \
                         ${WORKDIR}/release/ta_enc/libdhub.ta/dolphin/A0/genx/1316a183-894d-43fe-9893-bb946ae103f3.stripped.elf \
"
EXTRA_OEMAKE += " CFG_EARLY_TA=y EARLY_TA_PATHS="${EARLY_SYNA_TA}""

do_install:append() {
	export security_dir="${S}/security/genx"

	enc_tool=${S}/security/genx/enc.sh
	in_bin=${B}/core/tee-pager_v2.bin
	out_bin=${D}${nonarch_base_libdir}/firmware/tz2_en.bin
	${enc_tool} ${SYNA_CHIP} A0 TZK ${in_bin} ${out_bin}
}
