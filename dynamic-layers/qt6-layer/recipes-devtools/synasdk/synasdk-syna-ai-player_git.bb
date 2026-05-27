SUMMARY = "Synaptics QT AI Video Player"
SECTION = "multimedia"
LICENSE = "Apache-2.0"

DEPENDS += "qtbase qtdeclarative glib-2.0 gstreamer1.0 \
            gstreamer1.0-plugins-base gstreamer1.0-plugins-bad udev \
            synasdk-syna-player-framework"
RDEPENDS:${PN} += "qtdeclarative-qmlplugins qt5compat-qmlplugins \
        gstreamer1.0-plugins-base gstreamer1.0-plugins-good \
        gstreamer1.0-plugins-bad udev"

# Add X11 and Wayland specific dependencies based on DISTRO_FEATURES
DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland', '', d)}"

qmldir = "/home/root/demos/qmls/"

SRC_URI = "${SYNA_SRC_DEMOS}"

SRCREV = "${SYNA_SRCREV_DEMOS}"

PV = "${ASTRA_VERSION}+git${SRCPV}"

S = "${WORKDIR}/${SYNA_SOURCE_PREFIX}/application/demos/syna-ai-player"

LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=4158a261ca7f2525513e31ba9c50ae98"

inherit pkgconfig
inherit qt6-qmake

# Add custom QMake configuration based on DISTRO_FEATURES
EXTRA_QMAKEVARS_PRE += "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'CONFIG+=x11', '', d)}"
EXTRA_QMAKEVARS_PRE += "${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'CONFIG+=wayland', '', d)}"

do_install () {
    install -d ${D}${qmldir}
    install -d ${D}${qmldir}/panels
    install -d ${D}${qmldir}/custom_views

    for i in ${S}/qmls/custom_views/*\.qml; do
        file_name=`basename "${i}"`
        install -m 0644 ${i} ${D}${qmldir}/custom_views/${file_name}
    done

    if [ "${MACHINE}" = "sl1620" ]; then
        install -m 0644 ${S}/qmls/sl1620-ai.qml  ${D}${qmldir}/
        install -m 0644 ${S}/qmls/panels/FaceDetection1620.qml ${D}${qmldir}/panels/FaceDetection.qml
        install -m 0644 ${S}/qmls/panels/ObjectDetection1620.qml ${D}${qmldir}/panels/ObjectDetection.qml
        install -m 0644 ${S}/qmls/panels/PoseEstimation1620.qml ${D}${qmldir}/panels/PoseEstimation.qml
        install -m 0644 ${S}/qmls/panels/AIEncoding1620.qml ${D}${qmldir}/panels/AIEncoding.qml
    fi

    if [ "${MACHINE}" = "sl1640" ]; then
        install -m 0644 ${S}/qmls/sl1640-ai.qml  ${D}${qmldir}/
        install -m 0644 ${S}/qmls/panels/FaceDetection.qml ${D}${qmldir}/panels/FaceDetection.qml
        install -m 0644 ${S}/qmls/panels/ObjectDetection.qml ${D}${qmldir}/panels/ObjectDetection.qml
        install -m 0644 ${S}/qmls/panels/PoseEstimation.qml ${D}${qmldir}/panels/PoseEstimation.qml
        install -m 0644 ${S}/qmls/panels/AIEncoding.qml ${D}${qmldir}/panels/AIEncoding.qml
    fi

    if [ "${MACHINE}" = "sl1680" ]; then
        install -m 0644 ${S}/qmls/sl1680-ai.qml  ${D}${qmldir}/
        install -m 0644 ${S}/qmls/panels/FaceDetection.qml ${D}${qmldir}/panels/FaceDetection.qml
        install -m 0644 ${S}/qmls/panels/ObjectDetection.qml ${D}${qmldir}/panels/ObjectDetection.qml
        install -m 0644 ${S}/qmls/panels/PoseEstimation.qml ${D}${qmldir}/panels/PoseEstimation.qml
        install -m 0644 ${S}/qmls/panels/MultiAi.qml ${D}${qmldir}/panels/MultiAi.qml
        install -m 0644 ${S}/qmls/panels/AIEncoding.qml ${D}${qmldir}/panels/AIEncoding.qml
        install -m 0644 ${S}/qmls/panels/SRSlideshow.qml ${D}${qmldir}/panels/SRSlideshow.qml
        install -m 0644 ${S}/qmls/panels/Superres.qml ${D}${qmldir}/panels/Superres.qml
        install -m 0644 ${S}/qmls/panels/FaceRecognition.qml ${D}${qmldir}/panels/FaceRecognition.qml

    fi

    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/build/syna-ai-player ${D}${bindir}/
}

FILES:${PN} = " \
    ${bindir}/syna-ai-player \
    ${qmldir} \
"
