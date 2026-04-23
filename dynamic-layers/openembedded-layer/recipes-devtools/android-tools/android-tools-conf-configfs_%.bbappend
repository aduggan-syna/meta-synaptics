FILESEXTRAPATHS:prepend := "${THISDIR}/android-tools-conf-configfs:"

SRC_URI += " file://android-gadget-setup \
             file://android-gadget-start \
             file://android-gadget-cleanup \
             file://android-gadget-setup.machine.example \
             file://gadget-common.sh \
             file://99-udc-monitor.rules"

PROVIDES:remove = "android-tools-conf"
RPROVIDES:${PN}:remove = "android-tools-conf"

do_install:append() {
    install -d ${D}${sysconfdir}/udev/rules.d
    install -d ${D}${sysconfdir}
    install -d ${D}${bindir}

    install -m 0755 ${WORKDIR}/android-gadget-cleanup ${D}${bindir}/
    install -m 0755 ${WORKDIR}/gadget-common.sh ${D}${bindir}/android-gadget-common.sh
    install -m 0644 ${WORKDIR}/android-gadget-setup.machine.example ${D}${sysconfdir}/android-gadget-setup.machine
    install -m 0644 ${WORKDIR}/99-udc-monitor.rules ${D}${sysconfdir}/udev/rules.d/99-udc-monitor.rules
}

FILES:${PN} += " \
    ${bindir}/android-gadget-cleanup \
    ${bindir}/android-gadget-common.sh \
    ${sysconfdir}/android-gadget-setup.machine.example \
    ${sysconfdir}/udev/rules.d/99-udc-monitor.rules \
"
