DESCRIPTION = "DEMOS Videos"
SECTION = "multimedia"
LICENSE = "CLOSED"
LICENSE_FLAGS = "Synaptics-EULA"

SRC_URI = "${SYNA_SRC_DEMOS}"

S = "${WORKDIR}/${SYNA_SOURCE_PREFIX}/application/demos/videos"

SRCREV = "${SYNA_SRCREV_DEMOS}"

PV = "${ASTRA_VERSION}+git${SRCPV}"

rootdir = "/home/root"

COMPATIBLE_MACHINE = "syna"

CC:remove = "-Werror=format-security"
CXX:remove = "-Werror=format-security"

TARGET_HW:dolphin = "dolphin"
TARGET_HW:platypus = "platypus"
TARGET_HW:myna2 = "myna2"

CLEANBROKEN = "1"

do_install () {
    install -d ${D}${rootdir}/demos/videos
    install ${S}/* ${D}${rootdir}/demos/videos/
}

PACKAGES = " \
    synasdk-demos-videos \
"

FILES:${PN} = " \
    ${rootdir}/demos/videos/* \
"

