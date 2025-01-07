DESCRIPTION = "Synaptics SDK sysctl configurations"
SECTION = "devtools"
LICENSE = "CLOSED"
LICENSE_FLAGS = "Synaptics-EULA"
PR = "r2"

SRC_URI = "file://synasdk-sysctl.conf"

do_install() {
    install -d ${D}${sysconfdir}/sysctl.d
    install -m 0644 ${WORKDIR}/synasdk-sysctl.conf ${D}${sysconfdir}/sysctl.d/sysctl.conf
}
