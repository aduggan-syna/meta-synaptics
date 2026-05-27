DESCRIPTION = "BOOTCTRL"
SECTION = "devtools"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=796f2ee9e948e0d76a93a913bdd1b3bd"
PR = "r0"

SRC_URI = "${SYNA_SRC_APPLICATION}"

SRCREV = "${SYNA_SRCREV_APPLICATION}"

PV = "git${SRCPV}"

COMPATIBLE_MACHINE = "syna"

S = "${WORKDIR}/${SYNA_SOURCE_PREFIX}/application/power"

INSANE_SKIP:${PN} = "ldflags"
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

do_compile() {
    ${MAKE} BUILD_DIR=${S} -C ${S}
}

do_install () {
    install -d ${D}${bindir}
    install -m 0755 ${S}/power ${D}${bindir}
}

FILES:${PN} = " \
    ${bindir}/* \
"

FILES_SOLIBSDEV = ""
