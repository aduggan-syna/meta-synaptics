DESCRIPTION = "V4L2 ISP"
SECTION = "multimedia"
LICENSE = "CLOSED"
LICENSE_FLAGS = "Synaptics-EULA"
PR = "r0"

SRC_URI = "${SYNA_SRC_V4L2ISP}"

SRCREV = "${SYNA_SRCREV_V4L2ISP}"

PV = "git${SRCPV}"

COMPATIBLE_MACHINE = "dolphin"

DEPENDS += "qtbase qtdeclarative qtmultimedia qtxmlpatterns libpng jpeg udev python3"
RDEPENDS_${PN} += "qtdeclarative-qmlplugins qtgraphicaleffects-qmlplugins"

S = "${WORKDIR}/${SYNA_SOURCE_PREFIX}/application/v4l2isp/qt_camera_app"

inherit qmake5

FILES:${PN} = " \
    ${libdir}/*.so \
    ${libdir}/*.a \
    ${bindir}/* \
"

INSANE_SKIP:${PN} += "dev-so"
FILES_SOLIBSDEV = ""

do_install () {
    install -d ${D}${bindir}
    install -m 0755 ${S}/main.qml ${D}${bindir}
    install -m 0755 ${B}/bin/camera ${D}${bindir}
}
