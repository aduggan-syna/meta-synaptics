SUMMARY = "Synaptics QT Face Recognition App"
SECTION = "multimedia"
LICENSE = "Apache-2.0"

DEPENDS += "qtbase qtdeclarative glib-2.0 gstreamer1.0 \
            gstreamer1.0-plugins-base gstreamer1.0-plugins-bad udev \
            synasdk-syna-player-framework qtmultimedia opencv"
RDEPENDS:${PN} += "qtdeclarative-qmlplugins qt5compat-qmlplugins \
        gstreamer1.0-plugins-base gstreamer1.0-plugins-good \
        gstreamer1.0-plugins-bad udev opencv"

SRC_URI = "${SYNA_SRC_DEMOS}"

SRCREV = "${SYNA_SRCREV_DEMOS}"

PV = "${ASTRA_VERSION}+git${SRCPV}"

S = "${WORKDIR}/${SYNA_SOURCE_PREFIX}/application/demos/face-recognition"

LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=4158a261ca7f2525513e31ba9c50ae98"

inherit pkgconfig
inherit qt6-qmake

do_install () {
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/build/face-recognition ${D}${bindir}/
}

FILES:${PN} = " \
    ${bindir}/face-recognition \
"
