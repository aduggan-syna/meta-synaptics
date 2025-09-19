DESCRIPTION = "Wi-Fi connection automation script"
LICENSE = "CLOSED"
SRC_URI = "file://wlan_start \
            file://ssid.py \
            file://wlan_kill \
            file://wlan_start.service \
            file://wlan_dns \
            file://dns.service"

S = "${WORKDIR}"

do_install() {
    # Create the necessary directories
    install -d ${D}${bindir}
    install -d ${D}/usr/bin

    # Install the main scripts
    install -m 0755 ${S}/wlan_start ${D}${bindir}/wlan_start
    install -m 0755 ${S}/ssid.py ${D}${bindir}/ssid.py
    install -m 0755 ${S}/wlan_kill ${D}${bindir}/wlan_kill
    install -m 0755 ${S}/wlan_dns ${D}${bindir}/wlan_dns
    # Install the service file to the systemd directory
    install -d ${D}${sysconfdir}/systemd/system/
    install -m 0644 ${WORKDIR}/wlan_start.service ${D}${sysconfdir}/systemd/system/wlan_start.service
    install -m 0644 ${WORKDIR}/dns.service ${D}${sysconfdir}/systemd/system/dns.service
}

SYSTEMD_SERVICE_${PN} = "wlan_start.service dns.service"

inherit systemd

# Automatically enable the service on installation
SYSTEMD_AUTO_ENABLE = "enable"

# Automatically start the service on boot
SYSTEMD_AUTO_START = "1"

FILES_${PN} += "${systemd_system_unitdir}/wlan_start.service ${systemd_system_unitdir}/dns.service"
