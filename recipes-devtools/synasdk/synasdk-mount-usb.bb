DESCRIPTION = "Synaptics Mount usb drive"
SECTION = "devtools"
LICENSE = "CLOSED"
LICENSE_FLAGS = "Synaptics-EULA"

PR = "r1"

SRC_URI = " \
    file://99-usb-automount.rules \
    file://mount-usb.sh \
    file://umount-usb.sh \
"

do_install () {
    install -D -p -m 0644 ${WORKDIR}/99-usb-automount.rules ${D}${sysconfdir}/udev/rules.d/99-usb-automount.rules
    install -d ${D}/${bindir}
    install -m 0755 ${WORKDIR}/mount-usb.sh ${D}${bindir}/mount-usb.sh
    install -m 0755 ${WORKDIR}/umount-usb.sh ${D}${bindir}/umount-usb.sh
}

FILES:${PN} = " \
    ${sysconfdir}/udev/rules.d/99-usb-automount.rules \
    ${bindir}/mount-usb.sh \
    ${bindir}/umount-usb.sh \
"
