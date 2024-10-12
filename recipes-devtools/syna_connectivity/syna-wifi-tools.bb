DESCRIPTION = "Synaptics CSI Tools"
SECTION = "devtools"
LICENSE = "CLOSED"
LICENSE_FLAGS = "Synaptics-EULA"

PR = "r1"

SRC_URI = "file://syna_csi_retriever \
           file://syna_csi_decoder"

do_install () {
    install -d ${D}/${sbindir}
    install -m 0755 ${WORKDIR}/syna_csi_retriever ${D}/${sbindir}/syna_csi_retriever
    install -m 0755 ${WORKDIR}/syna_csi_decoder ${D}/${sbindir}/syna_csi_decoder
}

INSANE_SKIP:${PN} += "already-stripped"

FILES:${PN} = "${sbindir}/syna_csi_retriever \
               ${sbindir}/syna_csi_decoder"
