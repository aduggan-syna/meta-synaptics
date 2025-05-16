SUMMARY = "Synaptics QT Audio Classification"
SECTION = "multimedia"
LICENSE = "Apache-2.0"

DEPENDS += "qtbase qtmultimedia qtdeclarative glib-2.0 gstreamer1.0 \
            gstreamer1.0-plugins-base gstreamer1.0-plugins-bad"
RDEPENDS_${PN} += "qtdeclarative-qmlplugins qtgraphicaleffects-qmlplugins \
        gstreamer1.0-plugins-base gstreamer1.0-plugins-good \
        gstreamer1.0-plugins-bad"

SRC_URI = "${SYNA_SRC_DEMOS}"

SRCREV = "${SYNA_SRCREV_DEMOS}"

PV = "${ASTRA_VERSION}+git${SRCPV}"

S = "${WORKDIR}/${SYNA_SOURCE_PREFIX}/application/demos/audio-classification"

LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=4158a261ca7f2525513e31ba9c50ae98"

inherit pkgconfig
inherit qmake5

do_install () {
    install -d ${D}${bindir}
    install -d ${D}/usr/share/synap/models/audio_classification/yamnet/model
    install -d ${D}/home/root/demos/configs

    install -m 0644 ${S}/qmls/AudioClassificationConfig.qml ${D}/home/root/demos/configs/
    install -m 0644 ${S}/res/label/info.txt ${D}/usr/share/synap/models/audio_classification/yamnet/
    install -m 0644 ${S}/res/model/model.tflite ${D}/usr/share/synap/models/audio_classification/yamnet/model/
    install -m 0755 ${WORKDIR}/build/audio-classification ${D}${bindir}/
}

FILES:${PN} = " \
    ${bindir}/audio-classification \
    /usr/share/synap/models/audio_classification/yamnet \
    /home/root/demos/configs \
"
