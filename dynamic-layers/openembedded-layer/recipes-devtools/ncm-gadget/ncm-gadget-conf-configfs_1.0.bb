DESCRIPTION = "NCM (Network Control Model) USB Gadget Configuration"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = " \
    file://ncm-gadget-setup \
    file://ncm-gadget-cleanup \
    file://gadget-common.sh \
    file://ncm-gadget-start \
    file://ncm-gadget-dhcp \
    file://ncm-gadget-setup.machine.example \
    file://99-ncm-udc-monitor.rules \
    file://ncm-gadget-setup.service \
    file://ncm-gadget-dhcp.service \
"

inherit systemd

SYSTEMD_SERVICE:${PN} = "ncm-gadget-setup.service ncm-gadget-dhcp.service"

do_install() {
    install -d ${D}${sysconfdir}/udev/rules.d
    install -d ${D}${sysconfdir}
    install -d ${D}${bindir}
    install -d ${D}${systemd_system_unitdir}
    
    install -m 0755 ${WORKDIR}/ncm-gadget-setup ${D}${bindir}/
    install -m 0755 ${WORKDIR}/ncm-gadget-cleanup ${D}${bindir}/
    install -m 0755 ${WORKDIR}/gadget-common.sh ${D}${bindir}/ncm-gadget-common.sh
    install -m 0755 ${WORKDIR}/ncm-gadget-start ${D}${bindir}/
    install -m 0755 ${WORKDIR}/ncm-gadget-dhcp ${D}${bindir}/
    install -m 0644 ${WORKDIR}/ncm-gadget-setup.machine.example ${D}${sysconfdir}/ncm-gadget-setup.machine
    install -m 0644 ${WORKDIR}/99-ncm-udc-monitor.rules ${D}${sysconfdir}/udev/rules.d/
    install -m 0644 ${WORKDIR}/ncm-gadget-setup.service ${D}${systemd_system_unitdir}/
    install -m 0644 ${WORKDIR}/ncm-gadget-dhcp.service ${D}${systemd_system_unitdir}/
}

FILES:${PN} += " \
    ${bindir}/ncm-gadget-setup \
    ${bindir}/ncm-gadget-cleanup \
    ${bindir}/ncm-gadget-common.sh \
    ${bindir}/ncm-gadget-start \
    ${bindir}/ncm-gadget-dhcp \
    ${sysconfdir}/ncm-gadget-setup.machine.example \
    ${sysconfdir}/udev/rules.d/99-ncm-udc-monitor.rules \
    ${systemd_system_unitdir}/ncm-gadget-setup.service \
    ${systemd_system_unitdir}/ncm-gadget-dhcp.service \
    ${datadir}/doc/${PN}/* \
"
