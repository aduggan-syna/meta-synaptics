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

PACKAGES =+ "\
    gstreamer1.0-plugins-syna-videoconvertscale \
    gstreamer1.0-plugins-syna-compositor \
    gstreamer1.0-plugins-syna-synap \
    gstreamer1.0-plugins-syna-ai \
    gstreamer1.0-plugins-syna-synaoverlay \
"

FILES:gstreamer1.0-plugins-syna-videoconvertscale = "${libdir}/gstreamer-1.0/libgstsynavideoconvertscale.so"
FILES:gstreamer1.0-plugins-syna-compositor        = "${libdir}/gstreamer-1.0/libgstsynacompositor.so"
FILES:gstreamer1.0-plugins-syna-synap             = "${libdir}/gstreamer-1.0/libgstsynap.so"
FILES:gstreamer1.0-plugins-syna-synaoverlay       = "${libdir}/gstreamer-1.0/libgstsynaoverlay.so"

FILES:gstreamer1.0-plugins-syna-ai = " \
    ${datadir}/gst-ai \
    ${datadir}/gst-ai/* \
    ${bindir}/gst-ai \
"
RDEPENDS:gstreamer1.0-plugins-syna-ai += "gstreamer1.0-plugins-syna-synap"

do_install:append() {
        install -m 0755 -D ${WORKDIR}/ic.json ${D}${datadir}/gst-ai/ic.json
}