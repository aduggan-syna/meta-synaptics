DESCRIPTION = "V4L2 ISP"
SECTION = "multimedia"
LICENSE = "CLOSED"
LICENSE_FLAGS = "Synaptics-EULA"
PR = "r0"

DEPENDS += "qtbase qtdeclarative qtmultimedia qtxmlpatterns libpng jpeg udev python3"
RDEPENDS_${PN} += "qtdeclarative-qmlplugins qtgraphicaleffects-qmlplugins"

DEPENDS += " \
		   gstreamer1.0-meta-base \
		   gstreamer1.0-plugins-base \
		   gstreamer1.0-plugins-good \
		   gstreamer1.0-plugins-bad \
		   gstreamer1.0-plugins-ugly \
		   ffmpeg \
		   "


SRC_URI = "${SYNA_SRC_V4L2ISP}"

SRCREV = "${SYNA_SRCREV_V4L2ISP}"

PV = "git${SRCPV}"

COMPATIBLE_MACHINE = "dolphin"

S = "${WORKDIR}/${SYNA_SOURCE_PREFIX}/application/v4l2isp/qt_camera_app"

inherit qmake5

FILES:${PN} = " \
    ${libdir}/*.so \
    ${libdir}/*.a \
    ${bindir}/* \
"

INSANE_SKIP:${PN} += "dev-so"
FILES_SOLIBSDEV = ""

do_patch() {
	export SDKTARGETSYSROOT="${PKG_CONFIG_SYSROOT_DIR}"
}

do_install () {
    install -d ${D}${bindir}
    install -m 0755 ${B}/bin/camera ${D}${bindir}
    install -m 0755 ${B}/bin/main.qml ${D}${bindir}
}
