DESCRIPTION = "Wi-Fi connection automation script"
LICENSE = "CLOSED"
SRC_URI = "file://wlan_start \
    file://ssid.py"

S = "${WORKDIR}"

RDEPENDS_${PN} += "bash"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/wlan_start ${D}${bindir}/wlan_start
    install -m 0755 ${S}/ssid.py ${D}${bindir}/ssid.py
}
