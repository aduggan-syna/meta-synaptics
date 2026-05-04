SUMMARY = "DRM Gamma Correction Utility"
DESCRIPTION = "Userspace utility to set gamma correction on DRM CRTCs."
SECTION = "devtools"
LICENSE = "CLOSED"
LICENSE_FLAGS = "Synaptics-EULA"

COMPATIBLE_MACHINE = "syna"

DEPENDS = "libdrm"

S = "${DEPLOY_DIR_IMAGE}/release/synasdk-application-release/application/display"
B = "${WORKDIR}/build"

inherit pkgconfig

do_configure() {
    mkdir -p ${B}
    cp ${S}/gamma_set.c ${B}/
    cp ${S}/Makefile ${B}/
}

do_compile() {
    cd ${B}
    oe_runmake
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${B}/gamma_set ${D}${bindir}/
}

FILES:${PN} = "${bindir}/gamma_set"
