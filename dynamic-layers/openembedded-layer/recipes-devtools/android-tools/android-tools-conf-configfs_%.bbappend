FILESEXTRAPATHS:prepend := "${THISDIR}/android-tools-conf-configfs:"

SRC_URI += " file://android-gadget-setup \
             file://android-gadget-start \
             file://99-udc-monitor.rules"

PROVIDES:remove = "android-tools-conf"
RPROVIDES:${PN}:remove = "android-tools-conf"

do_install:append() {
    install -d ${D}${sysconfdir}/udev/rules.d
    install -m 0644 ${WORKDIR}/99-udc-monitor.rules ${D}${sysconfdir}/udev/rules.d/99-udc-monitor.rules
}

FILES:${PN} += " \
    ${sysconfdir}/udev/rules.d/99-udc-monitor.rules \
"
