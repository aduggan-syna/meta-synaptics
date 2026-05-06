DESCRIPTION = "Wi-Fi connection GUI"
LICENSE = "CLOSED"
SRC_URI = "file://app.py \
	   file://app_qt6.py \
           file://connection.py \
           file://scan_wifi.py \
           file://style.qss \
           file://known_networks.py \
           file://known_networks_qt6.py \
           file://connection_information.py \
           file://connection_information_qt6.py"

S = "${WORKDIR}"

do_install() {
    install -d ${D}${bindir}
    install -d ${D}/usr/bin

    # Choose correct app based on QT version
    if [ "${QT_MAJOR}" = "5" ]; then
    	install -m 0755 ${S}/app.py ${D}${bindir}/app.py
    	install -m 0755 ${S}/known_networks.py ${D}${bindir}/known_networks.py
    	install -m 0755 ${S}/connection_information.py ${D}${bindir}/connection_information.py
    else
    	install -m 0755 ${S}/app_qt6.py ${D}${bindir}/app.py
    	install -m 0755 ${S}/known_networks_qt6.py ${D}${bindir}/known_networks.py
    	install -m 0755 ${S}/connection_information_qt6.py ${D}${bindir}/connection_information.py
    fi
    install -m 0755 ${S}/connection.py ${D}${bindir}/connection.py
    install -m 0755 ${S}/scan_wifi.py ${D}${bindir}/scan_wifi.py
    install -m 0755 ${S}/style.qss ${D}${bindir}/style.qss
}
