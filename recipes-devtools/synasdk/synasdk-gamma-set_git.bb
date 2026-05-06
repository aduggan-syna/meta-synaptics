DESCRIPTION = "DRM Gamma Correction Utility"
SECTION = "devtools"
LICENSE = "CLOSED"
LICENSE_FLAGS = "Synaptics-EULA"
PR = "r0"

SRC_URI = "${SYNA_SRC_APPLICATION}"

SRCREV = "${SYNA_SRCREV_APPLICATION}"

PV = "git${SRCPV}"

COMPATIBLE_MACHINE = "myna2|klamath"

DEPENDS = "libdrm"

S = "${WORKDIR}/${SYNA_SOURCE_PREFIX}/application/display"

INSANE_SKIP:${PN} = "ldflags"
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

inherit pkgconfig

do_compile() {
    ${MAKE} BUILD_DIR=${S} -C ${S}
}

do_install () {
    install -d ${D}${bindir}
    install -m 0755 ${S}/gamma_set ${D}${bindir}
}

FILES:${PN} = " \
    ${bindir}/* \
"

FILES_SOLIBSDEV = ""
