SUMMARY = "NNStreamer, Stream Pipeline Paradigm for Nerual Network Applications"
DESCRIPTION = "NNStreamer is a GStreamer plugin allowing to construct neural network applications with stream pipeline paradigm."
SECTION = "AI"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "\
                file://LICENSE;md5=c25e5c1949624d71896127788f1ba590 \
                file://debian/copyright;md5=0462ef8fa89a1f53f2e65e74940519ef \
                "

DEPENDS = "\
            orc-native \
            glib-2.0 \
            gstreamer1.0 \
            gstreamer1.0-plugins-base \
            python3 \
            python3-numpy \
            gtest \
            json-glib \
            "
DEPENDS += "tensorflow-lite"

SRC_URI = "\
        git://github.com/nnstreamer/nnstreamer.git;branch=main;protocol=https \
        "
SRC_URI += "file://0001-tflite-build-fix.patch"

PV = "2.1.0+git${SRCPV}"
SRCREV = "${AUTOREV}"

S = "${WORKDIR}/git"

inherit meson pkgconfig

EXTRA_OEMESON += "\
                -Dskip-tflite-flatbuf-check=true \
                -Denable-pbtxt-converter=false \
                "

PACKAGECONFIG ??= "\
                ${@bb.utils.contains('DISTRO_FEATURES','opencv','opencv','',d)} \
                "

do_install:append() {
   cd ${D}/${libdir}
   ln -sf ./gstreamer-1.0/libnnstreamer.so ./libnnstreamer.so
}
INSANE_SKIP:${PN} += "dev-so"

FILES:${PN} += "\
            ${libdir}/*.so \
            ${libdir}/gstreamer-1.0/*.so \
            ${libdir}/nnstreamer/decoders/* \
            ${libdir}/nnstreamer/filters/* \
            ${sysconfdir}/nnstreamer.ini \
            "

PACKAGES += "${PN}-tensorflow-lite"

FILES:${PN}-tensorflow-lite += "\
                ${libdir}/nnstreamer/filters/libnnstreamer_filter_tensorflow2-lite.so \
                "

RPROVIDES:${PN}-tensorflow-lite = "${libdir}/nnstreamer/filters/libnnstreamer_filter_tensorflow2-lite.so"

RDEPENDS:${PN} = "\
                glib-2.0 \
                json-glib \
                gstreamer1.0 \
                gstreamer1.0-plugins-base \
                python3 \
                python3-numpy \
                python3-math \
                "

FILES:${PN}-dev = "\
                ${includedir}/nnstreamer/* \
                ${libdir}/*.a \
                ${libdir}/pkgconfig/*.pc \
                "
