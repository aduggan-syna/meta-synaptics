SUMMARY = "Syna Settings Framework and test"
DESCRIPTION = "A library to provide interface for settings"
SECTION = "multimedia"
LICENSE = "Apache-2.0"

DEPENDS = ""

SRC_URI = "${SYNA_SRC_DEMOS}"
SRCREV = "${SYNA_SRCREV_DEMOS}"
PV = "${ASTRA_VERSION}+git${SRCPV}"
S = "${WORKDIR}/${SYNA_SOURCE_PREFIX}/application/demos/framework/syna-settings-framework"

LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=4158a261ca7f2525513e31ba9c50ae98"

rootdir = "/home/root"

COMPATIBLE_MACHINE = "syna"

inherit meson pkgconfig

do_install () {
    install -d ${D}${libdir}
    install -m 0644 ${WORKDIR}/build/libsyna-settings-framework.so  ${D}${libdir}/libsyna-settings-framework.so

    cd ${D}${libdir}
    ln -s libsyna-settings-framework.so libsyna-settings-framework.so.1

    install -d ${D}${includedir}
    install -m 644 ${S}/*.h ${D}${includedir}

    install -d ${D}${libdir}/pkgconfig
    sed 's/@@PV@@/${PV}/g' ${S}/syna-settings-framework.pc.in > ${S}/syna-settings-framework.pc
    install -m 0644 ${S}/syna-settings-framework.pc ${D}${libdir}/pkgconfig/syna-settings-framework.pc
}

FILES:${PN} = " \
    ${libdir}/libsyna-settings-framework.so \
    ${libdir}/libsyna-settings-framework.so.1 \
"

SOLIBS = ".so"
FILES_SOLIBSDEV = ""

FILES:${PN}-dev = " \
    ${includedir} \
    ${libdir}/pkgconfig/syna-settings-framework.pc \
"
INSANE_SKIP:${PN} += "ldflags"

