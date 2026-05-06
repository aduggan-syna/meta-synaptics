SUMMARY = "Synaptics Settings"
SECTION = "multimedia"
LICENSE = "Apache-2.0"

DEPENDS += "qtbase qtdeclarative glib-2.0  gstreamer1.0-plugins-base \
            synasdk-syna-settings-framework"
# Add X11 and Wayland specific dependencies based on DISTRO_FEATURES
DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland', '', d)}"

RDEPENDS_${PN} += "qtdeclarative-qmlplugins qtgraphicaleffects-qmlplugins"

SRC_URI = "${SYNA_SRC_DEMOS}"

SRCREV = "${SYNA_SRCREV_DEMOS}"

PV = "${ASTRA_VERSION}+git${SRCPV}"

S = "${WORKDIR}/${SYNA_SOURCE_PREFIX}/application/demos/qt5/syna-settings"

LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=4158a261ca7f2525513e31ba9c50ae98"

inherit pkgconfig
inherit qmake5

# Add custom QMake configuration based on DISTRO_FEATURES
EXTRA_QMAKEVARS_PRE += "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'CONFIG+=x11', '', d)}"
EXTRA_QMAKEVARS_PRE += "${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'CONFIG+=wayland', '', d)}"

do_install () {
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/build/syna-settings ${D}${bindir}/
}

FILES:${PN} = " \
    ${bindir}/syna-settings \
"
