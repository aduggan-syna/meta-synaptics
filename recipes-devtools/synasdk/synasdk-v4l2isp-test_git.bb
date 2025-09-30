DESCRIPTION = "V4L2 ISP"
SECTION = "multimedia"
LICENSE = "CLOSED"
LICENSE_FLAGS = "Synaptics-EULA"
PR = "r0"

SRC_URI = "${SYNA_SRC_V4L2ISP}"

SRCREV = "${SYNA_SRCREV_V4L2ISP}"

PV = "git${SRCPV}"

COMPATIBLE_MACHINE = "klamath"

DEPENDS = "wayland libdrm"

S = "${WORKDIR}/${SYNA_SOURCE_PREFIX}/application/v4l2isp/isp_video_test"

inherit pkgconfig cmake

FILES:${PN} = " \
    ${libdir}/*.so \
    ${bindir}/* \
"

INSANE_SKIP:${PN} += "dev-so"
FILES_SOLIBSDEV = ""

do_install:append() {

    if [ "${libdir}" != "${nonarch_libdir}" ]; then
        install -d ${D}${libdir}
        mv ${D}${nonarch_libdir}/* ${D}${libdir}/
        rm -rf ${D}${nonarch_libdir}
    fi
}
