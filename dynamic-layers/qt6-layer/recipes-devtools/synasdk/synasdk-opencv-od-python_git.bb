SUMMARY = "Synaptics OpenCV Objectdetection python based Camera Preview App"
SECTION = "multimedia"
LICENSE = "Apache-2.0"

dedir = "/home/root/demos/scripts/"

SRC_URI = "${SYNA_SRC_DEMOS}"

SRCREV = "${SYNA_SRCREV_DEMOS}"

PV = "${ASTRA_VERSION}+git${SRCPV}"

S = "${WORKDIR}/${SYNA_SOURCE_PREFIX}/application/demos/scripts"

LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=4158a261ca7f2525513e31ba9c50ae98"

do_install () {
    install -d ${D}${dedir}

    if [ "${MACHINE}" = "sl1680" ]; then
        install -d ${D}${dedir}
        install -m 0755 ${S}/sl1680_od_cam.py ${D}${dedir}/
    fi

}

FILES:${PN} = " \
    ${dedir} \
"
