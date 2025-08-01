DESCRIPTION = "Synaptics Wifi Tools"
SECTION = "devtools"
LICENSE = "CLOSED"
LICENSE_FLAGS = "Synaptics-EULA"

PR = "r1"

SRC_URI = " \
   ${SYNA_SRC_LINUX_SYSROOT} \
"

SRCREV = "${SYNA_SRCREV_LINUX_SYSROOT}"

PV = "${ASTRA_VERSION}+git${SRCPV}"

S = "${WORKDIR}/${SYNA_SOURCE_PREFIX}"

do_install () {
    install -d ${D}/${sbindir}
    install -m 0755 ${S}/sysroot/linux-baseline/data/wifi_tools/syna_csi_retriever ${D}/${sbindir}/syna_csi_retriever
    install -m 0755 ${S}/sysroot/linux-baseline/data/wifi_tools/syna_csi_decoder ${D}/${sbindir}/syna_csi_decoder
    install -m 0755 ${S}/sysroot/linux-baseline/data/wifi_tools/dhd.wet ${D}/${sbindir}/dhd.wet
    install -m 0755 ${S}/sysroot/linux-baseline/data/wifi_tools/dhd.29 ${D}/${sbindir}/dhd.29
    install -m 0755 ${S}/sysroot/linux-baseline/data/wifi_tools/dhd.482 ${D}/${sbindir}/dhd.482
    install -m 0755 ${S}/sysroot/linux-baseline/data/wifi_tools/dhd.517 ${D}/${sbindir}/dhd.517
    install -m 0755 ${S}/sysroot/linux-baseline/data/wifi_tools/wl.wet ${D}/${sbindir}/wl.wet
    install -m 0755 ${S}/sysroot/linux-baseline/data/wifi_tools/wl.29 ${D}/${sbindir}/wl.29
    install -m 0755 ${S}/sysroot/linux-baseline/data/wifi_tools/wl.482 ${D}/${sbindir}/wl.482
    install -m 0755 ${S}/sysroot/linux-baseline/data/wifi_tools/wl.517 ${D}/${sbindir}/wl.517
}

INSANE_SKIP:${PN} += "already-stripped"

FILES:${PN} = "${sbindir}/syna_csi_retriever \
               ${sbindir}/syna_csi_decoder \
               ${sbindir}/dhd.wet \
               ${sbindir}/dhd.29 \
               ${sbindir}/dhd.482 \
               ${sbindir}/dhd.517 \
               ${sbindir}/wl.wet \
               ${sbindir}/wl.29 \
               ${sbindir}/wl.482 \
               ${sbindir}/wl.517"
