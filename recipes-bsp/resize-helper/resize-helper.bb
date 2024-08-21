SUMMARY = "Resize root filesystem to fit available disk space"
DESCRIPTION = "Resize root filesystem to fit available disk space"
SECTION = "admin"

LICENSE = "BSD-2-Clause"

SRC_URI = " \
	file://resize-helper.service;name=resize-helper-service \
	file://resize-helper;name=resize-helper-script \
"

inherit systemd

RDEPENDS:${PN} += "e2fsprogs-resize2fs gptfdisk parted util-linux udev"

do_install() {
	install -d ${D}${systemd_system_unitdir}
	install -m 0644 ${WORKDIR}/resize-helper.service ${D}${systemd_system_unitdir}
	install -d ${D}${sbindir}
	install -m 0755 ${WORKDIR}/resize-helper ${D}${sbindir}
}

SYSTEMD_SERVICE:${PN} = "resize-helper.service"

FILES:${PN} = " \
	${systemd_system_unitdir}/resize-helper.service \
	${sbindir}/resize-helper"