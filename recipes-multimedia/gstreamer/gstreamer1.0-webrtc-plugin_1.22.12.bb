SUMMARY = "GStreamer WebRTC sink"
HOMEPAGE = "https://gitlab.freedesktop.org/gstreamer/gst-plugins-rs/-/tree/main/net/webrtc"

LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE-MPL-2.0;md5=815ca599c9df247a0c7f619bab123dad"


SRC_URI = "git://gitlab.freedesktop.org/gstreamer/gst-plugins-rs.git;protocol=https;nobranch=1 \
           file://vendor.tar.xz \
           "

# Using gstreamer tag 1.22.8 from gst-plugin-rs
SRCREV = "a84bbc66f30573b62871db163c48afef75adf6ec"

S = "${WORKDIR}/git"

rootdir = "/home/root"

inherit syna_cargo

DEPENDS += " \
    gstreamer1.0 \
    gstreamer1.0-plugins-base \
    gstreamer1.0-plugins-bad \
    gstreamer1.0-plugins-good \
    libvpx \
    libopus \
    glib-2.0 \
    pkgconfig-native \
"

CARGO_HOME = "${WORKDIR}/cargo_home"

do_configure:prepend() {
    mkdir -p ${CARGO_HOME}/bitbake
    cp -r ${S}/../vendor/* ${CARGO_HOME}/bitbake
}

do_install:append() {

    install -d ${D}${libdir}/gstreamer-1.0
    install -m 0755 ${B}/target/aarch64-poky-linux-gnu/release/*.so ${D}${libdir}/gstreamer-1.0/

    install -d ${D}${rootdir}/demos/webrtc
    cp ${S}/net/webrtc/www/* ${D}${rootdir}/demos/webrtc/

}

FILES:${PN} += " \
                 ${libdir}/gstreamer-1.0/*.so \
                 ${rootdir}/demos/webrtc/* \
"
