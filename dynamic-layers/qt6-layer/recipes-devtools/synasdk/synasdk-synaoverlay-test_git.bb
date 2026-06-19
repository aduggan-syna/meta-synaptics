SUMMARY = "Synaptics Opencv Gstsreamer overlay plugin test app"
SECTION = "multimedia"
LICENSE = "Apache-2.0"

DEPENDS += "qtbase qtdeclarative glib-2.0 gstreamer1.0 \
            gstreamer1.0-plugins-base gstreamer1.0-plugins-bad udev \
            synasdk-syna-player-framework qtmultimedia opencv"
RDEPENDS_${PN} += "qtdeclarative-qmlplugins qtgraphicaleffects-qmlplugins \
        gstreamer1.0-plugins-base gstreamer1.0-plugins-good \
        gstreamer1.0-plugins-bad udev opencv"

SRC_URI = "${SYNA_SRC_DEMOS}"

SRCREV = "${SYNA_SRCREV_DEMOS}"

PV = "${ASTRA_VERSION}+git${SRCPV}"

S = "${WORKDIR}/${SYNA_SOURCE_PREFIX}/application/demos/synaoverlay-test"

LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=4158a261ca7f2525513e31ba9c50ae98"

inherit pkgconfig
inherit qt6-qmake

# Ensure qmake exposes Qt OpenGL Widgets headers such as QOpenGLWidget.
EXTRA_QMAKEVARS_PRE += "QT+=openglwidgets"

do_install () {
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/build/synaoverlay-test ${D}${bindir}/
}

FILES:${PN} = " \
    ${bindir}/synaoverlay-test \
"
