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
}

INSANE_SKIP:${PN} += "already-stripped"

FILES:${PN} = "${sbindir}/syna_csi_retriever \
               ${sbindir}/syna_csi_decoder"
