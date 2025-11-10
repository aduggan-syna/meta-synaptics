SUMMARY = "GStreamer Syna Plugins"
DESCRIPTION = "Synaptics Proprietary GStreamer Plugins"
SECTION = "multimedia"
LICENSE = "CLOSED"
LICENSE_FLAGS = "Synaptics-EULA"

require recipes-multimedia/gstreamer/gstreamer1.0-plugins-common.inc

inherit meson pkgconfig
COMMON_DEPS = "gstreamer1.0-plugins-base synasdk-syna-compositor json-glib opencv"

DEPENDS:append = " ${COMMON_DEPS} synasdk-synap-runtime"

EXTRA_OEMESON += " \
   -Dexamples=enabled \
"
EXTRA_OEMESON += " -Dsynap_dep=synap-runtime"

SRC_URI = "${SYNA_SRC_GSTREAMER} \
           file://ic.json \
          "

SRCREV = "${SYNA_SRCREV_GSTREAMER}"

PV = "1.22.12+git${SRCPV}"

S = "${WORKDIR}/${SYNA_SOURCE_PREFIX}/application/gstreamer-plugins-syna"

FILES:${PN} = "${datadir}/gst-ai"
do_install:append() {
        install -m 0755 -D ${WORKDIR}/ic.json ${D}${datadir}/gst-ai/ic.json
}
