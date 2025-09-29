DESCRIPTION = "DEMOS"
SECTION = "multimedia"
LICENSE = "CLOSED"
LICENSE_FLAGS = "Synaptics-EULA"

DEPENDS = " \
    gstreamer1.0-meta-base \
    gstreamer1.0-plugins-base \
    gstreamer1.0-plugins-good \
    gstreamer1.0-plugins-bad \
    gstreamer1.0-plugins-ugly \
    ffmpeg \
    libdrm \
"

SRC_URI = "${SYNA_SRC_DEMOS}"

S = "${WORKDIR}/${SYNA_SOURCE_PREFIX}/application/demos/codec-demo"

SRCREV = "${SYNA_SRCREV_DEMOS}"

PV = "${ASTRA_VERSION}+git${SRCPV}"

rootdir = "/home/root"

COMPATIBLE_MACHINE = "syna"

CC:remove = "-Werror=format-security"
CXX:remove = "-Werror=format-security"

TARGET_HW:dolphin = "dolphin"
TARGET_HW:platypus = "platypus"
TARGET_HW:myna2 = "myna2"
TARGET_HW:klamath = "klamath"

V4L2_ENABLE = "1"
V4L2_ENABLE:myna2 = "0"
V4L2_ENABLE:klamath = "0"

CLEANBROKEN = "1"

inherit meson pkgconfig

EXTRA_OEMESON = ""

EXTRA_OECONF += " -Dcurr_mach=${TARGET_HW} -Dv4l2_enable=${V4L2_ENABLE}"

do_install () {
    install -d ${D}${bindir}
    for i in \
        codec-demo;
    do
        install -m 0755 ${B}/$i ${D}${bindir}
    done

    install -d ${D}${rootdir}
    install ${THISDIR}/files/.profile ${D}${rootdir}/.profile
}

PACKAGES = " \
    synasdk-demos \
    synasdk-demos-dev \
    synasdk-demos-dbg \
"

FILES:${PN} = " \
    ${bindir}/* \
    ${libdir}/*.so \
    ${rootdir}/.profile \
"

FILES:${PN}-dev = " \
    ${includedir}/syna/* \
"

FILES:{$PN}-dbg = " \
    ${bindir}/.debug \
    ${libdir}/.debug \
"
INSANE_SKIP:${PN} += "ldflags"

