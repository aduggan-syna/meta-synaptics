SUMMARY = "Synaptics QT SR Slideshow application"
SECTION = "multimedia"
LICENSE = "Apache-2.0"

DEPENDS += "qtbase qtmultimedia qtdeclarative glib-2.0 gstreamer1.0 \
            gstreamer1.0-plugins-base gstreamer1.0-plugins-bad \
            synasdk-syna-player-framework"
RDEPENDS:${PN} += "qtdeclarative-qmlplugins qt5compat-qmlplugins \
        gstreamer1.0-plugins-base gstreamer1.0-plugins-good \
        gstreamer1.0-plugins-bad"

SRC_URI = "${SYNA_SRC_DEMOS}"

SRCREV = "${SYNA_SRCREV_DEMOS}"

PV = "${ASTRA_VERSION}+git${SRCPV}"

S = "${WORKDIR}/${SYNA_SOURCE_PREFIX}/application/demos/superres-slideshow"

LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=4158a261ca7f2525513e31ba9c50ae98"

inherit pkgconfig
inherit qt6-qmake

do_install () {
    install -d ${D}${bindir}
    install -d ${D}/home/root/demos/configs
    install -m 0755 ${WORKDIR}/build/superres-slideshow ${D}${bindir}/
    install -m 0644 ${S}/SlideshowConfig.qml  ${D}/home/root/demos/configs/SlideshowConfig.qml
}

FILES:${PN} = " \
    ${bindir}/superres-slideshow \
    /home/root/demos/configs/ \
"
