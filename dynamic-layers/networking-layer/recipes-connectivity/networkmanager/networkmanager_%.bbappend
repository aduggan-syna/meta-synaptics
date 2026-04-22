ALTERNATIVE_PRIORITY[resolv-conf] = "20"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += "file://NetworkManager.conf"
SRC_URI += "file://99-disable_wifi_info_debug.rules"

do_install:append() {
    install -d ${D}${sysconfdir}/NetworkManager
    install -m 0644 ${WORKDIR}/NetworkManager.conf ${D}${sysconfdir}/NetworkManager/NetworkManager.conf

    install -d ${D}${sysconfdir}/udev/rules.d
    install -m 0644 ${WORKDIR}/99-disable_wifi_info_debug.rules ${D}${sysconfdir}/udev/rules.d/

    # Mask the SysV-generated network-manager.service so that
    # systemd-sysv-generator does not auto-start the /etc/init.d/network-manager
    # script and race with NetworkManager.service on first boot
    # (causes "D-Bus service already taken" on freshly flashed images).
    # We mask via a /dev/null symlink rather than deleting the init.d script,
    # because the upstream networkmanager postinst invokes update-rc.d on it.
    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        install -d ${D}${sysconfdir}/systemd/system
        ln -sf /dev/null ${D}${sysconfdir}/systemd/system/network-manager.service
    fi
}

FILES:${PN} += "${sysconfdir}/systemd/system/network-manager.service"
