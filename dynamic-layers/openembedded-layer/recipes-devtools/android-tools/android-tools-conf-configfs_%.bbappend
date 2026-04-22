FILESEXTRAPATHS:prepend := "${THISDIR}/android-tools-conf-configfs:"

SRC_URI += " file://android-gadget-setup \
             file://android-gadget-start \
             file://android-gadget-cleanup \
             file://gadget-common.sh \
             file://99-udc-monitor.rules"

PROVIDES:remove = "android-tools-conf"
RPROVIDES:${PN}:remove = "android-tools-conf"

do_install:append() {
    install -d ${D}${sysconfdir}/udev/rules.d
    install -d ${D}${bindir}

    install -m 0755 ${WORKDIR}/android-gadget-cleanup ${D}${bindir}/
    install -m 0755 ${WORKDIR}/gadget-common.sh ${D}${bindir}/android-gadget-common.sh
    install -m 0644 ${WORKDIR}/99-udc-monitor.rules ${D}${sysconfdir}/udev/rules.d/99-udc-monitor.rules
}

FILES:${PN} += " \
    ${bindir}/android-gadget-cleanup \
    ${bindir}/android-gadget-common.sh \
    ${sysconfdir}/udev/rules.d/99-udc-monitor.rules \
"
