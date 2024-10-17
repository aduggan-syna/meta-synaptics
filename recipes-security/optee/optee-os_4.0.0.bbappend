require optee-syna.inc

do_install:append() {
	export security_dir="${S}/security/genx"

	enc_tool=${S}/security/genx/enc.sh
	in_bin=${B}/core/tee-pager_v2.bin
	out_bin=${D}${nonarch_base_libdir}/firmware/tz2_en.bin
	${enc_tool} ${SYNA_CHIP} A0 TZK ${in_bin} ${out_bin}
}
