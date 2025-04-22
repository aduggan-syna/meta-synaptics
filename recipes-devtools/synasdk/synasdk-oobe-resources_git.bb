DESCRIPTION = "OOBE Resources"
SECTION = "multimedia"
LICENSE = "CLOSED"
LICENSE_FLAGS = "Synaptics-EULA"

SRC_URI = "${SYNA_SRC_DEMOS}"

S = "${WORKDIR}/${SYNA_SOURCE_PREFIX}/application/demos/oobe-resources"

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
    install -d ${D}/etc
    install -d ${D}${rootdir}/demos/videos/mp4
    install -d ${D}${rootdir}/demos/videos/h264
    install -d ${D}${rootdir}/demos/configs
    install -d ${D}${rootdir}/demos/default-slideshow-images
    install -d ${D}${rootdir}/demos/user-slideshow-images

    cp -r ${S}/default-slideshow-images/* ${D}${rootdir}/demos/default-slideshow-images/
    cp ${S}/config_files/powervr.ini ${D}/etc/
    cp ${S}/videos/mp4/* ${D}${rootdir}/demos/videos/mp4/
    ${@bb.utils.contains('MACHINE', 'sl1620', 'cp ${S}/videos/h264/sl1620/* ${D}${rootdir}/demos/videos/h264/; install -d ${D}/usr/share/synap/models; cp -r ${S}/models/object_detection ${D}/usr/share/synap/models/; cp ${S}/config_files/syna_capability_demo_sl1620_config.txt ${D}/${rootdir}/demos/configs/', '', d)}
    ${@bb.utils.contains('MACHINE', 'sl1640', 'cp ${S}/videos/h264/sl1640/* ${D}${rootdir}/demos/videos/h264/; cp ${S}/config_files/syna_capability_demo_sl1640_config.txt ${D}/${rootdir}/demos/configs/', '', d)}
    ${@bb.utils.contains('MACHINE', 'sl1680', 'cp ${S}/videos/h264/sl1680/* ${D}${rootdir}/demos/videos/h264/; cp ${S}/config_files/syna_capability_demo_sl1680_config.txt ${D}/${rootdir}/demos/configs/', '', d)}
}

PACKAGES = " \
    synasdk-oobe-resources \
"

FILES:${PN} = " \
    /etc/* \
    ${rootdir}/demos/videos/* \
    ${rootdir}/demos/configs/* \
    ${rootdir}/demos/user-slideshow-images \
    ${rootdir}/demos/default-slideshow-images/* \
"
FILES:${PN} += "${@bb.utils.contains('MACHINE', 'sl1620', '/usr/share/synap/models/*', '', d)}"
