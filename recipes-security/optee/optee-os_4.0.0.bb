require optee-os.inc

DEPENDS += "dtc-native"

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
PATCHTOOL = "git"
SRCREV = "2a5b1d1232f582056184367fb58a425ac7478ec6"
SRC_URI += " \
    file://0001-allow-setting-sysroot-for-libgcc-lookup.patch \
    file://0002-core-Define-section-attributes-for-clang.patch \
    file://0003-optee-enable-clang-support.patch \
    file://0004-core-link-add-no-warn-rwx-segments.patch \
    file://0005-ta-pkcs11-disable-TA_FLAG_INSTANCE_KEEP_ALIVE.patch \
    file://0006-lib-extend-newlib-apis.patch \
    file://0007-use-REE-keys-to-sign-AFT-TZK.patch \
    file://0008-plat-syna-add-dolphin-platypus-and-myna2-support.patch \
    file://0009-libutee-add-syna-syscalls.patch \
    file://0010-core-support-berlin-secure-and-non-secure-heap.patch \
    file://0011-sign_encrypt.py-force-to-generate-TA-with-legacy-for.patch \
   "

SYNA_CHIP:myna2 = "myna2"
SYNA_CHIP:dolphin = "dolphin"
SYNA_CHIP:platypus= "platypus"

do_install:append() {
	export security_dir="${S}/security/genx"

	enc_tool=${S}/security/genx/enc.sh
	in_bin=${B}/core/tee-pager_v2.bin
	out_bin=${D}${nonarch_base_libdir}/firmware/tz2_en.bin
	${enc_tool} ${SYNA_CHIP} A0 TZK ${in_bin} ${out_bin}
}
