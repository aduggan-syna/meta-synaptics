DESCRIPTION = "Synaptics SDK tools"
SECTION = "devtools"
LICENSE = "CLOSED"
LICENSE_FLAGS = "Synaptics-EULA"

PR = "r1"

inherit native

require synasdk-build.inc

SRCREV_FORMAT = "build"

PV = "${ASTRA_VERSION}+git${SRCPV}"

DEPENDS += " openssl-native synasdk-config-native genx-img-py-native"

TOOL_NAMES = "parse_pt crc mkbootimg mkyaffs2img parse_pt_emmc gen_uniimg gen_subimg_info genimg parse_pt_xspi"

do_compile() {
    cd ${S}/build/tools/src/executables

    for i in ${TOOL_NAMES}; do
        make -C $i all OBJDIR=${B}/$i
        if [ $? -ne 0 ]; then
            echo 'make failed for tools (${i})!'
            exit 1
        fi
    done
}

do_install () {

    # Tools
    install -d ${D}${bindir}

    install -m 0755 ${S}/build/tools/bin/gen_secure_image ${D}${bindir}/gen_secure_image
    install -m 0755 ${S}/build/tools/bin/gen_x_secure_image ${D}${bindir}/gen_x_secure_image
    install -m 0755 ${S}/build/tools/bin/mk_nandblock0_image ${D}${bindir}/mk_nandblock0_image
    install -m 0755 ${S}/build/tools/bin/klamath/mk_nandblock0_image ${D}${bindir}/klamath-mk_nandblock0_image

    install -d ${D}${prefix}/libexec
    install -d ${D}${prefix}/libexec/syna

    install -m 0755 ${S}/build/tools/bin/in_extras.py ${D}${prefix}/libexec/syna/in_extras.py
    install -m 0755 ${S}/build/tools/bin/crc_calc.py ${D}${prefix}/libexec/syna/crc_calc.py
    install -m 0755 ${S}/build/tools/bin/gen_bg_crc64 ${D}${prefix}/libexec/syna/gen_bg_crc64

    install -m 0755 ${S}/build/tools/lib/sec_tools/bin/sign_image_v4 ${D}${prefix}/libexec/syna/sign_image_v4
    install -m 0755 ${S}/build/tools/lib/sec_tools/bin/genx_img ${D}${prefix}/libexec/syna/genx_img
    install -m 0755 ${S}/build/tools/lib/sec_tools/bin/genx_img_v3 ${D}${prefix}/libexec/syna/genx_img_v3

    if [ "is${CONFIG_GENX_IMG_PY}" = "isy" ]; then
        genx_img_py_staged="${STAGING_DIR_NATIVE}/usr/libexec/syna-build/genx_img_py"

        if [ ! -x "${genx_img_py_staged}" ]; then
            echo "ERROR: CONFIG_GENX_IMG_PY=y but staged genx_img_py missing: ${genx_img_py_staged}"
            exit 1
        fi

        install -m 0755 "${genx_img_py_staged}" ${D}${prefix}/libexec/syna/genx_img_py
    else
        bbnote "CONFIG_GENX_IMG_PY is not 'y' (got '${CONFIG_GENX_IMG_PY}'); using legacy genx_img as configured."
    fi

    if [ "is${CONFIG_GENX_IMG_V3_PY}" = "isy" ]; then
        genx_img_v3_py_staged="${STAGING_DIR_NATIVE}/usr/libexec/syna-build/genx_img_v3_py"

        if [ ! -x "${genx_img_v3_py_staged}" ]; then
            echo "ERROR: CONFIG_GENX_IMG_V3_PY=y but staged genx_img_v3_py missing: ${genx_img_v3_py_staged}"
            exit 1
        fi

        install -m 0755 "${genx_img_v3_py_staged}" ${D}${prefix}/libexec/syna/genx_img_v3_py
    else
        bbnote "CONFIG_GENX_IMG_V3_PY is not 'y' (got '${CONFIG_GENX_IMG_V3_PY}'); using legacy genx_img_v3 as configured."
    fi

    for file in ${TOOL_NAMES}
    do
        install -m 0755 ${B}/$file/$file ${D}${bindir}
    done

    install -m 0755 ${S}/build/tools/bin/prepend_image_info.sh ${D}${bindir}
}

PACKAGES = " \
    ${PN} \
"

FILES:${PN} = " \
    ${bindir}/* \
    ${prefix}/libexec/* \
"
